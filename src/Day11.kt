@Suppress("unused")
class Day11 : AbstractDay(11) {

    private val deviceGraph =  inputLines().associate{it.split(": ").zipWithNext { k, v -> k to v.split(" ") }.single()}

    private val cache = mutableMapOf<String, Long>()
    private val cache2 = mutableMapOf<String, Pair<Pair<Long, Long>, Pair<Long, Long>>>()

    private fun paths(startNode : String) : Long {

        if(cache.contains(startNode)) return cache.get(startNode)!!
        if(startNode=="out") return 1L

        val nextNodes = deviceGraph.getOrDefault(startNode, emptyList())
        val result =  nextNodes.sumOf { paths(it)}
        cache[startNode] = result
        return result
    }

    private fun paths2(startNode : String) : Pair<Pair<Long, Long>, Pair<Long, Long>> {

        if(cache2.contains(startNode)) return cache2.get(startNode)!!
        if(startNode=="out") return Pair(1L,0L) to Pair(0L,0L)

        val nextPaths = deviceGraph.getOrDefault(startNode, emptyList()).map { paths2(it) }
        val none = nextPaths.sumOf { (a, _) -> a.first }
        val all = nextPaths.sumOf { (a, _) -> a.second }
        val fft = nextPaths.sumOf { (_, b) -> b.first }
        val dac = nextPaths.sumOf { (_, b) -> b.second }

        val result: Pair<Pair<Long, Long>, Pair<Long, Long>> =
            when (startNode) {
                "fft" -> Pair(0L, all+dac) to Pair(none + fft, 0L)
                "dac" -> Pair(0L, all+fft) to Pair(0L, none+dac)
                else -> Pair(none, all) to Pair(fft, dac)
            }

        cache2[startNode] = result
        return result
    }

    override fun partOne(): Any = paths("you")

    override fun partTwo(): Any = paths2("svr").first.second
}