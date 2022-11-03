package y2021

import util.FileType
import util.getFile
import java.util.*

private sealed interface ChunkValidity
private object Valid : ChunkValidity
private data class Incomplete(val completionScore: Long) : ChunkValidity
private data class Corrupted(val expectedChar: Char, val illegalChar: Char) : ChunkValidity

private val brackets = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')
private val openingBrackets = brackets.keys
private val closingBrackets = brackets.values.toSet()
private val illegalCharScores = mapOf(')' to 3L, ']' to 57L, '}' to 1197L, '>' to 25137L)
private val missingCharScores = mapOf(')' to 1L, ']' to 2L, '}' to 3L, '>' to 4L)

fun main() {
    val navigationChunks = getFile(object{}, FileType.INPUT).readLines().map { it.toCharArray() }
    partOne(navigationChunks)
    partTwo(navigationChunks)
}

private fun partOne(chunks: List<CharArray>) {
    val totalSyntaxErrorScore = chunks.asSequence()
        .map { it to findChunkValidity(it) }
        .filter { it.second is Corrupted }
        .sumOf { illegalCharScores[(it.second as Corrupted).illegalChar]!! }
    println("Part 1: total syntax error score: $totalSyntaxErrorScore")
}

private fun partTwo(chunks: List<CharArray>) {
    val autoCompletionScores = chunks.asSequence()
        .map { it to findChunkValidity(it) }
        .filter { it.second is Incomplete }
        .map { (it.second as Incomplete).completionScore }
        .sorted()
        .toList()
    println("Part 2: middle auto-completion score: ${autoCompletionScores[(autoCompletionScores.size + 1) / 2 - 1]}")
}

private fun findChunkValidity(chunk: CharArray): ChunkValidity {
    val expectedClosingBracketsStack = LinkedList<Char>()

    for (bracket in chunk) {
        if (bracket in openingBrackets) {
            val closingBracket = brackets[bracket]
            expectedClosingBracketsStack.push(closingBracket)
        } else if (bracket in closingBrackets) {
            val expectedClosingBracket = expectedClosingBracketsStack.pop()
            if (bracket != expectedClosingBracket) {
                return Corrupted(expectedClosingBracket, bracket)
            }
        } else {
            error("Illegal character: '$bracket'")
        }
    }

    if (expectedClosingBracketsStack.isNotEmpty()) {
        val completionScore = expectedClosingBracketsStack.map { missingCharScores[it]!! }
            .fold(0L) { acc, crtVal -> acc * 5 + crtVal }
        return Incomplete(completionScore)
    }
    return Valid
}