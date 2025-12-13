@Suppress("unused")
class Day04 : AbstractDay(4) {

    private val grid = inputCharArray()
    private val rolls = cartesianProduct(grid.indices.toList(), grid[0].indices.toList())
        .filter { (x,y) -> grid[x][y]=='@' }.map{ (x, y) -> Point2D(x,y)}

    override fun partOne(): Any = rolls.count { adjacentDiagPoints2D.count { p -> p.add(it) in rolls } < 4 }

    private tailrec fun cascadeRemove(acc : Int, rollsSet : Set<Point2D>) : Int {
        val removed = rollsSet.filter { adjacentDiagPoints2D.count { p -> p.add(it) in rollsSet } < 4 }
        return if(removed.isEmpty()) acc
                else cascadeRemove(acc+removed.size, rollsSet.minus(removed))
    }

    override fun partTwo(): Any = cascadeRemove(0, rolls.toSet())
}