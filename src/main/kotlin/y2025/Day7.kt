package y2025

import util.FileType
import util.Map2D
import util.getFile

fun main() {
    val manifoldMap = getFile(object {}, FileType.INPUT).readLines()
        .map { line -> line.map { char -> ManifoldItem.fromString(char) } }
        .let { Map2D.fromListOfLists(it) }

    solvePart1(manifoldMap)
    solvePart2(manifoldMap)
}

private enum class ManifoldItem(val char: Char) {
    START('S'),
    SPLITTER('^'),
    EMPTY('.'),
    BEAM('|');

    override fun toString() = char.toString()

    companion object {
        fun fromString(char: Char): ManifoldItem = entries.find { it.char == char }!!
    }
}

private fun solvePart1(originalMap: Map2D<ManifoldItem>) {
    val crtMap = originalMap.copy()
    var splitTimes = 0
    for (row in 1 until crtMap.rows) {
        for (col in 0 until crtMap.cols) {
            when (crtMap[row, col]) {
                ManifoldItem.EMPTY -> {
                    if (crtMap[row - 1, col] == ManifoldItem.BEAM || crtMap[row - 1, col] == ManifoldItem.START) {
                        crtMap[row, col] = ManifoldItem.BEAM
                    }
                }
                ManifoldItem.SPLITTER -> {
                    if (crtMap[row - 1, col] == ManifoldItem.BEAM) {
                        splitTimes++
                        if (col - 1 in 0 until crtMap.cols) {
                            crtMap[row, col - 1] = ManifoldItem.BEAM
                        }
                        if (col + 1 in 0 until crtMap.cols) {
                            crtMap[row, col + 1] = ManifoldItem.BEAM
                        }
                    }
                }
                ManifoldItem.BEAM, ManifoldItem.START -> {}
            }
        }
        println("\nFinished row $row:\n$crtMap")
    }
    println("Part 1: $splitTimes")
}

private fun solvePart2(originalMap: Map2D<ManifoldItem>) {
    val crtMap = originalMap.copy()
    val possibilitiesMap: Map2D<Long> = Map2D.withValues(crtMap.rows, crtMap.cols) { row, col ->
        if (crtMap[row, col] == ManifoldItem.START) 1 else 0
    }
    for (row in 1 until crtMap.rows) {
        for (col in 0 until crtMap.cols) {
            when (crtMap[row, col]) {
                ManifoldItem.EMPTY -> {
                    if (crtMap[row - 1, col] == ManifoldItem.BEAM || crtMap[row - 1, col] == ManifoldItem.START) {
                        crtMap[row, col] = ManifoldItem.BEAM
                        possibilitiesMap[row, col] = possibilitiesMap[row -1, col]
                    }
                }
                ManifoldItem.SPLITTER -> {
                    if (crtMap[row - 1, col] == ManifoldItem.BEAM) {
                        if (col - 1 in 0 until crtMap.cols) {
                            crtMap[row, col - 1] = ManifoldItem.BEAM
                            possibilitiesMap[row, col - 1] += possibilitiesMap[row - 1, col]
                        }
                        if (col + 1 in 0 until crtMap.cols) {
                            crtMap[row, col + 1] = ManifoldItem.BEAM
                            possibilitiesMap[row, col + 1] += possibilitiesMap[row - 1, col]
                        }
                    }
                }
                ManifoldItem.BEAM -> {
                    possibilitiesMap[row, col] += possibilitiesMap[row - 1, col]
                }
                ManifoldItem.START -> {}
            }
        }
    }

    for (row in 0 until possibilitiesMap.rows) {
        for (col in 0 until possibilitiesMap.cols) {
            if (crtMap[row, col] != ManifoldItem.BEAM) print(crtMap[row, col])
            else print(possibilitiesMap[row, col])
        }
        println()
    }
    println()

    val totalPossibilities = (0 until possibilitiesMap.cols).sumOf { col -> possibilitiesMap[possibilitiesMap.rows - 1, col] }
    println("Part 2: $totalPossibilities")
}