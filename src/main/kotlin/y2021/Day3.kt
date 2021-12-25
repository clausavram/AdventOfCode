package y2021

import util.FileType
import util.getFile
import kotlin.math.pow

class Day3

fun main() {
    val lines = getFile(Day3::class, FileType.INPUT).readLines()

    partOne(lines)
    partTwo(lines)
}

private fun computeOnesCount(binaryNumbers: List<String>, bitIndex: Int): Int {
    return binaryNumbers.asSequence()
        .map { it[bitIndex] }
        .count { it == '1' }
}

private fun binaryToInt(binaryNumber: String) =
    binaryNumber.toCharArray().map { it.digitToInt() }.fold(0) { acc, bit -> acc * 2 + bit }

private fun partOne(binaryNumbers: List<String>) {
    val bitCount = binaryNumbers[0].length
    val gammaBinary = (0 until bitCount)
        .map { if (computeOnesCount(binaryNumbers, it) >= (binaryNumbers.size + 1) / 2) '1' else '0' }
        .joinToString("")

    val gamma = binaryToInt(gammaBinary)
    val epsilon = gamma xor (2.0.pow(bitCount).toInt() - 1)
    println("Part 1: gamma = $gamma, epsilon = $epsilon, result = ${gamma * epsilon}")
}

private fun partTwo(binaryNumbers: List<String>) {
    val oxygenGeneratorBinary = computeRating(binaryNumbers) { ones, zeroes -> ones >= zeroes }
    val co2ScrubberBinary = computeRating(binaryNumbers) { ones, zeroes -> ones < zeroes }
    val oxygenGenerator = binaryToInt(oxygenGeneratorBinary)
    val co2Scrubber = binaryToInt(co2ScrubberBinary)

    print("Part 2: ")
    print("Oxygen: $oxygenGenerator ($oxygenGeneratorBinary), ")
    print("CO2: $co2Scrubber ($co2ScrubberBinary), ")
    println("Result: ${oxygenGenerator * co2Scrubber}")
}

private fun computeRating(binaryNumbers: List<String>, useOne: (Int, Int) -> Boolean): String {
    val ratingCandidates = binaryNumbers.toMutableList()
    for (bitIndex in 0 until binaryNumbers[0].length) {
        val onesCount = computeOnesCount(ratingCandidates, bitIndex)
        val wantedBit = if (useOne(onesCount, ratingCandidates.size - onesCount)) '1' else '0'
        ratingCandidates.removeAll { it[bitIndex] != wantedBit }

        if (ratingCandidates.size == 1) {
            break
        }
    }
    if (ratingCandidates.size != 1) {
        error("Couldn't narrow down candidates to exactly 1. Got: $ratingCandidates")
    }
    return ratingCandidates[0]
}