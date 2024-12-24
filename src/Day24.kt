enum class Opr {
    AND, OR, XOR;

    fun apply(i1: Boolean, i2: Boolean) = when (this) {
        AND -> i1 and i2
        OR -> i1 or i2
        XOR -> i1 xor i2
    }

    companion object {
        fun from(label: String) = when (label) {
            "AND" -> AND
            "OR" -> OR
            "XOR" -> XOR
            else -> error("Invalid operator")
        }
    }
}

fun main() {

    data class Connection(
        val input1: String,
        val input2: String,
        val opr: Opr,
    ) {
        fun hasInputXY() = input1.first() in "xy" || input2.first() in "xy"
    }

    class GateManager(
        val wires: Map<String, Boolean>,
        val gates: Map<String, Connection>,
    ) {
        private val lastZ = gates.keys.fold(0) { acc, wire ->
            if (wire.startsWith("z")) {
                val curr = wire.substring(1).toInt()
                if (curr > acc) curr else acc
            } else acc
        }
        private val wiresOutput = mutableMapOf<String, Boolean>()

        fun run(gates: Map<String, Connection> = this.gates): Long {
            val zWires = mutableListOf<String>()
            gates.keys.forEach { output ->
                if (output.startsWith('z')) {
                    zWires.add(output)
                    gates.getOutput(output)
                }
            }
            return zWires.sorted().map { if (wiresOutput[it]!!) "1" else "0" }
                .reversed().joinToString("").toLong(2)
        }

        /*
            Full adder: A + B
            S = A XOR B XOR Ci (Carry-in)
            Co = (A AND B) OR (Ci AND (A XOR B))
         */
        fun findSwappedOutputs(): String {
            val lastZStr = "z%02d".format(lastZ)
            // All gates producing z must use XOR
            // Set1: Identify gates that output z but not use XOR (except last gate)
            val set1 = gates.keys.filter {
                gates[it]!!.opr != Opr.XOR && it.startsWith('z') && it != lastZStr
            }

            // Set2: XOR must only be applied to inputs or outputs, not for intermediate calculations
            val set2 = gates.keys.filter {
                val gate = gates[it]!!
                gate.opr == Opr.XOR && !gate.hasInputXY() && it.first() != 'z'
            }

            val mutableGates = gates.toMutableMap()
            // Each item from Set1 must be swapped with an item from Set2
            // Find the corresponding Z in Set1 for each item in Set2
            for (i2 in set2) {
                val i1 = set1.first { it == mutableGates.firstZThatUsesC(i2) }
                // swap the found pair to fix the output
                val temp = mutableGates[i2]!!
                mutableGates[i2] = mutableGates[i1]!!
                mutableGates[i1] = temp
            }

            // x + y must match z
            // Bits that don't match must be swapped
            // Find the first ONE in the xor as there is only one mismatch
            val firstDiff = (getWiresAsLong('x') + getWiresAsLong('y') xor run(mutableGates))
                .countTrailingZeroBits()
                .toString()

            // Find the gates that input x0[firstDiff] and y0[firstDiff]
            val falseMatches = mutableGates.filter {
                it.value.input1.endsWith(firstDiff) && it.value.input2.endsWith(firstDiff)
            }.keys

            return (set1 + set2 + falseMatches).sorted().joinToString(",")
        }

        private fun getWiresAsLong(type: Char) =
            wires.asSequence()
                .filter { it.key.startsWith(type) }
                .sortedBy { it.key }.map { if (it.value) "1" else "0" }
                .joinToString("")
                .reversed().toLong(2)

        private fun Map<String, Connection>.firstZThatUsesC(c: String): String? {
            val x = filter { it.value.input1 == c || it.value.input2 == c }.keys
            x.find { it.startsWith('z') }?.let { return "z%02d".format((it.drop(1).toInt() - 1)) }
            return x.firstNotNullOfOrNull { firstZThatUsesC(it) }
        }

        private fun Map<String, Connection>.getOutput(
            name: String,
        ): Boolean {
            wiresOutput[name]?.let { return it }
            wiresOutput[name] = this[name]
                ?.let {
                    it.opr.apply(
                        i1 = getOutput(it.input1),
                        i2 = getOutput(it.input2),
                    )
                }
                ?: wires[name]!!
            return wiresOutput[name]!!
        }

    }

    fun List<String>.toGateManager(): GateManager {
        val wires = mutableMapOf<String, Boolean>()
        val gates = mutableMapOf<String, Connection>()

        var firstSection = true
        forEach { line ->
            if (line.isBlank()) {
                firstSection = false
            } else {
                if (firstSection) {
                    val wire = line.split(": ")
                    wires[wire[0]] = wire[1] == "1"
                } else {
                    val gate = line.split(" ")
                    gates[gate[4]] = Connection(gate[0], gate[2], Opr.from(gate[1]))
                }
            }
        }

        return GateManager(wires, gates)
    }

    fun part1(input: List<String>): Long {
        return input.toGateManager().run()
    }

    fun part2(input: List<String>): String {
        return input.toGateManager().findSwappedOutputs()
    }

    val testInput11 = readInput("Day24_test")
    check(part1(testInput11) == 4L)
    val testInput12 = readInput("Day24_test2")
    check(part1(testInput12) == 2024L)

    val input = readInput("Day24")
    part1(input).println()
    part2(input).println()
}
