package y2025

import util.FileType
import util.getFile


fun main() {
    val text = getFile(object {}, FileType.INPUT).readText()
    val (rangesLines, ingredientLines) = text.split("\n\n")

    val freshIngredientRanges = rangesLines.lines()
        .map { rangeLine -> rangeLine.split("-")}
        .map { rangeArray -> rangeArray[0].toLong() .. rangeArray[1].toLong() }
    val ingredients = ingredientLines.lines().map { it.toLong() }

    solvePart1(freshIngredientRanges, ingredients)
    solvePart2(freshIngredientRanges)
}

private fun solvePart1(freshIngredientRanges: List<LongRange>, ingredients: List<Long>) {
    val freshCount = ingredients.count { ingredient -> freshIngredientRanges.any { ingredient in it } }
    println("Part 1: $freshCount")
}

private fun solvePart2(freshIngredientRanges: List<LongRange>) {
    val rangeToOverlappingGroups: MutableMap<LongRange, Set<LongRange>> =
        freshIngredientRanges.associateWithTo(mutableMapOf()) { setOf(it) }

    for (firstRangeIdx in 0 until (freshIngredientRanges.size - 1)) {
        for (secondRangeIdx in (firstRangeIdx + 1) until freshIngredientRanges.size) {
            val firstRange = freshIngredientRanges[firstRangeIdx]
            val secondRange = freshIngredientRanges[secondRangeIdx]
            if (firstRange overlapsWith secondRange) {
                val firstRangeOverlappingGroup = rangeToOverlappingGroups[firstRange]!!
                val secondRangeOverlappingGroup = rangeToOverlappingGroups[secondRange]!!
                val newOverlappingGroup = firstRangeOverlappingGroup union secondRangeOverlappingGroup
                newOverlappingGroup.forEach { ingredient -> rangeToOverlappingGroups[ingredient] = newOverlappingGroup }
            }
        }
    }

    val overlappingGroups = rangeToOverlappingGroups.values.distinct()
    val freshIngredientsCount = overlappingGroups.sumOf { overlappingGroup ->
        val compactedRange = overlappingGroup.minOf { it.first }..overlappingGroup.maxOf { it.last }
        val rangeSize = compactedRange.last - compactedRange.first + 1
        println("compacted range: $compactedRange, size: $rangeSize, source ranges: $overlappingGroup")
        rangeSize
    }

    println("Part 2: $freshIngredientsCount")
}

private infix fun LongRange.overlapsWith(that: LongRange): Boolean {
    return this.first in that || this.last in that || that.first in this || that.last in this
}

