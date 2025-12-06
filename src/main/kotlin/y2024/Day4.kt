package y2024

import util.FileType
import util.getFile
import kotlin.math.min

fun main() {
    val letterMatrix = getFile(object {}, FileType.INPUT).readLines()
    val searchStr = "XMAS"

    partOne(letterMatrix, searchStr)
    partTwo(letterMatrix)
}

private const val ENABLE_DEBUG = false
private fun debug(msg: String) {
    if (ENABLE_DEBUG) println(msg)
}

private fun partOne(letterMatrix: List<String>, searchStr: String) {
    debug("\n- no rotation:\n${letterMatrix.joinToString("\n") { it }}")
    val rotated0Count = getHorizontalAndDiagonalCount(letterMatrix, searchStr)
    debug("left→right + top-left→down-right count: $rotated0Count")

    val rotated90Matrix = rotate90DegCounterClockwise(letterMatrix)
    debug("\n- rotated 90:\n${rotated90Matrix.joinToString("\n") { it }}")
    val rotated90Count = getHorizontalAndDiagonalCount(rotated90Matrix, searchStr)
    debug("left→right + top-left→down-right count: $rotated90Count")

    val rotated180Matrix = rotate90DegCounterClockwise(rotated90Matrix)
    debug("\n- rotated 180:\n${rotated180Matrix.joinToString("\n") { it }}")
    val rotated180Count = getHorizontalAndDiagonalCount(rotated180Matrix, searchStr)
    debug("left→right + top-left→down-right count: $rotated180Count")

    val rotated270Matrix = rotate90DegCounterClockwise(rotated180Matrix)
    debug("\n- rotated 270:\n${rotated270Matrix.joinToString("\n") { it }}")
    val rotated270Count = getHorizontalAndDiagonalCount(rotated270Matrix, searchStr)
    debug("left→right + top-left→down-right count: $rotated270Count")

    println("\nPart 1: ${rotated0Count + rotated90Count + rotated180Count + rotated270Count}")
}

private fun getHorizontalAndDiagonalCount(letterMatrix: List<String>, searchStr: String): Int {
    val horizontalCount = countHorizontalOccurrences(letterMatrix, searchStr)
    val diagonalCount = countDiagonalOccurrences(letterMatrix, searchStr)
    return horizontalCount + diagonalCount
}

private fun rotate90DegCounterClockwise(letterMatrix: List<String>): List<String> {
    val srcRows = letterMatrix.size
    val srcCols = letterMatrix[0].length
    val dstMatrix = mutableListOf<String>()

    for (srcCol in 0 until srcCols) {
        val dstLine = (0 until srcRows).asSequence()
            .map { srcRow -> letterMatrix[srcRow][srcCol] }
            .joinToString("") { it.toString() }
        dstMatrix += dstLine
    }
    return dstMatrix.reversed()
}

private fun countHorizontalOccurrences(letterMatrix: List<String>, searchStr: String): Int =
    letterMatrix.sumOf { line -> countSubstrings(line, searchStr) }.apply {
        debug("horizontal count: $this")
    }

private fun countSubstrings(line: String, searchedSubStr: String): Int {
    var occurrences = 0
    var startIdx = 0
    while (true) {
        val newIdx = line.indexOf(searchedSubStr, startIdx)
        if (newIdx == -1) break
        startIdx = newIdx + searchedSubStr.length
        occurrences++
    }
    return occurrences
}

/** only go right + down; other directions covered by rotating whole matrix */
private fun countDiagonalOccurrences(letterMatrix: List<String>, searchSubStr: String): Int {
    val rows = letterMatrix.size
    val cols = letterMatrix[0].length
    var totalDiagonalCount = 0

    debug("lower half")
    for (startRow in 0 until (rows - searchSubStr.length + 1)) {
        val diagonalStrBuilder = StringBuilder()
        for (posInDiag in 0 until min(rows - startRow, cols)) {
            val row = posInDiag + startRow
            val col = posInDiag
            diagonalStrBuilder.append(letterMatrix[row][col])
        }
        val crtCount = countSubstrings(diagonalStrBuilder.toString(), searchSubStr)
        debug("$diagonalStrBuilder -> $crtCount")
        totalDiagonalCount += crtCount
    }

    debug("upper half")
    for (startCol in 1 until (cols - searchSubStr.length + 1)) {
        val diagonalStrBuilder = StringBuilder()
        for (posInDiag in 0 until min(rows, cols - startCol)) {
            val row = posInDiag
            val col = posInDiag + startCol
            diagonalStrBuilder.append(letterMatrix[row][col])
        }
        val crtCount = countSubstrings(diagonalStrBuilder.toString(), searchSubStr)
        debug("$diagonalStrBuilder -> $crtCount")
        totalDiagonalCount += crtCount
    }

    debug("diagonal count: $totalDiagonalCount")
    return totalDiagonalCount
}

private fun partTwo(grid: List<String>) {
    var xmasCount = 0
    val acceptableNeighbors = setOf("MMSS", "MSSM", "SSMM", "SMMS")
    for (row in 1 until grid.size - 1) {
        for (col in 1 until grid[0].length - 1) {
            if (grid[row][col] != 'A') continue
            val neighbors = "${grid[row-1][col-1]}${grid[row-1][col+1]}${grid[row+1][col+1]}${grid[row+1][col-1]}"
            if (neighbors in acceptableNeighbors) {
                xmasCount++
            }
        }
    }
    println("Part 2: $xmasCount")
}