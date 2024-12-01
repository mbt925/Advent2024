import java.util.*
import kotlin.math.abs

fun main() {

    fun part1(input: List<String>): Long {
        val list1 = mutableListOf<Int>()
        val list2 = mutableListOf<Int>()
        input.forEach { line ->
            val nums = Scanner(line)
            list1.add(nums.nextInt())
            list2.add(nums.nextInt())
        }
        list1.sort()
        list2.sort()
        return list1.foldIndexed(0L) { index, acc, num1 ->
            acc + abs(num1 - list2[index])
        }
    }

    fun part2(input: List<String>): Long {
        val list1 = mutableListOf<Int>()
        val list2 = mutableListOf<Int>()
        input.forEach { line ->
            val nums = Scanner(line)
            list1.add(nums.nextInt())
            list2.add(nums.nextInt())
        }

        val list2MapByCount = list2.groupingBy { it }.eachCount()
        return list1.fold(0L) { acc, num1 ->
            acc + num1 * list2MapByCount.getOrDefault(num1, 0)
        }
    }

    val testInput1 = readInput("Day01_test_part1")
    check(part1(testInput1) == 11L)

    val testInput2 = readInput("Day01_test_part2")
    check(part2(testInput2) == 31L)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
