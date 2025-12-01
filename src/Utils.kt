import java.lang.Exception
import kotlin.collections.chunked
import kotlin.collections.filter
import kotlin.collections.flatMap
import kotlin.collections.isNotEmpty
import kotlin.collections.joinToString
import kotlin.collections.map
import kotlin.collections.plus
import kotlin.collections.zipWithNext
import kotlin.math.abs
import kotlin.ranges.until
import kotlin.text.isNotBlank
import kotlin.text.split
import kotlin.text.toInt
import kotlin.text.toLong
import kotlin.to


/***********************************************
 *   Parsing
 ************************************************/

fun String.extractNumbers() = this.split(Regex("[^-\\d]+")).filter { it.isNotBlank() }.map { it.toInt() }
fun String.extractLongs() = this.split(Regex("[^-\\d]+")).filter { it.isNotBlank() }.map { it.toLong() }
fun String.extractNumberPairs() = this.extractNumbers().chunked(2).flatMap { it.zipWithNext() }
fun String.extractLongPairs() = this.extractLongs().chunked(2).flatMap { it.zipWithNext() }
fun String.extractPoints2D() = this.extractNumberPairs().map { Point2D(it) }

/***********************************************
 *   Math functions
 ************************************************/

fun lcm(a: Int, b: Int): Int {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0 && lcm % b == 0) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}

fun lcm(numbers: List<Int>): Int {
    var result = numbers[0]
    for (i in 1 until numbers.size) {
        result = lcm(result, numbers[i])
    }
    return result
}

fun lcmLong(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}

fun lcmLong(numbers: List<Long>): Long {
    var result = numbers[0]
    for (i in 1 until numbers.size) {
        result = lcmLong(result, numbers[i])
    }
    return result
}

fun gcd(a: Int, b: Int): Int {
    var num1 = a
    var num2 = b
    while (num2 != 0) {
        val temp = num2
        num2 = num1 % num2
        num1 = temp
    }
    return num1
}

fun gcd(numbers: List<Int>): Int {
    require(numbers.isNotEmpty()) { "List must not be empty" }
    var result = numbers[0]
    for (i in 1 until numbers.size) {
        var num1 = result
        var num2 = numbers[i]
        while (num2 != 0) {
            val temp = num2
            num2 = num1 % num2
            num1 = temp
        }
        result = num1
    }
    return result
}


/***********************************************
 *   Tuples
 ************************************************/

operator fun  Pair<Long, Long>.plus(otherPair: Pair<Long, Long>) = this.first + otherPair.first to this.second + otherPair.second

/***********************************************
 *   Collections
 ************************************************/

fun List<String>.concat() = this.joinToString("") { it }

fun <T, U> cartesianProduct(c1: Collection<T>, c2: Collection<U>): List<Pair<T, U>> {
    return c1.flatMap { lhsElem -> c2.map { rhsElem -> lhsElem to rhsElem } }
}

/***********************************************
 *  Points, Vectors
 ************************************************/


typealias Point = Pair<Int, Int>

data class Point2D (val x:Int, val y:Int) {

    constructor(pair : Pair<Int, Int>) : this(pair.first, pair.second)

    fun diff(otherPoint : Point2D) : Point2D = Point2D(this.x - otherPoint.x, this.y - otherPoint.y)
    fun add(otherPoint : Point2D) : Point2D = Point2D(this.x + otherPoint.x, this.y + otherPoint.y)
    fun scalarMultiplication(m:Int) : Point2D = Point2D(x*m, y*m)

    fun inverse() : Point2D = Point2D(-x, -y)

    fun rotateClockwise() : Point2D = Point2D(y, -x)
    fun rotateAntiClockwise() : Point2D = Point2D(-y, x)

    fun print() {
        val x = this.x
        val y = this.y
        println("$x,$y")
    }

    fun longID() : Long = x.toLong() + 10000*y.toLong()
}

val adjacentPoints2D : List<Point2D> = listOf(Point2D(0, 1), Point2D(0, -1), Point2D(1, 0), Point2D(-1, 0))
val adjacentDiagPoints2D : List<Point2D> = adjacentPoints2D + listOf(
    Point2D(1, 1),
    Point2D(-1, 1),
    Point2D(1, -1),
    Point2D(-1, -1)
)

fun manhattan2D(p1 : Point2D, p2 : Point2D) = abs(p1.x - p2.x) + abs(p1.y - p2.y)
fun manhattan3D(p1 : Point3D, p2 : Point3D) = abs(p1.x - p2.x) + abs(p1.y - p2.y) + abs(p1.z - p2.z)

data class Point3D (val x : Int, val y : Int, val z : Int){

    fun transform(orientation : Int) =
        when(orientation % 24) {
            0 -> Point3D(x, y, z)
            1 -> Point3D(x, -y, -z)
            2 -> Point3D(x, z, -y)
            3 -> Point3D(x, -z, y)
            4 -> Point3D(-x, y, -z)
            5 -> Point3D(-x, -y, z)
            6 -> Point3D(-x, z, y)
            7 -> Point3D(-x, -z, -y)
            8 -> Point3D(y, x, -z)
            9 -> Point3D(y, -x, z)
            10 -> Point3D(y, z, x)
            11 -> Point3D(y, -z, -x)
            12 -> Point3D(-y, x, z)
            13 -> Point3D(-y, -x, -z)
            14 -> Point3D(-y, z, -x)
            15 -> Point3D(-y, -z, x)
            16 -> Point3D(z, x, y)
            17 -> Point3D(z, -x, -y)
            18 -> Point3D(z, y, -x)
            19 -> Point3D(z, -y, x)
            20 -> Point3D(-z, x, -y)
            21 -> Point3D(-z, -x, y)
            22 -> Point3D(-z, y, x)
            23 -> Point3D(-z, -y, -x)
            else -> throw Exception("Invalid orientation")
        }

    fun diff(otherPoint : Point3D) : Point3D = Point3D(this.x - otherPoint.x, this.y - otherPoint.y, this.z - otherPoint.z)
    fun add(otherPoint : Point3D) : Point3D = Point3D(this.x + otherPoint.x, this.y + otherPoint.y, this.z + otherPoint.z)
    fun scalarMultiplication(m:Int) : Point3D = Point3D(x*m, y*m, z*m)

    fun longID() : Long = x.toLong() + 10000*y.toLong() + 100000000*z.toLong()
}