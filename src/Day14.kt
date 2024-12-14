fun main() {

    data class Point(val row: Int, val col: Int) {

        fun getQuadrant(maxRows: Int, maxCols: Int): Int {
            val halfRow = maxRows / 2
            val halfCol = maxCols / 2
            return when {
                row < halfRow && col < halfCol -> 0
                row < halfRow && col >= maxCols - halfCol -> 1
                row >= maxRows - halfRow && col < halfCol -> 2
                row >= maxRows - halfRow && col >= maxCols - halfCol -> 3
                else -> -1
            }
        }
    }

    data class Robot(
        val velocity: Point,
        val startingPoint: Point,
    ) {
        fun move(steps: Int, maxRows: Int, maxCols: Int): Point {
            var pointN = startingPoint.copy(
                row = (startingPoint.row + velocity.row * steps) % maxRows,
                col = (startingPoint.col + velocity.col * steps) % maxCols,
            )
            pointN = pointN.copy(
                row = if (pointN.row < 0) pointN.row + maxRows else pointN.row,
                col = if (pointN.col < 0) pointN.col + maxCols else pointN.col,
            )
            return pointN
        }

    }

    fun List<String>.toRobots(): List<Robot> {
        val regex = Regex(".*?(-?\\d+).*?(-?\\d+).*?(-?\\d+).*?(-?\\d+)")
        return map { line ->
            val nums = regex.find(line)!!
            Robot(
                startingPoint = Point(
                    col = nums.groups[1]!!.value.toInt(),
                    row = nums.groups[2]!!.value.toInt(),
                ),
                velocity = Point(
                    col = nums.groups[3]!!.value.toInt(),
                    row = nums.groups[4]!!.value.toInt(),
                ),
            )
        }
    }

    class RobotsManager(
        val robots: List<Robot>,
    ) {
        fun getSafetyFactor(steps: Int, maxRows: Int, maxCols: Int): Long {
            val pointsAfterSteps = robots.map {
                it.move(steps = steps, maxRows = maxRows, maxCols = maxCols)
            }
            val factors = mutableMapOf<Int, Int>()
            pointsAfterSteps.forEach { robot ->
                val quadrant = robot.getQuadrant(maxRows = maxRows, maxCols = maxCols)
                if (quadrant >= 0) {
                    factors[quadrant] = factors.getOrDefault(quadrant, 0) + 1
                }
            }

            return factors.values.fold(1L) { acc, sum ->
                acc * sum
            }
        }

        fun findXMasTree(steps: Int, maxRows: Int, maxCols: Int): Int {
            repeat(steps) { i ->
                val pointsAfterSteps = robots.map {
                    it.move(steps = i, maxRows = maxRows, maxCols = maxCols)
                }
                if (!pointsAfterSteps.haveConflicts()) {
                    pointsAfterSteps.printGrid(maxRows, maxCols)
                    return i
                }
            }
            return -1
        }

        private fun List<Point>.haveConflicts(): Boolean {
            val positions = mutableSetOf<Point>()
            forEach {
                if (positions.contains(it)) return true
                positions.add(it)
            }
            return false
        }

        private fun List<Point>.printGrid(maxRows: Int, maxCols: Int) {
            val grid = Array(maxRows) { Array(maxCols) { 0 } }
            forEach { grid[it.row][it.col]++ }
            grid.forEach {
                println(it.joinToString(separator = "") { if (it == 0) " " else "*" })
            }
        }
    }

    fun part1(input: List<String>, maxRows: Int, maxCols: Int): Long {
        return RobotsManager(input.toRobots())
            .getSafetyFactor(
                steps = 100,
                maxRows = maxRows,
                maxCols = maxCols,
            )
    }

    fun part2(input: List<String>, maxRows: Int, maxCols: Int): Int {
        return RobotsManager(input.toRobots()).findXMasTree(
            steps = 100000,
            maxRows = maxRows,
            maxCols = maxCols,
        )
    }

    val testInput11 = readInput("Day14_test")
    check(part1(input = testInput11, maxRows = 7, maxCols = 11) == 12L)

    val input = readInput("Day14")
    part1(input = input, maxRows = 103, maxCols = 101).println()
    part2(input, maxRows = 103, maxCols = 101).println()
}
