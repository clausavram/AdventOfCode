package y2025

import util.FileType
import util.getFile

fun main() {
    val rawLines = getFile(object {}, FileType.INPUT).readLines()

    solvePart1(rawLines)
    solvePart2(rawLines)
}

private fun solvePart1(rawLines: List<String>) {
    val splitLines = rawLines
        .map { line -> line.trim().split(Regex(" +")) }
        .toMutableList()

    val operations: List<String> = splitLines.removeLast()
    val operands: List<List<Long>> = splitLines.map { line -> line.map { it.toLong() } }

    val totalResult = operations.mapIndexed { index, operand ->
        val (initialValue: Long, operation: (Long, Long) -> Long) = extractInitialValueAndOperation(operand)

        val columnResult: Long = operands.asSequence()
            .map { it[index] }
            .fold(initialValue, operation)

        columnResult
    }.sum()
    println("Part 1: $totalResult")
}

private fun solvePart2(rawLines: List<String>) {
    // ordering data right-to-left
    val operandsInputLines = rawLines.toMutableList()
    val operators = operandsInputLines.removeLast().trim().split(Regex(" +")).reversed().toMutableList()
    val inputRows = operandsInputLines.size
    val inputCols = operandsInputLines[0].length
    val operandsTranslatedLines = ((inputCols - 1) downTo 0).map { inputCol ->
        (0 until inputRows).asSequence()
            .map { inputRow -> operandsInputLines[inputRow][inputCol] }
            .joinToString("")
    }
    println(operandsTranslatedLines.joinToString("\n"))

    var totalResult = 0L
    var crtOperator: String = operators.removeFirst()
    var (crtResult, crtOperation) = extractInitialValueAndOperation(crtOperator)

    for (translatedLine in operandsTranslatedLines) {
        if (translatedLine.isBlank()) {
            totalResult += crtResult
            println("Added $crtResult")
            crtOperator = operators.removeFirst()
            val (newResult, newOperation) = extractInitialValueAndOperation(crtOperator)
            crtResult = newResult
            crtOperation = newOperation
        } else {
            val crtOperand = translatedLine.trim().toLong()
            crtResult = crtOperation.invoke(crtResult, crtOperand)
        }
    }
    totalResult += crtResult
    println("Added $crtResult")

    println("Part 2: $totalResult")
}


private fun extractInitialValueAndOperation(operand: String): Pair<Long, (Long, Long) -> Long> = when (operand) {
    "+" -> 0L to { a: Long, b: Long -> a + b }
    "*" -> 1L to { a: Long, b: Long -> a * b }
    else -> throw IllegalArgumentException("Unknown operation '$operand'")
}
