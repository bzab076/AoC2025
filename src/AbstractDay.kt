import java.io.File
import kotlin.collections.map
import kotlin.collections.toIntArray
import kotlin.collections.toTypedArray
import kotlin.io.readLines
import kotlin.io.readText
import kotlin.jvm.javaClass
import kotlin.text.digitToInt
import kotlin.text.map
import kotlin.text.padStart
import kotlin.text.toCharArray
import kotlin.text.toInt

abstract class AbstractDay (private val dayNumber: Int) {

    abstract fun partOne(): Any

    abstract fun partTwo(): Any

    fun getFile() =  File(javaClass.classLoader.getResource("day${dayNumber.toString().padStart(2, '0')}").toURI())

    fun inputLines(): List<String> =
        File(javaClass.classLoader.getResource("day${dayNumber.toString().padStart(2, '0')}").toURI()).readLines()

    fun inputString(): String = File(javaClass.classLoader.getResource("day${dayNumber.toString().padStart(2, '0')}").toURI()).readText()

    fun inputNumbers() = inputLines().map { it -> it.toInt() }

    fun inputDigits() =  inputLines().map { it.map { char -> char.digitToInt() }.toIntArray() }.toTypedArray()

    fun inputCharArray() = inputLines().map {it.toCharArray()}.toTypedArray()

}