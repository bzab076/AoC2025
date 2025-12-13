@Suppress("unused")
class Day03 : AbstractDay(3) {

    private fun joltage(size : Int, line : String) : Long {

        if(line.length<size) return 0L
        if(size==1) return line.toCharArray().maxOf { it.digitToInt().toLong() }

        var leading = 9
        while(leading>=0) {
            val digit = '0' + leading
            val index = line.indexOfFirst { it==digit }
            if(index>=0 && line.length-index>=size) {
                val subjoltage = joltage(size-1, line.substring(index+1)).toString()
                return "$digit$subjoltage".toLong()
            }
            leading--
        }

        return 0L
    }

    override fun partOne(): Any = inputLines().sumOf { joltage(2,it)}

    override fun partTwo(): Any = inputLines().sumOf { joltage(12,it)}
}



