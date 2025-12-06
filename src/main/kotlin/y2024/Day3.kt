package y2024

import util.FileType
import util.getFile

private val mulRegex = Regex("mul\\((\\d{1,3}),(\\d{1,3})\\)")

fun main() {
    val wholeMemory = getFile(object {}, FileType.INPUT).readText()

    println("Part 1: ${partOne(wholeMemory)}")
    println("Part 2: ${partTwo(wholeMemory)}")
}

private fun partOne(wholeMemory: String): Int {
    return executeMultiplies(wholeMemory) { true }
}

private fun partTwo(wholeMemory: String): Int {
    val dos = listOf(listOf(-1), indicesOfRegex(wholeMemory, Regex("do\\(\\)"))).flatten()
    val donts = listOf(listOf(-2), indicesOfRegex(wholeMemory, Regex("don't\\(\\)"))).flatten()

    return executeMultiplies(wholeMemory) { crtIdx -> isEnabled(crtIdx, dos, donts) }
}

private fun indicesOfRegex(searchedString: String, regex: Regex): List<Int> {
    return regex.findAll(searchedString)
        .map { it.range.first }
        .toList()
}

private fun executeMultiplies(wholeMemory: String, isEnabled: (Int) -> Boolean): Int {
    var mulTotal = 0
    var crtIndex = 0
    while (true) {
        val match = mulRegex.find(wholeMemory, crtIndex) ?: break
        if (isEnabled(match.range.first)) {
            val mulResult = match.groups[1]!!.value.toInt() * match.groups[2]!!.value.toInt()
            mulTotal += mulResult
        }

        crtIndex = match.range.last + 1
        if (crtIndex == wholeMemory.length) break
    }
    return mulTotal
}

private fun isEnabled(crtIdx: Int, dos: List<Int>, donts: List<Int>): Boolean {
    val lastDo = dos.last { it < crtIdx }
    val lastDont = donts.last { it < crtIdx }
    return lastDo > lastDont
}