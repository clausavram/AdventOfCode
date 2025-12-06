package y2024

import util.FileType
import util.getFile
import kotlin.math.absoluteValue

fun main() {
    val lines = getFile(object {}, FileType.INPUT).readLines()
    val input = lines.map { it.split(Regex(" +")) }
        .map { pair -> pair[0].toInt() to pair[1].toInt() }

    println("Part 1: ${partOne(input)}")
    println("Part 2: ${partTwo(input)}")
}

private fun partOne(input: List<Pair<Int, Int>>): Int {
    val leftList = input.map { it.first }.sorted()
    val rightList = input.map { it.second }.sorted()
    var sum = 0
    for (i in leftList.indices) {
        sum += (leftList[i] - rightList[i]).absoluteValue
    }
    return sum
}

private fun partTwo(input: List<Pair<Int, Int>>): Int {
    val leftList = input.map { it.first }.sorted()
    val rightList = input.map { it.second }.sorted()
    val rightHistogram = rightList.groupingBy { it }.eachCount()
    return leftList.map { it * rightHistogram.getOrDefault(it, 0) }.sum()
}