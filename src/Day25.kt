fun main() {

    data class LockOrKey(
        val pin: List<Int>,
        val maxHeight: Int,
        val isLock: Boolean,
    ) {
        fun fits(key: LockOrKey): Boolean {
            if (!this.isLock || key.isLock) return false
            pin.forEachIndexed { index, d ->
                if ((maxHeight - d) < key.pin[index]) return false
            }
            return true
        }
    }

    class DoorManager(
        private val locks: List<LockOrKey>,
        private val keys: List<LockOrKey>,
    ) {
        fun numOfFits(): Int {
            var fits = 0
            for (lock in locks) {
                for (key in keys) {
                    fits += if (lock.fits(key)) 1 else 0
                }
            }
            return fits
        }
    }

    fun List<String>.toLockOrKey(): LockOrKey {
        val isLock = this[0].startsWith('#')
        val pin = Array(this[0].length) { 0 }
        for (i in 1..<this.lastIndex) {
            for (j in this[i].indices) {
                pin[j] = pin[j] + if (this[i][j] == '#') 1 else 0
            }
        }
        return LockOrKey(pin.toList(), this.size - 2, isLock)
    }

    fun List<String>.toDoorManager(): DoorManager {
        val locksAndKeys = mutableListOf<LockOrKey>()
        for (i in indices step 8) {
            locksAndKeys.add(subList(i, i + 7).toLockOrKey())
        }
        return DoorManager(
            locks = locksAndKeys.filter { it.isLock },
            keys = locksAndKeys.filterNot { it.isLock },
        )
    }

    fun part1(input: List<String>): Int {
        return input.toDoorManager().numOfFits()
    }

    val testInput11 = readInput("Day25_test")
    check(part1(testInput11) == 3)

    val input = readInput("Day25")
    part1(input).println()
}
