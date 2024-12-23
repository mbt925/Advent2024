fun main() {

    class NetworkManager(
        val graph: Map<String, Set<String>>,
    ) {
        constructor(input: List<String>) : this(
            graph = buildMap {
                input.forEach { line ->
                    val pair = line.split("-")
                    put(pair[0], getOrDefault(pair[0], setOf()) + pair[1])
                    put(pair[1], getOrDefault(pair[1], setOf()) + pair[0])
                }
            }
        )

        fun find3InterConnectedComponents(): Int {
            val interConnectedComponents = mutableSetOf<String>()
            graph.keys.forEach { node ->
                if (node.startsWith("t")) {
                    val outgoingEdges = graph[node]!!
                    for (i in outgoingEdges.indices) {
                        for (j in i + 1..<outgoingEdges.size) {
                            val a = outgoingEdges.elementAt(i)
                            val b = outgoingEdges.elementAt(j)
                            if (graph.getOrDefault(a, emptyList()).contains(b)) {
                                val connectedComponent = listOf(node, a, b).sorted().joinToString()
                                interConnectedComponents.add(connectedComponent)
                            }
                        }
                    }
                }
            }
            return interConnectedComponents.size
        }

        fun findInterConnectedComponents(maxGroupSize: Int): String {
            val interConnectedComponents = mutableMapOf<Int, MutableSet<List<String>>>()
            graph.keys.forEach { node ->
                val outgoingEdges = graph[node]!!
                outgoingEdges.forEach { a ->
                    interConnectedComponents.putIfAbsent(2, mutableSetOf())
                    interConnectedComponents[2]!! += listOf(node, a).sorted()
                }
            }

            var newGroupAdded = true
            var groupSize = 3
            while (newGroupAdded && groupSize <= maxGroupSize) {
                graph.keys.forEach { node ->
                    newGroupAdded = false
                    val outgoingEdges = graph[node]!!
                    outgoingEdges.forEach { a ->
                        // find a component with [groupSize-1] with a in it and check connection to all other members
                        val allGroupsWithA = interConnectedComponents[groupSize - 1]!!.filter { it.contains(a) }
                        allGroupsWithA.forEach { groupWithA ->
                            if (groupWithA.all { outgoingEdges.contains(it) }) {
                                interConnectedComponents.putIfAbsent(groupSize, mutableSetOf())
                                interConnectedComponents[groupSize]!! += (groupWithA + node).sorted()
                                newGroupAdded = true
                            }
                        }
                    }
                }
                groupSize++
            }
            return interConnectedComponents[groupSize - 1]!!.first().joinToString(",")
        }

    }

    fun part1(input: List<String>): Int {
        return NetworkManager(input).find3InterConnectedComponents()
    }

    fun part2(input: List<String>): String {
        return NetworkManager(input).findInterConnectedComponents(input.size)
    }

    val testInput11 = readInput("Day23_test")
    check(part1(testInput11) == 7)
    check(part2(testInput11) == "co,de,ka,ta")

    val input = readInput("Day23")
    part1(input).println()
    part2(input).println()
}
