import kotlin.math.pow

fun main() {

    class ProgramExecutor(
        aStartValue: Long,
        val commands: List<Long>,
    ) {
        var registerA: Long = aStartValue
        var registerB: Long = 0
        var registerC: Long = 0

        private fun adv(operand: Long) {
            registerA = (registerA / 2.0.pow(operand.toDouble())).toLong()
        }

        private fun bxl(operand: Long) {
            registerB = registerB xor operand
        }

        private fun bst(operand: Long) {
            registerB = operand % 8
        }

        private fun jnz(operand: Long): Long {
            return if (registerA != 0L) operand else -1
        }

        private fun bxc(operand: Long) {
            registerB = registerB xor registerC
        }

        private fun out(operand: Long): Long {
            return (operand % 8)
        }

        private fun bdv(operand: Long) {
            registerB = (registerA / 2.0.pow(operand.toDouble())).toLong()
        }

        private fun cdv(operand: Long) {
            registerC = (registerA / 2.0.pow(operand.toDouble())).toLong()
        }

        fun execute(returnFirstOutput: Boolean = false): List<Long> {
            return buildList {
                var pointer = 0
                while (pointer < commands.lastIndex) {
                    val operator = commands[pointer]
                    val literalOperand = commands[pointer + 1]
                    val comboOperand = when (commands[pointer + 1]) {
                        in 0..3 -> literalOperand
                        4L -> registerA
                        5L -> registerB
                        6L -> registerC
                        else -> -1 // Not valid
                    }

                    when (operator) {
                        0L -> adv(comboOperand)
                        1L -> bxl(literalOperand)
                        2L -> bst(comboOperand)
                        3L -> {
                            val result = jnz(literalOperand)
                            if (result != -1L) {
                                pointer = result.toInt()
                                continue
                            }
                        }

                        4L -> bxc(literalOperand)
                        5L -> {
                            val output = out(comboOperand)
                            add(output)
                            if (returnFirstOutput) return this
                        }

                        6L -> bdv(comboOperand)
                        7L -> cdv(comboOperand)
                    }
                    pointer += 2
                }
            }
        }
    }

    fun List<String>.tpProgramExecutor(): Pair<List<Long>, List<Long>> {
        val regex = Regex(".*?(\\d+)")
        val registers = buildList {
            add(regex.find(this@tpProgramExecutor[0])!!.groups[1]!!.value)
            add(regex.find(this@tpProgramExecutor[1])!!.groups[1]!!.value)
            add(regex.find(this@tpProgramExecutor[2])!!.groups[1]!!.value)
        }.map { it.toLong() }
        val commands = this[4].split(" ")[1].split(",").map { it.toLong() }
        return registers to commands
    }

    fun part1(input: List<String>): String {
        val (registers, commands) = input.tpProgramExecutor()
        return ProgramExecutor(registers[0], commands).execute().joinToString(",")
    }

    fun solve(commands: List<Long>, index: Int, currRegisterA: Long): Long? {
        if (index < 0) return currRegisterA
        for (lowest3Bits in 0..7L) {
            val registerA = (currRegisterA shl 3) or lowest3Bits
            val output = ProgramExecutor(registerA, commands).execute(true)[0]
            if (output == commands[index]) {
                solve(commands, index - 1, registerA)?.run { return this }
            }
        }
        return null
    }

    fun part2(input: List<String>): Long {
        val (_, commands) = input.tpProgramExecutor()
        return solve(commands, commands.lastIndex, 0)!!
    }

    val testInput11 = readInput("Day17_test")
    check(part1(input = testInput11) == "4,6,3,5,6,3,5,2,1,0")
    val testInput12 = readInput("Day17_test2")
    check(part2(input = testInput12) == 117440L)

    val input = readInput("Day17")
    part1(input = input).println()
    part2(input = input).println()
}
