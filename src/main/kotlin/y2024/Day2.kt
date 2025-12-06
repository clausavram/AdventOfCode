package y2024

import util.FileType
import util.getFile
import kotlin.math.absoluteValue
import kotlin.math.sign

fun main() {
    val lines = getFile(object {}, FileType.INPUT).readLines()
    val reports = lines.map { report -> report.split(Regex(" +")).map { level -> level.toInt() } }

    println("Part 1: ${partOne(reports)}")
    println("Part 2: ${partTwo(reports)}")
}

private fun partOne(reports: List<List<Int>>): Int {
    return reports.count { isSafe(it) }
}

private fun partTwo(reports: List<List<Int>>): Int {
    return reports.count { isSafeDampened(it) }
}

private fun isSafe(report: List<Int>): Boolean {
    val sequentialLevelDiff = report.windowed(2).map { it[1] - it[0] }
    if (sequentialLevelDiff.map { it.sign }.distinct().size != 1) {
        return false
    }
    return sequentialLevelDiff.all { it.absoluteValue <= 3 }
}

private fun isSafeDampened(report: List<Int>): Boolean {
    if (isSafe(report)) return true
    val monotonicity = (report[1] - report[0]).sign

    for (i in 0 until report.size - 1) {
        val crt = report[i]
        val next = report[i + 1]
        if ((next - crt).sign != monotonicity || (next - crt).absoluteValue !in 1..3) {
            return isSafeDampenedAround(report, i)
        }
    }
    throw IllegalStateException("unsafe level not identified! report: $report")
}

private fun isSafeDampenedAround(report: List<Int>, crtIdx: Int): Boolean {
    return crtIdx > 0 && isSafe(reportWithoutLevel(report, crtIdx - 1))
            || isSafe(reportWithoutLevel(report, crtIdx))
            || isSafe(reportWithoutLevel(report, crtIdx + 1))
}

private fun reportWithoutLevel(report: List<Int>, levelIndexToRemove: Int): List<Int> {
    return listOf(report.subList(0, levelIndexToRemove), report.subList(levelIndexToRemove + 1, report.size)).flatten()
}