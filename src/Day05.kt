import java.util.LinkedList

fun main() {

    data class Node(val value: Int) {
        private val children = LinkedList<Int>()

        fun addChild(node: Int) = children.add(node)
        fun hasChild(value: Int) = children.any { it == value }
    }

    class UpdateManager {

        private val pages = mutableMapOf<Int, Node>()

        fun addPageOrder(first: Int, then: Int) {
            val node = pages.getOrPut(first) { Node(first) }
            node.addChild(then)
        }

        private fun verifyOrder(first: Int, then: Int): Boolean {
            val firstNode = pages.getOrDefault(first, Node(first))
            return firstNode.hasChild(then)
        }

        fun isUpdateOrderRight(pages: List<Int>): Boolean {
            for (i in 0..<pages.lastIndex) {
                if (!verifyOrder(pages[i], pages[i + 1])) return false
            }
            return true
        }

        fun fixUpdateOrderRight(pages: List<Int>): List<Int> {
            val mutablePages = pages.toMutableList()
            var atLeastOnWrongOrder = true
            while (atLeastOnWrongOrder) {
                atLeastOnWrongOrder = false
                for (j in 0..<pages.lastIndex) {
                    if (!verifyOrder(mutablePages[j], mutablePages[j + 1])) {
                        atLeastOnWrongOrder = true
                        val temp = mutablePages[j]
                        mutablePages.removeAt(j)
                        mutablePages.add(j + 1, temp)
                    }
                }
            }
            return mutablePages
        }
    }

    fun part1(input: List<String>): Long {
        val updateManager = UpdateManager()
        var sumOfMiddlePageNums = 0L

        input.forEach { line ->
            if (line.contains("|")) {
                val pageOrder = line.split("|").map { it.toInt() }
                updateManager.addPageOrder(pageOrder[0], pageOrder[1])
            } else if (line.isNotBlank()) {
                val pageUpdates = line.split(",").map { it.toInt() }
                if (updateManager.isUpdateOrderRight(pageUpdates)) {
                    sumOfMiddlePageNums += pageUpdates[pageUpdates.size / 2]
                }
            }
        }
        return sumOfMiddlePageNums
    }

    fun part2(input: List<String>): Long {
        val updateManager = UpdateManager()
        var sumOfMiddlePageNums = 0L

        input.forEach { line ->
            if (line.contains("|")) {
                val pageOrder = line.split("|").map { it.toInt() }
                updateManager.addPageOrder(pageOrder[0], pageOrder[1])
            } else if (line.isNotBlank()) {
                val pageUpdates = line.split(",").map { it.toInt() }
                if (!updateManager.isUpdateOrderRight(pageUpdates)) {
                    val rightPageUpdates = updateManager.fixUpdateOrderRight(pageUpdates)
                    sumOfMiddlePageNums += rightPageUpdates[rightPageUpdates.size / 2]
                }
            }
        }
        return sumOfMiddlePageNums
    }

    val testInput1 = readInput("Day05_test")
    check(part1(testInput1) == 143L)
    check(part2(testInput1) == 123L)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}
