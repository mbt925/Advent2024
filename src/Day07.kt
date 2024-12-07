import EquationOperator.*
import kotlin.system.measureTimeMillis

enum class EquationOperator {
    Sum, Mul, Concat;

    fun run(operand1: Long, operand2: Long) = when (this) {
        Sum -> operand1 + operand2
        Mul -> operand1 * operand2
        Concat -> (operand1 * operand2.multiplier() + operand2)
    }

    // Calculate the multiplier (10^digits in b)
    private fun Long.multiplier(): Long {
        var temp = this
        var multiplier = 1L
        while (temp > 0) {
            temp /= 10
            multiplier *= 10
        }
        return multiplier
    }
}

fun main() {

    data class Equation(
        val result: Long,
        val operands: List<Long>,
        val validOperators: Set<EquationOperator>,
    ) {
        fun isPossible(operators: List<EquationOperator> = emptyList()): Boolean {
            if (operators.size == operands.size - 1) {
                val currentResult = operands.foldIndexed(0L) { index, acc, num ->
                    when {
                        index == 0 -> num
                        else -> operators[index - 1].run(acc, num)
                    }
                }
                return currentResult == result
            }
            validOperators.forEach {
                if (isPossible(operators + it)) return true
            }
            return false
        }
    }

    fun String.toEquation(validOperators: Set<EquationOperator>): Equation {
        val split = this.split(':', ' ')
        return Equation(
            result = split[0].toLong(),
            operands = split.subList(2, split.size).map { it.toLong() },
            validOperators = validOperators,
        )
    }

    fun part1(input: List<String>): Long {
        return input.fold(0L) { acc, line ->
            val equation = line.toEquation(setOf(Sum, Mul))
            acc + if (equation.isPossible()) equation.result else 0
        }
    }

    fun part2(input: List<String>): Long {
        return input.fold(0L) { acc, line ->
            val equation = line.toEquation(setOf(Sum, Mul, Concat))
            acc + if (equation.isPossible()) equation.result else 0
        }
    }

    val testInput1 = readInput("Day07_test")
    check(part1(testInput1) == 3749L)
    check(part2(testInput1) == 11387L)

    val input = readInput("Day07")
    part1(input).println()
    val a = measureTimeMillis {
        part2(input).println()
    }
    println("$a ms")
}
