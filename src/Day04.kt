import Direction.*

enum class Direction(val x: Int, val y: Int) {
    Left(-1, 0), Right(1, 0), Up(0, -1), Down(0, 1),
    RightDown(1, 1), RightUp(1, -1), LeftDown(-1, 1), LeftUp(-1, -1)
}

fun main() {

    data class Point(val row: Int, val col: Int) {
        fun move(direction: Direction) = this.copy(row = row + direction.y, col = col + direction.x)
    }

    class CharTable(
        private val table: Array<CharArray>,
    ) {
        constructor(list: List<String>) : this(list.map { it.toCharArray() }.toTypedArray())

        val size = table.size

        fun get(point: Point): Char? {
            if (point.row in table.indices) {
                if (point.col in table[point.row].indices) {
                    return table[point.row][point.col]
                }
            }
            return null
        }
    }

    fun findMas(table: CharTable, curr: Point, direction: Direction, sequence: String = ""): Boolean {
        val currChar = table.get(curr) ?: return false
        val word = sequence + currChar
        if (word.contentEquals("MAS")) return true
        if ("MAS".startsWith(word)) return findMas(table, curr.move(direction), direction, word)
        return false
    }

    fun findStarShapeMas(table: CharTable, curr: Point): Boolean {
        val currChar = table.get(curr) ?: return false
        if (currChar == 'A') {
            // Check if this is the center of a star
            val firstDiagonal = findMas(table, curr.move(LeftUp), RightDown) || findMas(table, curr.move(RightDown), LeftUp)
            val secondDiagonal = findMas(table, curr.move(RightUp), LeftDown) || findMas(table, curr.move(LeftDown), RightUp)
            return firstDiagonal && secondDiagonal
        }
        return false
    }

    fun part1(input: List<String>): Int {
        val table = CharTable(input)
        var count = 0
        for (row in 0..<table.size) {
            for (col in 0..<table.size) {
                val currPoint = Point(row, col)
                if (table.get(currPoint) == 'X') {
                    for (direction in Direction.entries) {
                        count += if (findMas(table, currPoint.move(direction), direction)) 1 else 0
                    }
                }
            }
        }
        return count
    }

    fun part2(input: List<String>): Int {
        val table = CharTable(input)
        var count = 0
        for (row in 0..<table.size) {
            for (col in 0..<table.size) {
                count += if (findStarShapeMas(table, Point(row, col))) 1 else 0
            }
        }
        return count
    }

    val testInput1 = readInput("Day04_test")
    check(part1(testInput1) == 18)
    check(part2(testInput1) == 9)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
