import Direction.*

fun main() {
    data class Coord(val row: Int, val col: Int) {
        fun move(direction: Direction) = copy(row = row + direction.y, col = col + direction.x)
        fun neighbors() = listOf(Up, Down, Left, Right).map { move(it) }
        fun isValid(size: Int) = row in 0..<size && col in 0..<size
        fun isNeighbor(other: Coord) = neighbors().contains(other)
        fun getEdgeFacing(other: Coord) = if (row == other.row) {
            if (col < other.col) Right else Left
        } else {
            if (row < other.row) Down else Up
        }
    }

    data class Edge(val coord: Coord, val direction: Direction) {
        fun isConnected(other: Edge) = direction == other.direction && coord.isNeighbor(other.coord)
        fun isHorizontal() = direction == Up || direction == Down
    }

    operator fun Array<Array<Int>>.get(index: Coord) = this[index.row][index.col]
    fun Array<Array<Int>>.put(index: Coord, value: Int) {
        this[index.row][index.col] = value
    }

    operator fun Array<CharArray>.get(index: Coord) = this[index.row][index.col]


    data class ConnectedComponents(
        val graph: Array<Array<Int>>,
        val numOfComponents: Int,
    ) {

        fun Coord.edges(): List<Edge> {
            val component = graph[this]
            return buildList {
                neighbors().forEach { neighbor ->
                    if (neighbor.isValid(graph.size)) {
                        if (graph[neighbor] != component) add(Edge(this@edges, getEdgeFacing(neighbor)))
                    } else add(Edge(this@edges, getEdgeFacing(neighbor)))
                }
            }
        }

        fun numOfEdges(coord: Coord): Int {
            val component = graph[coord]
            return coord.neighbors().fold(0) { acc, neighbor ->
                acc + if (neighbor.isValid(graph.size)) {
                    if (graph[neighbor] == component) 0 else 1
                } else 1
            }
        }

    }

    class MapManager(
        val map: Array<CharArray>,
    ) {
        constructor(input: List<String>) : this(input.map { it.toCharArray() }.toTypedArray())

        private val size = map.size

        private fun connectedComponents(
            graph: Array<Array<Int>>,
            start: Coord,
            componentIndex: Int
        ) {
            val queue = mutableSetOf<Coord>()
            val char = map[start]

            queue.add(start)
            while (queue.isNotEmpty()) {
                val curr = queue.elementAt(0)
                queue.remove(curr)
                graph.put(curr, componentIndex)
                curr.neighbors().forEach { neighbor ->
                    if (neighbor.isValid(size) && map[neighbor] == char && graph[neighbor] == -1) {
                        queue.add(neighbor)
                    }
                }
            }
        }

        private fun connectedComponents(): ConnectedComponents {
            val graph = Array(size = size) { Array(size) { -1 } }
            var componentIndex = 0

            graph.forEachIndexed { row, rowChars ->
                rowChars.forEachIndexed { col, _ ->
                    val coord = Coord(row, col)
                    if (graph[coord] == -1) {
                        connectedComponents(graph, coord, componentIndex)
                        componentIndex++
                    }
                }
            }
            return ConnectedComponents(graph, componentIndex)
        }

        fun fencePrice(): Long {
            val connectedComponents = connectedComponents()
            val areas = mutableMapOf<Int, Int>()
            val perimeters = mutableMapOf<Int, Int>()

            connectedComponents.graph.forEachIndexed { row, rowChars ->
                rowChars.forEachIndexed { col, component ->
                    val coord = Coord(row, col)
                    areas[component] = areas.getOrPut(component) { 0 } + 1
                    perimeters[component] = perimeters.getOrPut(component) { 0 } + connectedComponents.numOfEdges(coord)
                }
            }
            return areas.keys.fold(0L) { acc, component ->
                acc + areas[component]!! * perimeters[component]!!
            }
        }

        private fun List<Edge>.numOfEdges(): Int {
            var edgesCount = 0
            var lastEdge = this[0]
            for (i in 1..<size) {
                val currEdge = this[i]
                if (!currEdge.isConnected(lastEdge)) edgesCount++
                lastEdge = currEdge
            }
            return edgesCount + 1
        }

        fun ConnectedComponents.numOfSides(coord: Coord): Int {
            val component = graph[coord]
            val horEdges = mutableSetOf<Edge>()
            val verEdges = mutableSetOf<Edge>()
            val queue = mutableSetOf<Coord>()
            val visited = mutableSetOf<Coord>()

            queue.add(coord)
            while (queue.isNotEmpty()) {
                val curr = queue.elementAt(0)
                queue.remove(curr)
                visited.add(curr)
                curr.edges().forEach {
                    if (it.isHorizontal()) horEdges.add(it) else verEdges.add(it)
                }
                curr.neighbors().forEach { neighbor ->
                    if (neighbor.isValid(size) && graph[neighbor] == component && !visited.contains(neighbor)) {
                        queue.add(neighbor)
                    }
                }
            }
            val horEdgesPerRow = horEdges.groupBy { it.coord.row }
            var horEdgesCount = 0
            horEdgesPerRow.forEach { (_, edges) ->
                val upSorted = edges.filter { it.direction == Up }.sortedBy { it.coord.col }
                val downSorted = edges.filter { it.direction == Down }.sortedBy { it.coord.col }
                if (upSorted.isNotEmpty()) horEdgesCount += upSorted.numOfEdges()
                if (downSorted.isNotEmpty()) horEdgesCount += downSorted.numOfEdges()
            }

            val verEdgesPerCol = verEdges.groupBy { it.coord.col }
            var verEdgesCount = 0
            verEdgesPerCol.forEach { (_, edges) ->
                val leftSorted = edges.filter { it.direction == Left }.sortedBy { it.coord.row }
                val rightSorted = edges.filter { it.direction == Right }.sortedBy { it.coord.row }
                if (leftSorted.isNotEmpty()) verEdgesCount += leftSorted.numOfEdges()
                if (rightSorted.isNotEmpty()) verEdgesCount += rightSorted.numOfEdges()
            }
            return horEdgesCount + verEdgesCount
        }

        fun fenceBulkDiscountPrice(): Long {
            val connectedComponents = connectedComponents()
            val areas = mutableMapOf<Int, Int>()
            val edges = mutableMapOf<Int, Int>()

            val visited = mutableSetOf<Int>()
            connectedComponents.graph.forEachIndexed { row, rowChars ->
                rowChars.forEachIndexed { col, component ->
                    val coord = Coord(row, col)
                    areas[component] = areas.getOrPut(component) { 0 } + 1
                    if (!visited.contains(component)) {
                        edges[component] = connectedComponents.numOfSides(coord)
                        visited.add(component)
                    }
                }
            }
            return areas.keys.fold(0L) { acc, component ->
                acc + areas[component]!! * edges[component]!!
            }
        }
    }

    fun part1(input: List<String>): Long {
        return MapManager(input).fencePrice()
    }

    fun part2(input: List<String>): Long {
        return MapManager(input).fenceBulkDiscountPrice()
    }

    val testInput11 = readInput("Day12_test1")
    check(part1(testInput11) == 140L)
    check(part2(testInput11) == 80L)
    val testInput12 = readInput("Day12_test2")
    check(part1(testInput12) == 1930L)
    check(part2(testInput12) == 1206L)

    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}
