import Direction.*
import java.util.*
import kotlin.Long.Companion.MAX_VALUE

fun main() {

    data class Coord(val row: Int, val col: Int) {
        fun move(direction: Direction) = copy(row = row + direction.y, col = col + direction.x)
        fun isValid(size: Int) = row in 0..<size && col in 0..<size
    }

    data class Node(
        val coord: Coord,
        val score: Long,
        val direction: Direction,
    )

    fun Direction.numOfRotationToFace(other: Direction): Int {
        if (this == other) return 0
        if (isVertical() && other.isVertical()) return 2
        if (!isVertical() && !other.isVertical()) return 2
        return 1
    }

    data class Maze(
        val maze: Array<CharArray>,
        val start: Coord,
        val end: Coord,
    ) {

        fun getNeighbors(node: Node): List<Node> {
            return listOf(Up, Down, Left, Right)
                .filter {
                    val movedNode = node.coord.move(it)
                    movedNode.isValid(maze.size) && maze[movedNode.row][movedNode.col] == '.'
                }
                .map {
                    val score = node.direction.numOfRotationToFace(it) * 1000 + 1L
                    Node(node.coord.move(it), score, it)
                }
        }
    }

    fun List<String>.toMaze(): Maze {
        lateinit var start: Coord
        lateinit var end: Coord
        val warehouse = buildList {
            this@toMaze.forEachIndexed { row, line ->
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
        return Maze(warehouse, start, end)
    }

    class MazeManager(
        val maze: Maze
    ) {
        constructor(input: List<String>) : this(input.toMaze())

        // Backward BFS using the shortest paths from the start node
        // A node is on the shortest path if score[start][node] + score[node][end] <= shortestPathScore
        fun numOfUniqueNodesInAllShortestPaths(): Int {
            val shortestScores = shortestPathScore()
            val shortestScore = shortestScores[maze.end]!!

            val shortestPathNodes = mutableSetOf<Coord>()
            val visited = mutableSetOf<Coord>()
            val queue = LinkedList<Node>()
            queue.add(Node(maze.end, 0L, Right))
            visited.add(maze.end)

            while (queue.isNotEmpty()) {
                val currNode = queue.remove()
                visited.add(currNode.coord)
                maze.getNeighbors(currNode).forEach { neighbor ->
                    val distanceScore = if (currNode.coord == maze.end) 1 else neighbor.score
                    val distanceToEnd = currNode.score + distanceScore
                    val distanceFromStart = shortestScores.getOrDefault(neighbor.coord, MAX_VALUE)
                    if (distanceFromStart + distanceToEnd <= shortestScore) {
                        shortestPathNodes.add(neighbor.coord)
                        if (!visited.contains(neighbor.coord)) queue.add(neighbor.copy(score = distanceToEnd))
                    }
                }
            }
            return shortestPathNodes.size + 1 // to count the start node
        }

        fun shortestPathScore(): Map<Coord, Long> {
            val minHeap = PriorityQueue<Node> { o1, o2 -> (o1.score - o2.score).toInt() }
            val score = mutableMapOf<Coord, Long>()

            minHeap.add(Node(maze.start, 0L, Right))
            score[maze.start] = 0L

            while (minHeap.isNotEmpty()) {
                val currNode = minHeap.remove()
                maze.getNeighbors(currNode).forEach { neighbor ->
                    val newScore = currNode.score + neighbor.score
                    val oldScore = score.getOrDefault(neighbor.coord, MAX_VALUE)
                    if (newScore < oldScore) {
                        score[neighbor.coord] = newScore
                        minHeap.add(neighbor.copy(score = newScore))
                    }
                }
            }
            return score
        }

    }

    fun part1(input: List<String>): Long {
        val mazeManager = MazeManager(input)
        val shortestScores = mazeManager.shortestPathScore()
        return shortestScores[mazeManager.maze.end]!!
    }

    fun part2(input: List<String>): Int {
        return MazeManager(input).numOfUniqueNodesInAllShortestPaths()
    }

    val testInput11 = readInput("Day16_test")
    check(part1(input = testInput11) == 7036L)
    check(part2(input = testInput11) == 45)

    val testInput12 = readInput("Day16_test2")
    check(part1(input = testInput12) == 11048L)
    check(part2(input = testInput12) == 64)

    val input = readInput("Day16")
    part1(input = input).println()
    part2(input = input).println()
}
