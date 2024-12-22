fun main() {

    class SecretNumberManager {

        fun next(current: Long, n: Int): Long {
            var counter = 0
            var secret = current
            while (counter < n) {
                secret = nextSecret(secret)
                counter++
            }
            return secret
        }

        fun bestSeqPrice(secrets: List<Int>, n: Int): Int {
            val mapOfSeqsWithMaxPrice = secrets.map { mapOfSeqsWithMaxPrice(it, n) }
            val totalPriceMap = mutableMapOf<Int, Int>()
            mapOfSeqsWithMaxPrice.forEach {
                it.entries.forEach { (seq, price) ->
                    totalPriceMap[seq] = totalPriceMap.getOrDefault(seq, 0) + price
                }
            }
            return totalPriceMap.maxOf { it.value }
        }

        private fun mapOfSeqsWithMaxPrice(current: Int, n: Int): Map<Int, Int> {
            var counter = 0
            var secret = current.toLong()
            var seq = 0 // 1 byte per diff. 4 bytes = Int
            val map = mutableMapOf<Int, Int>()
            while (counter < n) {
                val newSecret = nextSecret(secret)
                val price = (secret % 10).toInt()
                val newPrice = (newSecret % 10).toInt()
                val diff = (newPrice - price) and 0xFF // 0xFF only keeps the lower byte
                seq = seq shl 8
                seq = seq or diff
                if (counter >= 3) {
                    map.putIfAbsent(seq, newPrice)
                }
                counter++
                secret = newSecret
            }
            return map
        }

        private fun nextSecret(currentSecret: Long): Long {
            return currentSecret
                .mixWith { it shl 6 }
                .prune()
                .mixWith { it shr 5 }
                .prune()
                .mixWith { it shl 11 }
                .prune()
        }

        fun Long.mixWith(other: (curr: Long) -> Long): Long = this xor other(this)
        fun Long.prune() = this and 0xFFFFFF
    }

    fun part1(input: List<String>): Long {
        val secretNumberManager = SecretNumberManager()
        return input.fold(0L) { acc, secret ->
            acc + secretNumberManager.next(secret.toLong(), 2000)
        }
    }

    fun part2(input: List<String>): Int {
        val secretNumberManager = SecretNumberManager()
        val secrets = input.map { it.toInt() }
        return secretNumberManager.bestSeqPrice(secrets, 2000)
    }

    val testInput11 = readInput("Day22_test")
    check(part1(testInput11) == 37327623L)
    val testInput12 = readInput("Day22_test2")
    check(part2(testInput12) == 23)

    val input = readInput("Day22")
    part1(input).println()
    part2(input).println()
}
