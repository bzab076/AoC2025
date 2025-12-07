@Suppress("unused")
class Day07 : AbstractDay(7) {

    private val grid = inputCharArray()
    private val start = Point2D(0,grid[0].indexOf('S'))

    private fun inGrid(p : Point2D) : Boolean = p.x in grid.indices && p.y in grid[0].indices

    private tailrec fun splitBeams1(beams : Set<Point2D>, acc : Int) : Pair<Set<Point2D>, Int> {

        val newBeams = beams.map{p -> p.add(Point2D(1,0))}
            .flatMap { p ->
                if(inGrid(p) && grid[p.x][p.y] == '^') listOf(p.add(Point2D(0,1)), p.add(Point2D(0, -1)))
                else listOf(p)
            }
            .filter{inGrid(it)}

        val splits = newBeams.size - beams.size  // number of new splits in this iteration

        return if(newBeams.isEmpty())  beams to acc else splitBeams1(newBeams.toSet(), acc+splits)
    }

    private tailrec fun splitBeams2(beams : List<Pair<Point2D, Long>>) : List<Pair<Point2D, Long>> {

        // here we work with pairs (head of beam, count)
        val newBeams = beams.map{(p,c) -> p.add(Point2D(1,0)) to c}
            .flatMap { (p, c) ->
                if(inGrid(p) && grid[p.x][p.y] == '^') listOf(p.add(Point2D(0,1)) to c, p.add(Point2D(0, -1)) to c)
                else listOf(p to c)
            }
            .filter{(p, _) -> inGrid(p)}

        val groupedBeams = newBeams.groupBy { it.first }.map { (k,v) -> k to v.sumOf { it.second } }

        return if(newBeams.isEmpty()) beams  else splitBeams2(groupedBeams)
    }

    override fun partOne(): Any = splitBeams1(setOf(start), 0).second

    override fun partTwo(): Any = splitBeams2(listOf(start to 1L)).sumOf { it.second }
}