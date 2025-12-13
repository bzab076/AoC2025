

@Suppress("unused")
class Day10 : AbstractDay(10) {

    private val lightDiagrams = inputLines().map { str -> str.substring(1, str.indexOfFirst { it==']' }) }
    private val buttons = inputLines().map{ str -> str.substring(str.indexOfFirst { it==']' } + 2, str.indexOfFirst { it=='{' })}
        .map{it.split(")")}.map{l -> l.filter { it.isNotEmpty()}}.map{l -> l.map{it.drop(1)}}.map{l -> l.filter { it.isNotEmpty()}}
        .map{l -> l.map{it.extractNumbers()}}
    private val joltages = inputLines().map{str -> str.substring(str.indexOfFirst { it=='{' } + 1, str.indexOfFirst { it=='}' })}.map{it.extractNumbers()}

    private fun chrIndex(c : Char) : Int = if(c.isDigit()) c - '0' else c - 'a' + 10

    // brute force method to determine button configuration for part 1
    private fun fewestButtons(diagram : String, buttons : List<List<Int>>) : Int {

        for(n in 0..10000000) {

            val nstr = n.toString(buttons.size)
            val idxs = nstr.toCharArray().map { chrIndex(it) }
            val btns = idxs.map { buttons[it]}

            if(areButtonsOK(diagram,btns)) return btns.size
        }

        return -1
    }

    private fun areButtonsOK(diagram : String, buttons : List<List<Int>>) : Boolean {
        val mods = diagram.toCharArray().map { if(it=='#') 1 else 0 }
        val presses = diagram.toCharArray().indices.map{i -> buttons.count { it.contains(i) }}
        return presses.zip(mods).all { (c,m) -> c % 2 == m }
    }

    override fun partOne(): Any = lightDiagrams.zip(buttons).sumOf { (d, b) -> fewestButtons(d, b) }

    override fun partTwo(): Any = buttons.zip(joltages).sumOf { (b,j) -> optimizeButtonsLP(b,j) }

    private fun optimizeButtonsLP(buttonVector : List<List<Int>>, joltageVector : List<Int>) : Int {

        val matrix = joltageVector.indices.map{ btn -> buttonVector.map { if(it.contains(btn)) 1 else 0 }.toIntArray() }.toTypedArray()
        val b = joltageVector.toIntArray()
        val x = IntMinSumSolver.solve(matrix, b, timeLimitMs = 480_000L)
        val result = x!!.sum()

        return result
    }
}