import Cell.BOX
import Cell.BOX_END
import Cell.BOX_START
import Cell.EMPTY
import Cell.WALL
import Direction.*
import IWarehouse.Point
import kotlin.math.abs
import kotlin.math.max

object Cell {
    const val WALL = '#'
    const val EMPTY = '.'
    const val BOX = 'O'
    const val BOX_START = '['
    const val BOX_END = ']'
}

interface IWarehouse {
    fun execute(command: Char)
    fun boxPoints(): List<Point>

    data class Point(val row: Int, val col: Int) {
        fun move(direction: Direction) = this.copy(row = row + direction.y, col = col + direction.x)
        fun left() = this.copy(row = row, col = col - 1)
        fun right() = this.copy(row = row, col = col + 1)
    }
}

fun main() {


    fun Char.toDirection() = when (this) {
        '<' -> Left
        '>' -> Right
        '^' -> Up
        'v' -> Down
        else -> error("Invalid command")
    }

    fun Array<CharArray>.toPoints(target: Char = BOX): List<Point> {
        return buildList {
            this@toPoints.forEachIndexed { row, line ->
                line.forEachIndexed { col, c ->
                    val point = Point(row, col)
                    if (c == target) add(point)
                }
            }
        }
    }

    data class Warehouse(
        val grid: Array<CharArray>,
        val staringPoint: Point,
    ) : IWarehouse {

        var currPoint: Point = staringPoint

        operator fun get(index: Point) = grid[index.row][index.col]

        fun findFirstEmptyCell(from: Point, direction: Direction): Point? {
            var currPoint = from
            var cell = this[currPoint]
            while (cell != WALL) {
                if (cell == EMPTY) return currPoint
                currPoint = currPoint.move(direction)
                cell = this[currPoint]
            }
            return null
        }

        fun shiftCells(start: Point, end: Point, direction: Direction) {
            val inverseDirection = direction.inverse()
            var currPoint = end
            while (currPoint != start) {
                val nextPoint = currPoint.move(inverseDirection)
                grid[currPoint.row][currPoint.col] = this[nextPoint]
                grid[nextPoint.row][nextPoint.col] = EMPTY
                currPoint = nextPoint
            }
        }

        override fun execute(command: Char) {
            val direction = command.toDirection()
            val newPoint = currPoint.move(direction)
            val cell = this[newPoint]
            if (cell == WALL) return
            if (cell == EMPTY) {
                currPoint = newPoint
                return
            }
            val firstEmptyBox = findFirstEmptyCell(newPoint, direction)
            if (firstEmptyBox != null) {
                shiftCells(start = newPoint, end = firstEmptyBox, direction = direction)
                currPoint = newPoint
            }
        }

        override fun boxPoints() = grid.toPoints()

    }

    data class LargeWarehouse(
        val warehouse: Warehouse,
    ) : IWarehouse {

        private operator fun get(index: Point) = warehouse[index]

        private fun findFirstEmptyCellVertical(from: Point, direction: Direction): Pair<Point, Point>? {
            val cell = this[from]
            return when (cell) {
                BOX_START -> {
                    findFirstEmptyCellVertical(from, from.right(), direction)
                }

                BOX_END -> {
                    findFirstEmptyCellVertical(from.left(), from, direction)
                }

                EMPTY -> from to from
                else -> null
            }
        }

        private fun findFirstEmptyCellVertical(
            fromLeft: Point,
            fromRight: Point,
            direction: Direction
        ): Pair<Point, Point>? {
            val cellLeft = this[fromLeft]
            val cellRight = this[fromRight]
            while (cellLeft != WALL || cellRight != WALL) {
                if (cellLeft == EMPTY && cellRight == EMPTY) return fromLeft to fromRight
                val nextPointLeft = findFirstEmptyCellVertical(fromLeft.move(direction), direction)
                val nextPointRight = findFirstEmptyCellVertical(fromRight.move(direction), direction)

                if (nextPointLeft == null || nextPointRight == null) {
                    return null
                }
                return nextPointLeft.first to nextPointRight.second
            }
            return null
        }

        private fun shiftCellsVertical(origin: Point, next: Point, direction: Direction) {
            val cell = this[next]
            when (cell) {
                BOX_START -> {
                    shiftCellsVertical(origin, next.move(direction), direction)
                    shiftCellsVertical(next.right(), next.move(direction).right(), direction)
                }
                BOX_END -> {
                    shiftCellsVertical(origin, next.move(direction), direction)
                    shiftCellsVertical(next.left(), next.move(direction).left(), direction)
                }
                EMPTY -> {
                    warehouse.shiftCells(origin, next, direction)
                }
            }
        }

        override fun execute(command: Char) {
            val direction = command.toDirection()
            val newPoint = warehouse.currPoint.move(direction)
            if (direction.isVertical()) {
                val firstEmptyCell = findFirstEmptyCellVertical(newPoint, direction) ?: return
                val maxRow = max(firstEmptyCell.first.row, firstEmptyCell.second.row)
                if (abs(maxRow - warehouse.currPoint.row) == 1) {
                    warehouse.currPoint = newPoint
                    return
                }
                shiftCellsVertical(
                    origin = newPoint,
                    next = newPoint,
                    direction = direction,
                )
                warehouse.currPoint = newPoint
            } else {
                warehouse.execute(command)
            }
        }

        override fun boxPoints(): List<Point> = warehouse.grid.toPoints(BOX_START)

    }

    fun List<String>.toWarehouse(): Warehouse {
        lateinit var startingPoint: Point
        val warehouse = buildList {
            this@toWarehouse.forEachIndexed { row, line ->
                if (line.isBlank()) return@buildList
                val botIndex = line.indexOfFirst { it == '@' }
                if (botIndex >= 0) startingPoint = Point(row, botIndex)
                add(line.toCharArray().apply { if (botIndex >= 0) this[botIndex] = '.' })
            }
        }.toTypedArray()
        return Warehouse(warehouse, startingPoint)
    }

    fun String.toEnlargeCharArray(): CharArray {
        val arr = CharArray(length * 2) { EMPTY }
        this.forEachIndexed { index, c ->
            if (c == WALL) {
                arr[index * 2] = WALL
                arr[index * 2 + 1] = WALL
            } else if (c == BOX) {
                arr[index * 2] = BOX_START
                arr[index * 2 + 1] = BOX_END
            }
        }
        return arr
    }

    fun List<String>.toLargeWarehouse(): IWarehouse {
        lateinit var startingPoint: Point
        val warehouse = buildList {
            this@toLargeWarehouse.forEachIndexed { row, line ->
                if (line.isBlank()) return@buildList
                val botIndex = line.indexOfFirst { it == '@' }
                if (botIndex >= 0) startingPoint = Point(row, botIndex * 2)
                add(line.toEnlargeCharArray())
            }
        }.toTypedArray()
        return LargeWarehouse(Warehouse(warehouse, startingPoint))
    }

    fun List<String>.toCommands(): String {
        var seenEmptyLine = false
        return buildString {
            this@toCommands.forEach { line ->
                if (line.isBlank()) seenEmptyLine = true
                else if (seenEmptyLine) append(line.toCharArray())
            }
        }
    }

    class WarehouseManager(
        val warehouse: IWarehouse,
    ) {

        fun execute(commands: String) {
            commands.forEach { command ->
                warehouse.execute(command)
            }
        }

        fun gpsScore(): Long {
            return warehouse.boxPoints().fold(0L) { acc, box ->
                acc + box.row * 100 + box.col
            }
        }

    }

    fun part1(input: List<String>): Long {
        val warehouse = input.toWarehouse()
        val commands = input.toCommands()
        val warehouseManager = WarehouseManager(warehouse)
        warehouseManager.execute(commands)
        return warehouseManager.gpsScore()
    }

    fun part2(input: List<String>): Long {
        val warehouse = input.toLargeWarehouse()
        val commands = input.toCommands()
        val warehouseManager = WarehouseManager(warehouse)
        warehouseManager.execute(commands)
        return warehouseManager.gpsScore()
    }

    val testInput11 = readInput("Day15_test")
    check(part1(input = testInput11) == 10092L)
    check(part2(input = testInput11) == 9021L)

    val input = readInput("Day15")
    part1(input = input).println()
    part2(input = input).println()
}
