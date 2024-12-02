import java.util.*
import kotlin.math.abs
import kotlin.math.max

fun main() {

    fun List<Int>.isIncreasing(margin: Int): Boolean {
        forEachIndexed { index, num ->
            val diff = num - this[max(0, index - 1)]
            if (index > 0 && (diff <= 0 || diff > margin)) return false
        }
        return true
    }

    fun List<Int>.isDecreasing(margin: Int): Boolean {
        forEachIndexed { index, num ->
            val diff = num - this[max(0, index - 1)]
            if (index > 0 && (diff >= 0 || diff < -margin)) return false
        }
        return true
    }

    fun List<Int>.isMarginallyIncreasing(margin: Int): Boolean {
        repeat(size) { skipIndex ->
            val newList = subList(0, skipIndex) + subList(skipIndex + 1, size)
            if (newList.isIncreasing(margin)) return true
        }
        return false
    }

    fun List<Int>.isMarginallyDecreasing(margin: Int): Boolean {
        repeat(size) { skipIndex ->
            val newList = subList(0, skipIndex) + subList(skipIndex + 1, size)
            if (newList.isDecreasing(margin)) return true
        }
        return false
    }

    fun String.isSafe(): Boolean {
        val list = split(" ").map { it.toInt() }
        return list.isIncreasing(3) || list.isDecreasing(3)
    }

    fun String.isMarginallySafe(): Boolean {
        val list = split(" ").map { it.toInt() }
        return list.isMarginallyIncreasing(3) || list.isMarginallyDecreasing(3)
    }

    fun part1(input: List<String>): Int {
        return input.fold(0) { acc, line ->
            acc + if (line.isSafe()) 1 else 0
        }
    }

    fun part2(input: List<String>): Int {
        return input.fold(0) { acc, line ->
            acc + if (line.isMarginallySafe()) 1 else 0
        }
    }

    val testInput1 = readInput("Day02_test_part1")
    check(part1(testInput1) == 2)

    val testInput2 = readInput("Day02_test_part2")
    check(part2(testInput2) == 4)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
