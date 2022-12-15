package y2022

import util.FileType
import util.getFile


fun main() {
    val lines = getFile(object {}, FileType.INPUT).readLines()

    val elves = mutableListOf(0)
    for (line in lines) {
        if (line.isEmpty()) {
            elves.add(0)
        } else {
            elves[elves.size - 1] += line.toInt()
        }
    }
    elves.sortDescending()

    println("Part 1: greatest calories on one elf: ${elves.first()}")
    println("Part 2: sum of top 3 elves' calories: ${elves[0] + elves[1] + elves[2]}")
}
