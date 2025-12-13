
import kotlin.math.min

/**
 * Solve: minimize sum(x) subject to A x = b, x >= 0 integer
 * Optimizations:
 *  - variable ordering by column sum (most-constraining first)
 *  - tight per-variable upper bounds and iterative tightening
 *  - capacity look-ahead pruning
 *  - memoization of failed (col, rhs) states
 *  - greedy initial feasible solution to initialize bestSum
 *
 * Usage: call solve(A, b, timeLimitMs). Returns IntArray or null if no solution found in time.
 */
object IntMinSumSolver {

    fun solve(Ain: Array<IntArray>, bin: IntArray, timeLimitMs: Long = 60_000L): IntArray? {
        val m = Ain.size
        val n = Ain[0].size

        // variable ordering (columns with larger total coefficients first)
        val colScore = IntArray(n) { j -> (0 until m).sumOf { Ain[it][j] } }
        val order = (0 until n).sortedByDescending { colScore[it] }.toIntArray()

        // reorder A accordingly to A2 (m x n)
        val A2 = Array(m) { i -> IntArray(n) { j -> Ain[i][order[j]] } }
        val b = bin.copyOf()

        // initial per-variable upper bounds
        val maxInit = IntArray(n) { col ->
            var ub = Int.MAX_VALUE
            for (i in 0 until m) {
                val c = A2[i][col]
                if (c > 0) ub = min(ub, b[i] / c)
            }
            if (ub == Int.MAX_VALUE) 1000 else ub
        }

        // helper: tighten upper bounds given rhs
        fun tightenMaxVals(maxVals: IntArray, rhs: IntArray) {
            var changed: Boolean
            do {
                changed = false
                for (col in 0 until n) {
                    var ub = maxVals[col]
                    for (i in 0 until m) {
                        val c = A2[i][col]
                        if (c > 0) ub = min(ub, rhs[i] / c)
                    }
                    if (ub < maxVals[col]) { maxVals[col] = ub; changed = true }
                }
            } while (changed)
        }

        // capacity feasibility check for remaining vars
        fun feasibleRemaining(rhs: IntArray, startCol: Int, maxVals: IntArray): Boolean {
            for (i in 0 until m) {
                var cap = 0L
                for (col in startCol until n) cap += A2[i][col].toLong() * maxVals[col].toLong()
                if (rhs[i].toLong() > cap) return false
            }
            return true
        }

        // quick greedy to get initial feasible solution (if exists)
        fun greedyFeasible(): IntArray? {
            val rhs = b.copyOf()
            val sol = IntArray(n)
            for (col in 0 until n) {
                val ub = maxInit[col]
                var chosen = -1
                for (v in 0..ub) {
                    val newRhs = IntArray(m) { i -> rhs[i] - A2[i][col] * v }
                    if (newRhs.any { it < 0 }) break
                    // capacity check with remaining maxInit
                    var ok = true
                    for (i in 0 until m) {
                        var cap = 0L
                        for (c in col + 1 until n) cap += A2[i][c].toLong() * maxInit[c].toLong()
                        if (newRhs[i].toLong() > cap) { ok = false; break }
                    }
                    if (ok) { chosen = v; break }
                }
                if (chosen == -1) return null
                sol[col] = chosen
                for (i in 0 until m) rhs[i] -= A2[i][col] * chosen
            }
            if (rhs.any { it != 0 }) return null
            return sol
        }

        // compute a small lower bound for remaining sum: ceil(sum(rhs)/maxRowsPerVar)
        val maxRowsPerVar = (0 until n).map { col -> (0 until m).sumOf { A2[it][col] } }.maxOrNull() ?: 1
        fun lowerBoundRemainingSum(rhs: IntArray): Int {
            val tot = rhs.sum()
            return if (maxRowsPerVar == 0) if (tot == 0) 0 else Int.MAX_VALUE else (tot + maxRowsPerVar - 1) / maxRowsPerVar
        }

        // prepare search state
        var bestSum = Int.MAX_VALUE
        var bestSol: IntArray? = null
        val cur = IntArray(n)
        val memo = HashSet<Long>() // store hashed failed states
        val startTime = System.currentTimeMillis()

        // pack state into long (hash); collisions possible but acceptable for pruning
        fun packState(col: Int, rhs: IntArray, slack: Int): Long {
            var h = 1469598103934665603L
            h = h xor col.toLong(); h *= 1099511628211L
            h = h xor slack.toLong(); h *= 1099511628211L
            for (v in rhs) { h = h xor v.toLong(); h *= 1099511628211L }
            return h
        }

        // DFS recursion with pruning
        fun dfs(col: Int, rhs: IntArray, sumSoFar: Int, maxVals: IntArray) {
            // time cutoff
            if (System.currentTimeMillis() - startTime > timeLimitMs) throw RuntimeException("time_limit")
            if (sumSoFar >= bestSum) return
            if (col == n) {
                if (rhs.all { it == 0 }) {
                    bestSum = sumSoFar
                    bestSol = cur.copyOf()
                }
                return
            }
            // trivial infeasible checks
            if (rhs.any { it < 0 }) return
            // capacity feasibility
            if (!feasibleRemaining(rhs, col, maxVals)) return
            val lb = lowerBoundRemainingSum(rhs)
            if (sumSoFar + lb >= bestSum) return
            val key = packState(col, rhs, bestSum - sumSoFar)
            if (memo.contains(key)) return

            // tighten local max
            val localMax = maxVals.copyOf()
            for (j in col until n) {
                var ub = localMax[j]
                for (i in 0 until m) {
                    val c = A2[i][j]
                    if (c > 0) ub = min(ub, rhs[i] / c)
                }
                localMax[j] = ub
            }
            tightenMaxVals(localMax, rhs)

            // try small values first (0..ub)
            val coeffs = IntArray(m) { i -> A2[i][col] }
            val ubCol = localMax[col]
            for (v in 0..ubCol) {
                val newRhs = IntArray(m) { i -> rhs[i] - coeffs[i] * v }
                if (newRhs.any { it < 0 }) break
                cur[col] = v
                // update per-var max for future (simple local update)
                val nextMax = localMax.copyOf()
                for (j in (col + 1) until n) {
                    var u = nextMax[j]
                    for (i in 0 until m) {
                        val c = A2[i][j]
                        if (c > 0) u = min(u, newRhs[i] / c)
                    }
                    nextMax[j] = u
                }
                dfs(col + 1, newRhs, sumSoFar + v, nextMax)
                cur[col] = 0
                if (sumSoFar + v >= bestSum) break
            }
            memo.add(key)
        }

        // seed bestSum with greedy solution if available
        val greedy = greedyFeasible()
        if (greedy != null) {
            bestSol = greedy
        }

        // start DFS
        try {
            dfs(0, b.copyOf(), 0, maxInit.copyOf())
        } catch (e: RuntimeException) {
            if (e.message != "time_limit") throw e
            // time limit reached; bestSol holds best found so far (maybe null)
        }

        if (bestSol == null) return null

        // reorder back to original indices
        val result = IntArray(n)
        for (k in 0 until n) result[order[k]] = bestSol[k]
        return result
    }
}
