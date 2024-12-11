import java.util.LinkedList

fun main() {

    data class NumAndBlink(val num: Long, val blink: Int)

    class StonesManager(
        val stones: LinkedList<Long>,
    ) {

        constructor(input: List<String>) : this(
            input[0].split(" ")
                .map { it.toLong() }
                .let { s ->
                    val list = LinkedList<Long>()
                    s.forEach { list.add(it) }
                    list
                })

        private val cache = mutableMapOf<NumAndBlink, Long>()

        fun dp(numAndBlink: NumAndBlink): Long {
            cache[numAndBlink]?.let {
                return it
            }

            if (numAndBlink.blink == 0) {
                return cache.getOrPut(numAndBlink) { 1 }
            }
            if (numAndBlink.num == 0L) {
                val newNumAndBlink = NumAndBlink(1, numAndBlink.blink - 1)
                return cache.getOrPut(newNumAndBlink) { dp(NumAndBlink(1, numAndBlink.blink - 1)) }
            } else {
                val str = numAndBlink.num.toString()
                if (str.length % 2 == 0) {
                    val left = str.substring(0, str.length / 2).toLong()
                    val right = str.substring(str.length / 2, str.length).toLong()
                    val leftNumAndBlink = NumAndBlink(left, numAndBlink.blink - 1)
                    val rightNumAndBlink = NumAndBlink(right, numAndBlink.blink - 1)

                    return cache.getOrPut(numAndBlink) {
                        dp(leftNumAndBlink) + dp(rightNumAndBlink)
                    }
                } else {
                    val newNumAndBlink = NumAndBlink(numAndBlink.num * 2024, numAndBlink.blink - 1)
                    return cache.getOrPut(newNumAndBlink) { dp(newNumAndBlink) }
                }
            }
        }

        fun blink(count: Int): Long {
            return stones.fold(0L) { acc, num ->
                acc + dp(NumAndBlink(num, count))
            }
        }

    }

    fun part1(input: List<String>): Long {
        return StonesManager(input).blink(25)
    }

    fun part2(input: List<String>): Long {
        return StonesManager(input).blink(75)
    }

    val testInput1 = readInput("Day11_test")
    check(part1(testInput1) == 55312L)

    val input = readInput("Day11")
    part1(input).println()
    part2(input).println()
}
