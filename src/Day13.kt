fun main() {

    data class Equation(
        val jumpA: Long,
        val jumpB: Long,
        val target: Long,
    ) {
        fun hitsTarget(countA: Long, countB: Long) = (countA * jumpA + countB * jumpB) == target
    }

    data class Game(
        val equationX: Equation,
        val equationY: Equation,
    ) {

        // Two lines must have only one collision point
        fun findCheapestAB(maxCount: Long): Long {
            // determinant of the coefficient matrix
            val determinant = equationX.jumpA * equationY.jumpB - equationY.jumpA * equationX.jumpB
            if (determinant == 0L) return 0L

            val countA = (equationX.target * equationY.jumpB - equationY.target * equationX.jumpB) / determinant
            val countB = (equationY.target * equationX.jumpA - equationX.target * equationY.jumpA) / determinant

            if (countA <= 0 || countB <= 0) return 0L
            if (maxCount > 0 && (countA > maxCount || countB > maxCount)) return 0L
            if (!equationX.hitsTarget(countA, countB) || !equationY.hitsTarget(countA, countB)) return 0L

            return countA * 3 + countB
        }
    }

    fun List<String>.toGames(targetPlus: Long): List<Game> {
        val regex = Regex(".*?(\\d+).*?(\\d+)")
        return buildList {
            for (i in this@toGames.indices step 4) {
                val buttonA = regex.find(this@toGames[i])!!
                val buttonB = regex.find(this@toGames[i + 1])!!
                val prize = regex.find(this@toGames[i + 2])!!

                add(
                    Game(
                        equationX = Equation(
                            jumpA = buttonA.groups[1]!!.value.toLong(),
                            jumpB = buttonB.groups[1]!!.value.toLong(),
                            target = prize.groups[1]!!.value.toLong() + targetPlus,
                        ),
                        equationY = Equation(
                            jumpA = buttonA.groups[2]!!.value.toLong(),
                            jumpB = buttonB.groups[2]!!.value.toLong(),
                            target = prize.groups[2]!!.value.toLong() + targetPlus,
                        ),
                    )
                )
            }
        }
    }

    class EquationSolver(
        val games: Array<Game>,
    ) {
        constructor(input: List<String>, targetPlus: Long = 0) : this(input.toGames(targetPlus).toTypedArray())

        fun findCheapest(maxCount: Long = -1): Long {
            return games.fold(0L) { acc, game ->
                acc + game.findCheapestAB(maxCount)
            }
        }
    }

    fun part1(input: List<String>): Long {
        return EquationSolver(input).findCheapest(maxCount = 100)
    }

    fun part2(input: List<String>): Long {
        return EquationSolver(input, 10000000000000).findCheapest()
    }

    val testInput11 = readInput("Day13_test")
    check(part1(testInput11) == 480L)
    check(part2(testInput11) == 875318608908L)

    val input = readInput("Day13")
    part1(input).println()
    part2(input).println()
}
