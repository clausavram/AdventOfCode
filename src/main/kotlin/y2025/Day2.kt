package y2025

import util.FileType
import util.getFile
import kotlin.math.log10
import kotlin.math.pow

fun main() {
    val ranges = getFile(object {}, FileType.INPUT).readText()
        .split(Regex("[,\n]"))      // off-spec new-line separator, for file legibility
        .map { range -> range.split('-').let { it[0].toLong() .. it[1].toLong() } }

    solvePart1(ranges)
    solvePart2(ranges)
}

private fun solvePart1(ranges: List<LongRange>) {
    val totalInvalidSum = ranges.sumOf { extractInvalidDoubledNumbers_bruteForce(it) }
    println("Part 1: $totalInvalidSum")
}

private fun extractInvalidDoubledNumbers_bruteForce(range: LongRange): Long {
    var sum = 0L
    for (number in range) {
        val digitCount = getDigitCount(number)       // calc can be done only when increasing digits
        if (digitCount % 2L == 1L) continue          // can be improved to skip directly to next usable number
        val halfNumberMask = 10.0.pow((digitCount / 2).toDouble()).toLong()
        val lowerHalf = number % halfNumberMask
        val upperHalf = number / halfNumberMask
        if (lowerHalf == upperHalf) {
            sum += number
        }
    }
    return sum
}

private fun solvePart2(ranges: List<LongRange>) {
    val totalInvalidSum = ranges.sumOf { sumInvalidGenericNumbers(it) }
    println("Part 2: $totalInvalidSum")
}

fun sumInvalidGenericNumbers(range: LongRange): Long {
    println("Range: $range")
    return range.filter { isNumberWithRepeatedSubSequences(it) }.sum()
}

private fun isNumberWithRepeatedSubSequences(number: Long): Boolean {
    val totalDigitCount = getDigitCount(number)
    if (totalDigitCount == 1L) {
        return false
    }
    factorsLoop@ for (subNumberDigitCount in getFactors(totalDigitCount)) {
        val subNumbersCount = totalDigitCount / subNumberDigitCount
        val moduloMask = 10.0.pow(subNumberDigitCount.toDouble()).toLong()

        val firstSubNumber = number % moduloMask
        var divider = 1L
        for (subNumberIteration in 2..subNumbersCount) {
            divider *= moduloMask
            val crtSubNumber = (number / divider) % moduloMask
            if (crtSubNumber != firstSubNumber) {
                continue@factorsLoop
            }
        }
        return true
    }
    return false
}

private val factorsMap: MutableMap<Long, List<Long>> = mutableMapOf()

private fun getDigitCount(number: Long): Long = log10(number.toDouble()).toLong() + 1

/** doesn't contain the number itself - we wouldn't split the original number anymore */
private fun getFactors(number: Long): List<Long> {
    return factorsMap.computeIfAbsent(number) {
        val factors = mutableListOf(1L)

        for (factorCandidate in 2..(number / 2)) {
            if (number % factorCandidate == 0L) {
                factors.add(factorCandidate)
            }
        }

        factors.sorted().distinct().toList()
    }
}
