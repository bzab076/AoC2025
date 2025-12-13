@Suppress("unused")
class Day01 : AbstractDay(1) {

    private fun password(partTwo : Boolean) : Int {

        var dial = 50
        var result = 0

        inputLines().forEach { line ->

            val move = Integer.parseInt(line.substring(1))
            val olddial = dial

            dial = if(line.startsWith("R")) (dial + move).mod(100)
                   else (dial - move).mod(100)

            if(dial==0) result++

            if(partTwo) {
                if(olddial!=0 && dial!=0) {
                    if(olddial>dial && line.startsWith("R")) result++
                    if(olddial<dial && line.startsWith("L")) result++
                }

                result+=move.div(100)
            }
        }

        return result
    }

    override fun partOne(): Any = password(false)

    override fun partTwo(): Any = password(true)
}