package y2025

import util.FileType
import util.Map2D
import util.getFile

private enum class Item(val str: String) {
    EMPTY("."),
    PAPER_ROLL("@"),
    ACCESSIBLE_PAPER_ROLL("x"),
    ;
    override fun toString(): String = str
    companion object {
        fun fromString(str: String): Item = entries.find { it.str == str }!!
    }
}

fun main() {
    val map: Map2D<Item> = getFile(object {}, FileType.INPUT).readLines()
        .map { it.toList().map { c -> Item.fromString(c.toString()) } }
        .let { Map2D.fromListOfLists(it) }

    solvePart1(map)
    solvePart2(map)
}

private fun solvePart1(inputMap: Map2D<Item>) {
    var accessibleRollsCount = 0
    inputMap.forEach { crtRow, crtCol, item ->
        if (item != Item.PAPER_ROLL) {
            return@forEach
        }
        var paperRollNeighborsCount = 0
        inputMap.onEachNeighbor(crtRow, crtCol) { neighborRow, neighborCol ->
            if (inputMap[neighborRow, neighborCol] == Item.PAPER_ROLL) {
                paperRollNeighborsCount++
            }
        }
        if (paperRollNeighborsCount < 4) {
            accessibleRollsCount++
        }
    }
    println("Part 1: $accessibleRollsCount")
}

private fun solvePart2(inputMap: Map2D<Item>) {
    val accessibleRollsMap = inputMap.copy()

    var prevAccessibleRollsCount = 0
    var crtAccessibleRollsCount = 0
    do {
        prevAccessibleRollsCount = crtAccessibleRollsCount

        accessibleRollsMap.forEach { crtRow, crtCol, item ->
            if (item != Item.PAPER_ROLL) {
                return@forEach
            }
            var paperRollNeighborsCount = 0
            accessibleRollsMap.onEachNeighbor(crtRow, crtCol) { neighborRow, neighborCol ->
                // in-place optimization: disconsider just-converted rolls as well
                if (accessibleRollsMap[neighborRow, neighborCol] == Item.PAPER_ROLL) {
                    paperRollNeighborsCount++
                }
            }
            if (paperRollNeighborsCount < 4) {
                accessibleRollsMap[crtRow, crtCol] = Item.ACCESSIBLE_PAPER_ROLL
            }
        }

        crtAccessibleRollsCount = accessibleRollsMap.countIf { it == Item.ACCESSIBLE_PAPER_ROLL }
    } while (crtAccessibleRollsCount != prevAccessibleRollsCount)

    println("Part 2: $crtAccessibleRollsCount")
}

private fun <T> Map2D<T>.countIf(checker: (T) -> Boolean): Int {
    var count = 0
    this.forEach { _, _, value -> if (checker(value)) count++ }
    return count
}
