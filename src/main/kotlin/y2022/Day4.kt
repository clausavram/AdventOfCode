package y2022

import util.FileType
import util.getFile

private fun IntRange.contains(subRange: IntRange) = this.contains(subRange.first) && this.contains(subRange.last)
private fun IntRange.containsOrContained(that: IntRange) = this.contains(that) || that.contains(this)
private fun IntRange.overlaps(that: IntRange) = that.first in this || that.last in this || first in that || last in that

fun main() {
    val lines = getFile(object {}, FileType.INPUT).readLines()
    val lineRegex = "(\\d+)-(\\d+),(\\d+)-(\\d+)".toRegex()
    val elfPairs = mutableListOf<Pair<IntRange, IntRange>>()
    for (line in lines) {
        val matchResult = lineRegex.find(line)!!
        val leftElf = matchResult.groups[1]!!.value.toInt() .. matchResult.groups[2]!!.value.toInt()
        val rightElf = matchResult.groups[3]!!.value.toInt() .. matchResult.groups[4]!!.value.toInt()
        elfPairs += leftElf to rightElf
    }

    println("Part 1: ${elfPairs.count { it.first.containsOrContained(it.second) }}")
    println("Part 2: ${elfPairs.count { it.first.overlaps(it.second) }}")
}