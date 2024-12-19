fun main() {

    class TowelManager(
        val patterns: List<String>,
    ) {

        private fun matchDesign(design: String): Boolean {
            if (design.isEmpty()) return true
            patterns.forEach { pattern ->
                if (design.startsWith(pattern) && matchDesign(design.substring(pattern.length))) return true
            }
            return false
        }

        private val matchesCountMap = mutableMapOf<String, Long>()

        private fun countDesignMatches(design: String): Long {
            if (design.isEmpty()) return 1
            matchesCountMap[design]?.let { return it }
            return patterns.fold(0L) { acc, pattern ->
                acc + if (design.startsWith(pattern)) countDesignMatches(design.substring(pattern.length)) else 0
            }.also { matchesCountMap[design] = it }
        }

        fun numOfPossibleDesigns(designs: List<String>): Int {
            return designs.fold(0) { acc, design ->
                acc + if (matchDesign(design)) 1 else 0
            }
        }

        fun numOfPossibleDesignMatches(designs: List<String>): Long {
            return designs.fold(0L) { acc, design ->
                acc + countDesignMatches(design)
            }
        }
    }

    fun List<String>.parse(): Pair<List<String>, List<String>> {
        val patterns = this[0].split(", ")
        val designs = this.subList(2, size)
        return patterns to designs
    }

    fun part1(input: List<String>): Int {
        val (patterns, designs) = input.parse()
        return TowelManager(patterns).numOfPossibleDesigns(designs)
    }

    fun part2(input: List<String>): Long {
        val (patterns, designs) = input.parse()
        return TowelManager(patterns).numOfPossibleDesignMatches(designs)
    }

    val testInput11 = readInput("Day19_test")
    check(part1(input = testInput11) == 6)
    check(part2(input = testInput11) == 16L)

    val input = readInput("Day19")
    part1(input = input).println()
    part2(input = input).println()
}
