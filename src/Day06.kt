fun main() {

    data class Point(val row: Int, val col: Int) {
        fun move(direction: Direction) = this.copy(row = row + direction.y, col = col + direction.x)
    }

    data class MovingPoint(val point: Point, val direction: Direction) {
        fun move() = this.copy(point = point.move(direction), direction = direction)

        fun rotate(): MovingPoint {
            val nextDirection = when (direction) {
                Direction.Left -> Direction.Up
                Direction.Right -> Direction.Down
                Direction.Up -> Direction.Right
                Direction.Down -> Direction.Left
                else -> error("Invalid direction")
            }
            return this.copy(point = point.move(nextDirection), direction = nextDirection)
        }
    }

    fun List<String>.findStartingPoint(): MovingPoint {
        val startingPointRegex = Regex("[\\^><v]")
        forEachIndexed { row, line ->
            startingPointRegex.find(line)?.let {
                return MovingPoint(
                    point = Point(row, it.range.first),
                    direction = when (it.value) {
                        ">" -> Direction.Right
                        "<" -> Direction.Left
                        "^" -> Direction.Up
                        "v" -> Direction.Down
                        else -> error("Invalid input")
                    },
                )

            }
        }
        error("No starting point found")
    }

    class GridManager(
        private val grid: Array<CharArray>,
        private val startPoint: MovingPoint,
    ) {
        private val obstacle = '#'

        constructor(list: List<String>) : this(
            grid = list.map { it.toCharArray() }.toTypedArray(),
            startPoint = list.findStartingPoint(),
        )

        fun get(point: Point): Char? {
            if (point.row in grid.indices) {
                if (point.col in grid[point.row].indices) {
                    return grid[point.row][point.col]
                }
            }
            return null
        }

        fun findNumOfCellToTheWayOut(): Int {
            val visited = mutableSetOf<Point>()
            var nextPoint: MovingPoint = startPoint
            while (true) {
                visited.add(nextPoint.point)
                val lookAheadPoint = nextPoint.move()
                nextPoint = get(lookAheadPoint.point)?.let {
                    if (it == obstacle) nextPoint.rotate() else lookAheadPoint
                } ?: break
            }
            return visited.size
        }

        private fun Point.endsUpInLoop(): Boolean {
            val visited = mutableSetOf<MovingPoint>()
            var nextPoint: MovingPoint = startPoint
            val potentialObstacle = this
            while (true) {
                if (visited.contains(nextPoint)) return true
                visited.add(nextPoint)
                val lookAheadPoint = nextPoint.move()
                nextPoint = get(lookAheadPoint.point)?.let {
                    if (it == obstacle || lookAheadPoint.point == potentialObstacle) nextPoint.rotate() else lookAheadPoint
                } ?: break
            }
            return false
        }

        fun findLoopExitPoints(): Int {
            val visited = mutableSetOf<MovingPoint>()
            val potentialObstacles = mutableSetOf<Point>()
            var nextPoint: MovingPoint = startPoint
            while (true) {
                visited.add(nextPoint)
                val lookAheadPoint = nextPoint.move()
                nextPoint = get(lookAheadPoint.point)?.let {
                    if (it == obstacle) {
                        nextPoint.rotate()
                    } else {
                        // test a potential obstacle
                        if (lookAheadPoint.point.endsUpInLoop()) potentialObstacles.add(lookAheadPoint.point)
                        lookAheadPoint
                    }
                } ?: break
            }
            return potentialObstacles.size
        }
    }

    fun part1(input: List<String>): Int {
        return GridManager(input).findNumOfCellToTheWayOut()
    }

    fun part2(input: List<String>): Int {
        return GridManager(input).findLoopExitPoints()
    }

    val testInput1 = readInput("Day06_test")
    check(part1(testInput1) == 41)
    check(part2(testInput1) == 6)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
