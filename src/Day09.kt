import java.util.*

fun main() {

    data class Gap(
        val index: Int,
        val count: Int,
    ) {
        val end = index + count - 1

        fun fillFirstCell() = fillNCell(1)
        fun fillNCell(n: Int) = Gap(index = index + n, count = count - n)
    }

    data class File(
        val id: Int,
        val index: Int,
        val count: Int,
    ) {
        val end = index + count - 1

        fun emptyLastCell() = File(id = id, index = index, count = count - 1)
    }

    fun String.toDisk(): Triple<Array<Int>, LinkedList<File>, LinkedList<Gap>> {
        val length = fold(0) { acc, c -> acc + c.digitToInt() }
        val arr = Array(size = length) { 0 }
        val gaps = LinkedList<Gap>()
        val files = LinkedList<File>()
        var isFile = true
        var index = 0
        var label = 0
        forEach { c ->
            val num = if (isFile) label++ else -1
            isFile = !isFile
            val count = c.digitToInt()
            if (num == -1 && count > 0) gaps.add(Gap(index = index, count = count))
            if (num >= 0) files.add(File(id = num, index = index, count = count))
            repeat(count) {
                arr[index++] = num
            }
        }
        return Triple(arr, files, gaps)
    }

    class DiskManager(
        private val disk: Array<Int>,
        private val files: LinkedList<File>,
        private val gaps: LinkedList<Gap>,
    ) {

        fun fillGapsFromEnd() {
            while (gaps.isNotEmpty()) {
                val firstGap = gaps.first()
                val lastFile = files.last
                if (firstGap.index >= lastFile.index) return

                disk[firstGap.index] = lastFile.id
                disk[lastFile.end] = -1
                gaps.removeFirst()
                firstGap.fillFirstCell().also {
                    if (it.count > 0) gaps.addFirst(it)
                }
                files.removeLast()
                lastFile.emptyLastCell().also {
                    if (it.count > 0) files.addLast(it)
                }
            }
        }

        fun fillGapsWithWholeFilesFromEnd() {
            for (file in files.reversed()) {
                val firstPossibleGapIndex = gaps.indexOfFirst { it.count >= file.count && it.end < file.index }
                val firstPossibleGap = gaps.getOrNull(firstPossibleGapIndex) ?: continue
                repeat(file.count) {
                    disk[firstPossibleGap.index + it] = file.id
                    disk[file.index + it] = -1
                }
                gaps.removeAt(firstPossibleGapIndex)
                firstPossibleGap.fillNCell(file.count).also {
                    if (it.count > 0) gaps.add(firstPossibleGapIndex, it)
                }
            }
        }

        fun checksumOfOrderedDisk(): Long {
            return disk.foldIndexed(0L) { index, acc, id ->
                acc + if (id == -1) 0 else id * index
            }
        }
    }

    fun part1(input: List<String>): Long {
        val (disk, files, gaps) = input[0].toDisk()
        return DiskManager(disk = disk, files = files, gaps = gaps)
            .apply { fillGapsFromEnd() }
            .checksumOfOrderedDisk()
    }

    fun part2(input: List<String>): Long {
        val (disk, files, gaps) = input[0].toDisk()
        return DiskManager(disk = disk, files = files, gaps = gaps)
            .apply { fillGapsWithWholeFilesFromEnd() }
            .checksumOfOrderedDisk()
    }

    val testInput1 = readInput("Day09_test")
    check(part1(testInput1) == 1928L)
    check(part2(testInput1) == 2858L)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}
