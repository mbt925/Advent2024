import java.util.LinkedList

fun main() {

    data class Location(val row: Int, val col: Int) {

        fun antiNodesWith(other: Location, size: Int, infinity: Boolean): List<Location> {
            val queue = LinkedList<Pair<Location, Location>>()
            val visited = mutableSetOf(this, other)
            val antiNodes = mutableListOf<Location>()
            queue.add(Pair(this, other))

            var counter = 0
            val maxRepeat = if (infinity) Int.MAX_VALUE else 1
            while (counter < maxRepeat && queue.isNotEmpty()) {
                counter++
                val (first, second) = queue.pop()
                val antiNode1 = first.copy(
                    row = first.row + (first.row - second.row),
                    col = first.col + (first.col - second.col)
                )
                val antiNode2 = second.copy(
                    row = second.row + (second.row - first.row),
                    col = second.col + (second.col - first.col)
                )
                if (antiNode1.isInside(size)) {
                    if (!visited.contains(antiNode1)) {
                        visited.add(antiNode1)
                        antiNodes.add(antiNode1)
                        queue.add(Pair(first, antiNode1))
                    }
                }
                if (antiNode2.isInside(size)) {
                    if (!visited.contains(antiNode2)) {
                        visited.add(antiNode2)
                        antiNodes.add(antiNode2)
                        queue.add(Pair(second, antiNode2))
                    }
                }
            }
            return antiNodes
        }

        private fun isInside(size: Int) = row in 0..<size && col in 0..<size
    }

    fun List<String>.toAntennas(): Map<Char, List<Location>> {
        return buildMap {
            this@toAntennas.forEachIndexed { row, line ->
                line.forEachIndexed { col, c ->
                    if (c != '.') {
                        putIfAbsent(c, mutableListOf())
                        put(c, (get(c) as MutableList).apply { add(Location(row, col)) })
                    }
                }
            }
        }
    }

    class AntennaManager(
        private val antennas: Map<Char, List<Location>>,
        private val size: Int,
    ) {
        constructor(input: List<String>) : this(antennas = input.toAntennas(), size = input.size)

        fun totalNumOfUniqueAntiNodes(infinity: Boolean, includeAntennas: Boolean): Int {
            val antiNodesSet = mutableSetOf<Location>()
            antennas.forEach { (_, antennas) ->
                for (i in antennas.indices) {
                    for (j in i + 1..<antennas.size) {
                        antennas[i].antiNodesWith(other = antennas[j], size = size, infinity = infinity).forEach {
                            antiNodesSet.add(it)
                        }
                    }
                }
            }
            if (includeAntennas) {
                antennas.forEach { (_, antennas) ->
                    antennas.forEach { antiNodesSet.add(it) }
                }
            }
            return antiNodesSet.size
        }
    }

    fun part1(input: List<String>): Int {
        return AntennaManager(input).totalNumOfUniqueAntiNodes(infinity = false, includeAntennas = false)
    }

    fun part2(input: List<String>): Int {
        return AntennaManager(input).totalNumOfUniqueAntiNodes(infinity = true, includeAntennas = true)
    }

    val testInput1 = readInput("Day08_test")
    println(part2(testInput1))
    check(part1(testInput1) == 14)
    check(part2(testInput1) == 34)

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}
