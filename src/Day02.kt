@Suppress("unused")
class Day02 : AbstractDay(2) {

    private val ranges = inputString().split(",")

    private fun isNotValid(id : String) : Boolean  = id.substring(0, id.length/2 ) == id.substring(id.length/2)

    private fun isNotValid2(id : String) : Boolean  = (1..id.length/2).any { id.chunked(it).toSet().size==1 }

    private fun sumInvalidIDS (range : String, checker: (String) -> Boolean) : Long {
        val split = range.split("-").map { it.toLong() }
        return (split.first()..split.last()).filter { checker(it.toString()) }.sum()
    }

    override fun partOne(): Any = ranges.sumOf { sumInvalidIDS(it) { isNotValid(it) } }

    override fun partTwo(): Any  = ranges.sumOf { sumInvalidIDS(it) { isNotValid2(it) } }
}