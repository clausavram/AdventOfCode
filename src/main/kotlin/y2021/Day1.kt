package y2021

import util.FileType
import util.getFile

class Day1

fun main() {
    val lines = getFile(Day1::class, FileType.INPUT).readLines()
    val numbers = lines.map { it.toInt() }
    partOne(numbers)
    partTwo(numbers)
}

private fun partOne(numbers: List<Int>) {
    var prevNumber = numbers[0]
    var depthIncreases = 0
    for (number in numbers) {
        if (number > prevNumber) {
            depthIncreases++
        }
        prevNumber = number
    }
    println("Part 1: Depth increases: $depthIncreases")
}

private fun partTwo(numbers: List<Int>) {
    var prevSum = numbers[0] + numbers[1] + numbers[2]
    var depthIncreases = 0
    for (crtIdx in 3 until numbers.size) {
        val crtSum = prevSum - numbers[crtIdx - 3] + numbers[crtIdx]
        if (crtSum > prevSum) {
            depthIncreases++
        }
        prevSum = crtSum
    }
    println("Part 2: 3-measurement sliding window depth increases: $depthIncreases")
}