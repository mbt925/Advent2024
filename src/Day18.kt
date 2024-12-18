import Direction.*
import java.util.*
import kotlin.math.abs

fun main() {

    data class Coord(val row: Int, val col: Int) {
        fun move(direction: Direction) = copy(row = row + direction.y, col = col + direction.x)
        fun neighbors() = listOf(Up, Down, Left, Right).map { move(it) }
        fun isValid(size: Int) = row in 0..<size && col in 0..<size
    }

    data class Node(
        val coord: Coord,
        val len: Int,
    )

    class MemoryManager(
        val memory: Array<CharArray>,
    ) {

        private val size = memory.size

        private operator fun get(index: Coord) = memory[index.row][index.col]

        fun shortestPathLen(): Int {
            val start = Coord(0, 0)
            val end = Coord(size - 1, size - 1)
            val queue = LinkedList<Node>()
            val visited = mutableSetOf<Coord>()

            queue.add(Node(start, 0))
            visited.add(start)

            while (queue.isNotEmpty()) {
                val curr = queue.removeFirst()
                if (curr.coord == end) return curr.len

                curr.coord.neighbors().forEach { neighbor ->
                    if (!visited.contains(neighbor) && neighbor.isValid(size) && this[neighbor] == '.') {
                        queue.add(Node(neighbor, curr.len + 1))
                        visited.add(neighbor)
                    }
                }
            }
            return -1
        }
    }

    fun List<String>.toMemory(size: Int): Array<CharArray> {
        val memory = Array(size) { CharArray(size) { '.' } }
        forEach { line ->
            val split = line.split(",")
            memory[split[0].toInt()][split[1].toInt()] = '#'
        }
        return memory
    }

    fun part1(input: List<String>, size: Int, count: Int): Int {
        return MemoryManager(input.take(count).toMemory(size)).shortestPathLen()
    }

    fun part2(input: List<String>, size: Int): String {
        // binary search
        var previousEnd = 0
        var end = input.lastIndex
        while (previousEnd != end) {
            val shortestPathLen = MemoryManager(input.take(end).toMemory(size)).shortestPathLen()
            val diff = abs(previousEnd - end) / 2
            previousEnd = end
            end += if (shortestPathLen == -1) -diff else diff
        }
        return input[end]
    }

    val testInput11 = readInput("Day18_test")
    check(part1(input = testInput11, 7, 12) == 22)
    check(part2(input = testInput11, 7) == "6,1")

    val input = readInput("Day18")
    part1(input = input, 71, 1024).println()
    part2(input = input, 71).println()
}
