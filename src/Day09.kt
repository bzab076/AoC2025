import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Suppress("unused")
class Day09 : AbstractDay(9) {

    private val tiles = inputLines().flatMap { it.extractLongPairs() }
    private val pairs = cartesianProduct(tiles,tiles).filter { (a,b) -> a!=b }

    override fun partOne(): Any = pairs.maxOf { (a, b) -> (abs(a.first - b.first) + 1) * (abs(a.second - b.second) + 1) }

    override fun partTwo(): Any = pairs.maxOf { (a, b) -> areaInPolygon(a, b) }

    private fun areaInPolygon(corner1 : Pair<Long,Long>, corner2 : Pair<Long,Long>) : Long {

        val xs = tiles.map{it.first}.filter { x -> x >= min(corner1.first,corner2.first) && x <= max(corner1.first,corner2.first) }
        val ys = tiles.map{it.second}.filter { y -> y >= min(corner1.second, corner2.second) && y <= max(corner1.second, corner2.second) }

        // try all (x,y) combinations that lie within the rectangle
        // inefficient but working
        return if (cartesianProduct(xs,ys).all { p-> isPointInPolygon(p) }) (abs(corner1.first - corner2.first) +1) * (abs(corner1.second - corner2.second) +1) else  0L
    }

    fun isPointInPolygon(pt: Pair<Long, Long>): Boolean {
        var inside = false
        val noOfVertices = tiles.size

        var j = noOfVertices - 1  // previous vertex index

        for (i in 0 until noOfVertices) {
            val pi = tiles[i]
            val pj = tiles[j]

            // Check if point is on an edge
            if (isPointOnSegment(pj, pi, pt)) return true

            // Check for ray intersection
            val intersects = ( (pi.second > pt.second) != (pj.second > pt.second) ) &&
                    (pt.first < (pj.first - pi.first) * (pt.second - pi.second) / (pj.second - pi.second) + pi.first)

            if (intersects) inside = !inside
            j = i
        }

        return inside
    }

    fun isPointOnSegment(a: Pair<Long, Long>, b: Pair<Long, Long>, p: Pair<Long, Long>): Boolean {
        val cross = (p.second - a.second) * (b.first - a.first) - (p.first - a.first) * (b.second - a.second)
        if (abs(cross) > 1e-9) return false  // not collinear

        val dot = (p.first - a.first) * (b.first - a.first) + (p.second - a.second) * (b.second - a.second)
        if (dot < 0) return false

        val lenSq = (b.first - a.first)*(b.first - a.first) + (b.second - a.second)*(b.second - a.second)
        return dot <= lenSq
    }
}