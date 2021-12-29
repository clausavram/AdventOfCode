package y2021

import util.FileType
import util.getFile
import java.lang.AssertionError
import java.util.*

private class Day8

private data class Entry(val signalPatterns: List<String>, val outputValues: List<String>)

private val segmentsBitsToDigit = mapOf(
    bitSet('a', 'b', 'c', 'e', 'f', 'g') to 0,
    bitSet('c', 'f') to 1,
    bitSet('a', 'c', 'd', 'e', 'g') to 2,
    bitSet('a', 'c', 'd', 'f', 'g') to 3,
    bitSet('b', 'c', 'd', 'f') to 4,
    bitSet('a', 'b', 'd', 'f', 'g') to 5,
    bitSet('a', 'b', 'd', 'e', 'f', 'g') to 6,
    bitSet('a', 'c', 'f') to 7,
    bitSet('a', 'b', 'c', 'd', 'e', 'f', 'g') to 8,
    bitSet('a', 'b', 'c', 'd', 'f', 'g') to 9,
)

fun bitSet(vararg segments: Char): BitSet {
    val result = BitSet(7)
    segments.forEach { result.set(it - 'a') }
    return result
}

fun main() {
    val entries = getFile(Day8::class, FileType.INPUT).readLines()
        .map { it.split(" | ") }
        .map { Entry(it[0].split(' '), it[1].split(' ')) }
    
    partOne(entries)
    partTwo(entries)
}

private fun partOne(entries: List<Entry>) {
    val uniqueDigitSegmentsCounts = listOf(2, 3, 4, 7)
    val triviallyIdentifiableDigitsCount = entries.asSequence()
        .flatMap { it.outputValues }
        .filter { it.length in uniqueDigitSegmentsCounts }
        .count()
    println("Part 1: # of trivially identifiable digits in output values: $triviallyIdentifiableDigitsCount")
}

private fun partTwo(entries: List<Entry>) {
    println("Part 2: sum of output values: ${entries.withIndex().sumOf { decipherEntry(it) }}")
}

private fun decipherEntry(entry: IndexedValue<Entry>): Int {
    try {
        val segmentMapping = decipherSegments(entry.value.signalPatterns)
        return decipherOutputValue(entry.value.outputValues, segmentMapping)
    } catch (e: AssertionError) {
        e.printStackTrace()
        error("Entry #${entry.index} couldn't be parsed")
    }
}

@Suppress("LocalVariableName", "SpellCheckingInspection")
private fun decipherSegments(signalPatterns: List<String>): Map<Char, Char> {
    val signalPatternSets = signalPatterns.map { it.toCharArray().toSet() }.groupBy { it.size }
    val mixed1_CF = signalPatternSets[2]!!.first() // 1. find mixed '1'
    val mixed4_BCDF = signalPatternSets[4]!!.first() // 2. find mixed '4'
    val mixed7_ACF = signalPatternSets[3]!!.first() // 3. find mixed '7'
    // 5. identify A
    val A = (mixed7_ACF - mixed1_CF).first()
    // 6. & 7. & 8. find mixed '9'
    val mixed9_ABCDFG = signalPatternSets[6]!!.first { it.containsAll(mixed4_BCDF) && A in it }
    val G = (mixed9_ABCDFG - mixed4_BCDF - setOf(A)).first()
    // 9. find mixed '3'
    val mixed3_ACDFG = signalPatternSets[5]!!
        .first { G in it && A in it && it.containsAll(mixed1_CF) }
    // 10. find D
    val D = (mixed3_ACDFG - mixed1_CF - setOf(A, G)).first()
    // 11. find B
    val B = (mixed4_BCDF - mixed1_CF - setOf(D)).first()
    // 12. & 13. find mixed '5' and F
    val ABDG = setOf(A, B, D, G)
    val mixed5_ABDFG = signalPatternSets[5]!!.first { it.containsAll(ABDG) }
    val F = (mixed5_ABDFG - ABDG).first()
    // 14. find C
    val C = (mixed1_CF - setOf(F)).first()
    // 15. find mixed '8'
    val mixed8_ABCDEFG = signalPatternSets[7]!!.first()
    val E = (mixed8_ABCDEFG - setOf(A, B, C, D, F, G)).first()

    return mapOf(A to 'a', B to 'b', C to 'c', D to 'd', E to 'e', F to 'f', G to 'g')
}

private fun decipherOutputValue(outputValues: List<String>, segmentMapping: Map<Char, Char>): Int {
    return outputValues.map { mixedWord -> bitSet(*mixedWord.map { mixedChar -> segmentMapping[mixedChar]!! }.toCharArray()) }
        .map { segmentBits -> segmentsBitsToDigit[segmentBits]!! }
        .fold(0) { acc, digit -> acc * 10 + digit}
}