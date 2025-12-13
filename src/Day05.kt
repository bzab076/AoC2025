import java.lang.Long.max
import java.lang.Long.min

@Suppress("unused")
class Day05 : AbstractDay(5) {

    private val ranges = inputLines().take(181).map { it.split("-") }.map { it.first().toLong() to it.last().toLong() }
    private val ingredients  = inputLines().drop(182).map { it.toLong() }

    private fun distinctRanges(myRanges : List<Pair<Long,Long>>) : Set<Pair<Long,Long>> {

        val result = mutableSetOf<Pair<Long, Long>>()

        myRanges.forEach { range ->
            val frange = result.filter { range.first in (it.first..it.second) }
            val srange = result.filter { range.second in (it.first..it.second) }

            if (frange.isNotEmpty() && srange.isNotEmpty()) {
                val rng1 = frange.first()
                val rng2 = srange.first()
                result.remove(rng1)
                result.remove(rng2)
                result.add(min(range.first, rng1.first) to max(range.second, rng2.second))
            }
            else if(frange.isNotEmpty()) {
                val rng = frange.first()
                result.remove(rng)
                result.add(min(range.first, rng.first) to max(range.second, rng.second))
            }
            else if(srange.isNotEmpty()) {
                val rng = srange.first()
                result.remove(rng)
                result.add(min(range.first, rng.first) to max(range.second, rng.second))
            }
            else {
                result.add(range)
            }
        }

        return result.toSet()
    }

    override fun partOne(): Any = ingredients.count { ingr -> ranges.any { ingr in (it.first..it.second) } }

    override fun partTwo(): Any = distinctRanges(ranges.sortedBy { it.first }).sumOf { it.second - it.first + 1 }
}