import Direction.*
import java.util.LinkedList

fun main() {

    data class Coord(val row: Int, val col: Int) {
        fun move(direction: Direction) = copy(row = row + direction.y, col = col + direction.x)
        fun neighbors() = listOf(Up, Down, Left, Right).map { move(it) }
        fun isValid(size: Int) = row in 0..<size && col in 0..<size
    }

    data class Point(
        val height: Int,
        val coord: Coord,
    ) {

        fun neighbors() = listOf(Up, Down, Left, Right).map { coord.move(it) }

    }

    class HikingManager(
        val hill: Array<Array<Int>>,
    ) {
        constructor(input: List<String>) : this(input.map { it.map { it.digitToInt() }.toTypedArray() }.toTypedArray())

        private val size = hill.size

        private operator fun get(index: Coord) = hill[index.row][index.col]

        private fun ratingOfTrailHeadsFrom(start: Coord): Int {
            val queue = LinkedList<Coord>()
            var uniqueRoutes = 0

            queue.add(start)
            while (queue.isNotEmpty()) {
                val curr = queue.removeFirst()
                val currValue = this[curr]

                if (currValue == 9) {
                    uniqueRoutes++
                    continue
                }

                curr.neighbors().forEach { neighbor ->
                    if (neighbor.isValid(size)) {
                        val value = this[neighbor]
                        if (value == currValue + 1) queue.add(neighbor)
                    }
                }
            }
            return uniqueRoutes
        }

        private fun numOfTrailHeadsFrom(start: Coord): Int {
            val queue = LinkedList<Coord>()
            val visited = mutableSetOf<Coord>()
            val trainHeads = mutableSetOf<Coord>()

            queue.add(start)
            while (queue.isNotEmpty()) {
                val curr = queue.removeFirst()
                val currValue = this[curr]
                visited.add(curr)

                if (currValue == 9) {
                    trainHeads.add(curr)
                    continue
                }

                curr.neighbors().forEach { neighbor ->
                    if (neighbor.isValid(size) && !visited.contains(neighbor)) {
                        val value = this[neighbor]
                        if (value == currValue + 1) queue.add(neighbor)
                    }
                }
            }
            return trainHeads.size
        }

        fun numOfTrainHeads(): Int {
            return hill.foldIndexed(0) { row, acc, rowHeights ->
                acc + rowHeights.foldIndexed(0) { col, accRow, height ->
                    accRow + if (height == 0) numOfTrailHeadsFrom(Coord(row = row, col = col)) else 0
                }
            }
        }

        fun ratingOfTrainHeads(): Int {
            return hill.foldIndexed(0) { row, acc, rowHeights ->
                acc + rowHeights.foldIndexed(0) { col, accRow, height ->
                    accRow + if (height == 0) ratingOfTrailHeadsFrom(Coord(row = row, col = col)) else 0
                }
            }
        }
    }

    fun part1(input: List<String>): Int {
        return HikingManager(input).numOfTrainHeads()
    }

    fun part2(input: List<String>): Int {
        return HikingManager(input).ratingOfTrainHeads()
    }

    val testInput1 = readInput("Day10_test")
    println(part2(testInput1))
    check(part1(testInput1) == 36)
    check(part2(testInput1) == 81)

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}
