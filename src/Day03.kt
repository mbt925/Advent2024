fun main() {

    data class Multiplication(
        val a: Int,
        val b: Int
    ) {
        constructor(a: String, b: String) : this(a.toInt(), b.toInt())

        fun mul() = a * b
    }

    fun String.extractEnabledMuls(): List<Multiplication> {
        val regex = Regex("mul\\((\\d{1,3}),(\\d{1,3})\\)|do\\(\\)|don't\\(\\)")
        var isEnabled = true
        return buildList {
            regex.findAll(this@extractEnabledMuls).forEach {
                val isDo = it.value == "do()"
                val isDonot = it.value == "don't()"
                when {
                    isDo -> isEnabled = true
                    isDonot -> isEnabled = false
                    else -> {
                        if (isEnabled) {
                            add(Multiplication(it.groups[1]!!.value, it.groups[2]!!.value))
                        }
                    }
                }
            }
        }
    }

    fun String.extractMuls(): List<Multiplication> {
        val regex = Regex("mul\\((\\d{1,3}),(\\d{1,3})\\)")
        return buildList {
            regex.findAll(this@extractMuls).forEach {
                add(Multiplication(it.groups[1]!!.value, it.groups[2]!!.value))
            }
        }
    }

    fun part1(input: List<String>): Int {
        val oneBigLine = input.joinToString(separator = "")
        return oneBigLine.extractMuls().sumOf { it.mul() }
    }

    fun part2(input: List<String>): Int {
        val oneBigLine = input.joinToString(separator = "")
        return oneBigLine.extractEnabledMuls().sumOf { it.mul() }
    }

    val testInput1 = readInput("Day03_test")
    check(part1(testInput1) == 161)
    check(part2(testInput1) == 48)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
