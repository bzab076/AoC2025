@Suppress("unused")
class Day08 : AbstractDay(8) {

    private val jboxes = inputLines().map{it.extractNumbers()}.map{ Point3D(it[0], it[1], it[2]) }

    private val dists = cartesianProduct(jboxes, jboxes)
        .filter { (x,y) -> x.abs() > y.abs() }
        .map { p -> p to p.first.sqrdist(p.second) }
        .sortedBy { it.second }

    private fun groupBoxes (connectedPairs : Int) : Pair<List<Set<Point3D>>, Long> {

        val resultMap = mutableMapOf<Point3D, Set<Point3D>>()

        dists.take(connectedPairs).map{it.first}.forEach { currentPair ->

            val box1 = resultMap.toList().find { it.second.contains(currentPair.first) }
            val box2 = resultMap.toList().find { it.second.contains(currentPair.second) }

            if(box1!=null && box2!=null) {
                val newSet = box1.second.union(box2.second)
                resultMap.remove(box1.first)
                resultMap.remove(box2.first)
                resultMap.put(box1.first, newSet)
            }
            else if(box1!=null) {
                val newSet = box1.second.plus(currentPair.second)
                resultMap.replace(box1.first, newSet)
            }
            else if(box2!=null) {
                val newSet = box2.second.plus(currentPair.first)
                resultMap.replace(box2.first, newSet)
            }
            else {
                resultMap.put(currentPair.first, currentPair.toList().toSet())
            }

            // part 2 result
            if(resultMap.values.sumOf { it.size }==jboxes.size)
                return resultMap.values.toList() to currentPair.first.x.toLong() * currentPair.second.x.toLong()
        }

        // part 1 result
        return resultMap.values.toList() to 0L
    }

    override fun partOne(): Any = groupBoxes(1000).first.map { it.size }.sorted().takeLast(3).fold(1){acc, v -> acc*v}

    override fun partTwo(): Any = groupBoxes(10000).second
}