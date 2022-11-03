package y2021

import util.FileType
import util.getFile
import java.util.regex.Pattern

fun main() {
    val lines = getFile(object{}, FileType.INPUT).readLines()
    partOne(lines)
    partTwo(lines)
}

private fun partOne(lines: List<String>) {
    val spacePattern = Pattern.compile(" ")
    var (horizontalPos: Int, verticalPos: Int) = listOf(0, 0)
    lines.forEach {
        val (command, unitsStr) = it.split(spacePattern, 2)
        val units = unitsStr.toInt()
        when (command) {
            "forward" -> horizontalPos += units
            "up" -> verticalPos -= units
            "down" -> verticalPos += units
            else -> error("Illegal command: '$it'")
        }
    }
    println("Part 1: Horizontal * vertical distance: ${horizontalPos * verticalPos}")
}

private fun partTwo(lines: List<String>) {
    val spacePattern = Pattern.compile(" ")
    var (horizontalPos: Int, verticalPos: Int, aim: Int) = listOf(0, 0, 0)
    lines.forEach {
        val (command, unitsStr) = it.split(spacePattern, 2)
        val units = unitsStr.toInt()
        when (command) {
            "forward" -> {
                horizontalPos += units
                verticalPos += units * aim
            }
            "up" -> aim -= units
            "down" -> aim += units
            else -> error("Illegal command: '$it'")
        }
    }
    println("Part 2: Horizontal * vertical distance: ${horizontalPos * verticalPos}")
}