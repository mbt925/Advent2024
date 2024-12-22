import Direction.*
import java.util.*
import kotlin.math.min

fun main() {

    data class Coord(val row: Int, val col: Int) {
        fun move(direction: Direction) = copy(row = row + direction.y, col = col + direction.x)
        fun neighbors() = listOf(Up, Down, Left, Right).map { move(it) }
        fun isValid(rows: Int, cols: Int) = row in 0..<rows && col in 0..<cols
        fun direction(to: Coord): Char {
            return if (row == to.row) {
                if (col < to.col) '>' else '<'
            } else {
                if (row < to.row) 'v' else '^'
            }
        }
    }

    data class Node(
        val coord: Coord,
        val pathLen: Long,
    )

    val numericKeypadMap = mutableMapOf(
        '7' to Coord(0, 0),
        '8' to Coord(0, 1),
        '9' to Coord(0, 2),
        '4' to Coord(1, 0),
        '5' to Coord(1, 1),
        '6' to Coord(1, 2),
        '1' to Coord(2, 0),
        '2' to Coord(2, 1),
        '3' to Coord(2, 2),
        'N' to Coord(3, 0),
        '0' to Coord(3, 1),
        'A' to Coord(3, 2),
    )
    val directionalKeypadMap = mutableMapOf(
        'N' to Coord(0, 0),
        '^' to Coord(0, 1),
        'A' to Coord(0, 2),
        '<' to Coord(1, 0),
        'v' to Coord(1, 1),
        '>' to Coord(1, 2),
    )

    class KeypadManager(
        val keypad: Map<Char, Coord>,
        val rows: Int,
        val cols: Int,
    ) {

        val inverseKeypad = keypad.entries.associate { (key, value) -> value to key }

        private val shortestPaths = mutableMapOf<Pair<Char, Char>, List<String>>()

        init {
            keypad.keys.forEach { fromKey ->
                if (fromKey != 'N') shortestPath(fromKey)
            }
        }

        fun getShortestPath(fromKey: Char, toKey: Char) = shortestPaths[fromKey to toKey]!!

        private fun List<Coord>.toPath(): String {
            return buildString {
                for (i in 1..this@toPath.lastIndex) {
                    val preCell = this@toPath[i - 1]
                    val cell = this@toPath[i]
                    append(preCell.direction(cell))
                }
                append('A')
            }
        }

        private fun getNeighbors(cell: Coord): List<Coord> {
            return cell.neighbors()
                .filter { it.isValid(rows, cols) && inverseKeypad[it] != 'N' }
        }

        private fun shortestPath(fromKey: Char) {
            val startCell = keypad[fromKey]!!

            val queue = LinkedList<Node>()
            val paths = mutableMapOf<Coord, MutableList<List<Coord>>>()
            val dist = mutableMapOf<Coord, Long>()

            queue.add(Node(startCell, 0L))
            paths[startCell] = mutableListOf(mutableListOf(startCell))
            dist[startCell] = 0

            while (queue.isNotEmpty()) {
                val currNode = queue.remove()
                getNeighbors(currNode.coord)
                    .forEach { neighbor ->
                        val newLength = currNode.pathLen + 1
                        val oldLength = dist.getOrDefault(neighbor, Long.MAX_VALUE)
                        if (newLength < oldLength) {
                            dist[neighbor] = newLength
                            queue.add(Node(neighbor, newLength))
                        }
                        if (dist[neighbor] == newLength) {
                            for (path in paths.getOrDefault(currNode.coord, mutableListOf())) {
                                paths.putIfAbsent(neighbor, mutableListOf())
                                paths[neighbor]!!.add(path + neighbor)
                            }
                        }
                    }
            }
            keypad.keys.forEach { toKey ->
                if (toKey != 'N') {
                    shortestPaths[fromKey to toKey] = paths[keypad[toKey]]!!.map { it.toPath() }
                }
            }
        }

    }

    class MultipleKeypadManager(
        val numericKeypad: KeypadManager = KeypadManager(numericKeypadMap, 4, 3),
        val directionalKeypad: KeypadManager = KeypadManager(directionalKeypadMap, 2, 3),
    ) {

        private val cache = mutableMapOf<Triple<Char, Char, Int>, Long>()

        private fun shortestPath(input: String, keypadManager: KeypadManager, depth: Int): Long {
            val inputPlusS = "A$input"
            var minLength = 0L
            for (i in 1..inputPlusS.lastIndex) {
                val fromKey = inputPlusS[i - 1]
                val toKey = inputPlusS[i]
                minLength += shortestPath(fromKey, toKey, keypadManager, depth)
            }
            return minLength
        }

        private fun shortestPath(fromKey: Char, toKey: Char, keypadManager: KeypadManager, depth: Int): Long {
            if (depth == 0) return keypadManager.getShortestPath(fromKey, toKey).first().length.toLong()
            cache[Triple(fromKey, toKey, depth)]?.let { return it }
            val allPaths = keypadManager.getShortestPath(fromKey, toKey)
            var minLength = Long.MAX_VALUE
            allPaths.forEach { path ->
                val pathPlusA = "A$path"
                var overallLength = 0L
                for (i in 1..pathPlusA.lastIndex) {
                    overallLength += shortestPath(
                        fromKey = pathPlusA[i - 1],
                        toKey = pathPlusA[i],
                        keypadManager = directionalKeypad,
                        depth = depth - 1,
                    )
                }
                minLength = min(minLength, overallLength)
            }
            cache[Triple(fromKey, toKey, depth)] = minLength
            return minLength
        }

        private fun String.numericalPart() = filter { it.isDigit() }.toLong()

        fun complexity(input: String, depth: Int): Long {
            return shortestPath(input, numericKeypad, depth) * input.numericalPart()
        }
    }

    fun part1(input: List<String>): Long {
        val manager = MultipleKeypadManager()
        return input.fold(0) { acc, line ->
            acc + manager.complexity(line, 2)
        }
    }

    fun part2(input: List<String>): Long {
        val manager = MultipleKeypadManager()
        return input.fold(0) { acc, line ->
            acc + manager.complexity(line, 25)
        }
    }

    val testInput11 = readInput("Day21_test")
    check(part1(testInput11) == 126384L)

    val input = readInput("Day21")
    part1(input).println()
    part2(input).println()
}
