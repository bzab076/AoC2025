@Suppress("unused")
class Day06 : AbstractDay(6) {

    private val lines = inputLines().dropLast(1).map { "$it " }
    private val cols = getColumns(emptyList(), lines)
    private val ops = inputLines().last().split(" ").map { it.trim() }.filter { it.isNotEmpty() }.map { it[0] }

    private fun operation(op : Char, vals : List<Int>) : Long =
        when (op) {
            '+' -> vals.sumOf { it.toLong() }
            '*' -> vals.map{it.toLong()}.fold(1){ acc, v -> acc*v}
            else -> 0L
        }

    private tailrec fun getColumns (acc : List<List<String>>, lines : List<String>) : List<List<String>> {

        if(lines.isEmpty() || lines.any { it.isEmpty() }) return acc

        val maxlen = lines.map { it.indexOfFirst { it==' ' } }.max()
        val (cols, tails) = lines.map{it.substring(0,maxlen) to  it.substring(maxlen+1)}.unzip()
        return getColumns(acc + listOf(cols), tails)
    }

    private fun transposeNumStrings(vals : List<String>) : List<Int> {
        val maxlen = vals.maxOf { it.length }
        return (0..<maxlen).map { idx -> vals.map { it[idx] }.joinToString("").trim().toInt()}
    }

    override fun partOne(): Any = cols.mapIndexed { idx, v ->  operation(ops[idx],  v.map { it.trim().toInt() } ) }.sum()

    override fun partTwo(): Any = cols.map { transposeNumStrings(it) }.mapIndexed { idx, v -> operation(ops[idx], v) }.sum()
}