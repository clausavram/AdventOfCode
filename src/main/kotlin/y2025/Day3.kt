package y2025

import util.FileType
import util.getFile
import kotlin.streams.toList


fun main() {
    val batteryBanks = getFile(object {}, FileType.INPUT).readLines()
        .map { it.chars().map { c -> c.toChar().digitToInt() }.toList() }

    solvePart1(batteryBanks)
    solvePart2(batteryBanks)

}
private fun solvePart1(batteryBanks: List<List<Int>>) {
    val result = batteryBanks.sumOf { batteryBank -> getJoltiestBattery2Digits(batteryBank) }
    println("Part 1: $result")
}

private fun getJoltiestBattery2Digits(batteryBank: List<Int>): Int {
    val firstDigitCandidates = batteryBank.subList(0, batteryBank.size - 1)
    val maxFirstDigit = firstDigitCandidates.max()
    var maxSecondDigit = 0
    firstDigitCandidates.forEachIndexed { firstIndex, crtFirstDigit ->
        try {
            if (crtFirstDigit != maxFirstDigit) {
                return@forEachIndexed
            }
            val crtSecondDigit = batteryBank.subList(firstIndex + 1, batteryBank.size).max()
            if (crtSecondDigit > maxSecondDigit) {
                maxSecondDigit = crtSecondDigit
            }
        } catch (e: Exception) {
            System.err.println("Error on 1st index: $firstIndex, crtFirstDigit: $crtFirstDigit")
            throw Exception(e)
        }
    }
    val joltiness = maxFirstDigit * 10 + maxSecondDigit
    return joltiness
}

private fun solvePart2(batteryBanks: List<List<Int>>) {
    val result = batteryBanks.sumOf { batteryBank ->
        val joltiness = getJoltiestBatteryNDigits(batteryBank, 12).reverse()
        println("${batteryBank.str()} -> joltiness: $joltiness")
        return@sumOf joltiness
    }
    println("Part 2: $result")
}

// assumes no zeroes in battery values (would mess up reversal)
private fun getJoltiestBatteryNDigits(batteryBank: List<Int>, remainingLength: Int): Long {
    if (remainingLength == 0) return 0

    val crtBatteryCandidates = batteryBank.subList(0, batteryBank.size - remainingLength + 1)
    val crtBatteryValue = crtBatteryCandidates.max()
//    println("> [${batteryBank.str()}] crt digit: $crtBatteryValue - candidates: ${crtBatteryCandidates.str()}, remainingLength: $remainingLength")

    val maxJoltiness = crtBatteryCandidates.withIndex()
        .filter { it.value == crtBatteryValue }
        .map { crtBatteryCandidate ->
            val nextBatteriesJoltiness =
                getJoltiestBatteryNDigits(batteryBank.subList(crtBatteryCandidate.index + 1, batteryBank.size), remainingLength - 1)
            val crtJoltiness = crtBatteryValue + nextBatteriesJoltiness * 10
//            println("[${batteryBank.str()}, ln:$remainingLength] crt joltiness: $crtJoltiness, crt battery: value=$crtBatteryValue, idx=${crtBatteryCandidate.index}")
            return@map crtJoltiness
        }.max()
//    println("< [${batteryBank.str()}, ln:$remainingLength] joltiness: $maxJoltiness\n")
    return maxJoltiness

}

private fun List<Int>.str() = this.joinToString("")

private fun Long.reverse(): Long {
    var remainder = this
    var result = 0L
    while (remainder > 0) {
        result = result * 10 + remainder % 10
        remainder /= 10
    }
    return result
}