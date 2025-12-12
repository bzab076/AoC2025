@Suppress("unused")
class Day12 : AbstractDay(12) {


    private val shapes : List<List<String>> = inputLines().take(30).chunked(5).map { it.drop(1).dropLast(1) }
    private val regions : List<Pair<Pair<Int,Int>, List<Int>>> = inputLines().drop(30)
        .map { it.split(": ").zipWithNext { k, v -> Pair(k.extractNumbers().first(), k.extractNumbers().last()) to v.extractNumbers() }.single() }

    private fun countParts(shape: List<String>) : Int = shape.sumOf { it.toCharArray().count { it=='#' } }

    override fun partOne(): Any =
        regions.filter { (size, reqs) -> size.first*size.second >= reqs.mapIndexed { i, v -> v*countParts(shapes[i]) }.sum()}
                .count{(size, reqs) -> size.first/3 * size.second/3 >= reqs.sum()}

    override fun partTwo(): Any {
        return "That is it for this year!!!"
    }
}