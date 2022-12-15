package y2022

import util.FileType
import util.getFile

private fun priority(c: Char): Int = when (c) {
    in 'a'..'z' -> 1 + (c - 'a')
    in 'A'..'Z' -> 27 + (c - 'A')
    else -> throw IllegalArgumentException("Letter '$c' not recognized")
}

fun main() {
    val rucksacks: List<String> = getFile(object {}, FileType.INPUT).readLines()

    // Part 1 - split each rucksack into halves:
    val part1 = rucksacks
        .map { it.substring(0 until it.length / 2) to it.substring(it.length / 2) }
        .map { it.first.toCollection(LinkedHashSet()) to it.second.toCollection(LinkedHashSet()) }
        .map { it.first.intersect(it.second).first() }
        .sumOf { priority(it) }
    println("Part 1: $part1")

    // Part 2 - group by 3 rucksacks:
    val groupBadgePriorities = rucksacks.withIndex()
        .groupBy({ it.index / 3 }, { it.value.toSet() })
        .values
        .map { it[0].intersect(it[1]).intersect(it[2]).first() }
        .sumOf { priority(it) }
    println("Part 2: $groupBadgePriorities")
}