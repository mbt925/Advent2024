import Direction.*
import java.lang.Integer.max
import java.util.*
import kotlin.math.abs

fun main() {

    data class Coord(val row: Int, val col: Int) {
        fun move(direction: Direction) = copy(row = row + direction.y, col = col + direction.x)
        fun neighbors() = listOf(Up, Down, Left, Right).map { move(it) }
        fun isValid(size: Int) = row in 1..<size - 1 && col in 1..<size - 1
        fun manhattanDistanceTo(other: Coord) = abs(row - other.row) + abs(col - other.col)
    }

    data class Node(
        val coord: Coord,
        val pathLen: Long,
    )

    data class CheatPath(
        val start: Coord,
        val end: Coord,
    )

    data class Race(
        val grid: Array<CharArray>,
        val start: Coord,
        val end: Coord,
    ) {

        operator fun get(index: Coord) = grid[index.row][index.col]

        fun getNeighbors(cell: Coord): List<Coord> {
            return cell.neighbors()
                .filter { it.isValid(grid.size) && this[it] == '.' }
        }

    }

    fun List<String>.toRace(): Race {
        lateinit var start: Coord
        lateinit var end: Coord
        val warehouse = buildList {
            this@toRace.forEachIndexed { row, line ->
                val startIndex = line.indexOfFirst { it == 'S' }
                val endIndex = line.indexOfFirst { it == 'E' }
                if (startIndex >= 0) start = Coord(row, startIndex)
                if (endIndex >= 0) end = Coord(row, endIndex)
                add(line.toCharArray().apply {
                    if (startIndex >= 0) this[startIndex] = '.'
                    if (endIndex >= 0) this[endIndex] = '.'
                })
            }
        }.toTypedArray()
        return Race(warehouse, start, end)
    }

    class RaceManager(
        val race: Race,
    ) {

        fun cheatingShortestPathLength(savesAtLeast: Int, maxCheatsCount: Int): Int {
            val shortestPath = shortestPath()
            val maxSavedPerCheatPath = mutableMapOf<CheatPath, Int>()

            shortestPath.forEachIndexed { index, start ->
                for (nextIndex in index + savesAtLeast..shortestPath.lastIndex) {
                    val end = shortestPath[nextIndex]
                    val distance = nextIndex - index
                    val manhattanDistance = start.manhattanDistanceTo(end)
                    if (manhattanDistance <= maxCheatsCount) {
                        val cheatPath = CheatPath(start, end)
                        val saved = distance - manhattanDistance
                        if (saved >= savesAtLeast) {
                            maxSavedPerCheatPath[cheatPath] =
                                max(maxSavedPerCheatPath.getOrDefault(cheatPath, 0), saved)
                        }
                    }
                }
            }
            return maxSavedPerCheatPath.values.size
        }

        private fun shortestPath(): List<Coord> {
            val minHeap = PriorityQueue<Node> { o1, o2 -> (o1.pathLen - o2.pathLen).toInt() }
            val paths = mutableMapOf<Coord, Long>()
            val parent = mutableMapOf<Coord, Coord>()

            minHeap.add(Node(race.start, 0L))
            paths[race.start] = 0L

            while (minHeap.isNotEmpty()) {
                val currNode = minHeap.remove()
                race.getNeighbors(currNode.coord)
                    .forEach { neighbor ->
                        val newLength = currNode.pathLen + 1
                        val oldLength = paths.getOrDefault(neighbor, Long.MAX_VALUE)
                        if (newLength < oldLength) {
                            paths[neighbor] = newLength
                            parent[neighbor] = currNode.coord
                            minHeap.add(Node(neighbor, newLength))
                        }
                    }
            }
            return buildList<Coord> {
                var curr: Coord? = race.end
                while (curr != null) {
                    add(curr)
                    curr = parent[curr]
                }
            }.reversed()
        }
    }

    fun part1(input: List<String>, savesAtLeast: Int): Int {
        return RaceManager(input.toRace()).cheatingShortestPathLength(savesAtLeast, 2)
    }

    fun part2(input: List<String>, savesAtLeast: Int): Int {
        return RaceManager(input.toRace()).cheatingShortestPathLength(savesAtLeast, 20)
    }

    val testInput11 = readInput("Day20_test")
    check(part1(input = testInput11, 20) == 5)
    check(part2(input = testInput11, 76) == 3)

    val input = readInput("Day20")
    part1(input = input, 100).println()
    part2(input = input, 100).println()
}
