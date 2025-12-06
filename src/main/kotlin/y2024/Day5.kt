package y2024

import util.FileType
import util.getFile

fun main() {
    val (orderings, pagesStr) = getFile(object {}, FileType.INPUT).readText().split("\n\n")

    val beforeToAfters = orderings.split("\n")
        .map { it.split('|') }
        .groupBy({ it.first().toInt() }, { it.last().toInt() })
    val pages = pagesStr.split('\n').map { it.split(',').map { pageNr -> pageNr.toInt() } }

    val (orderedPages, unorderedPages) = pages.partition { isInCorrectOrder(it, beforeToAfters) }

    partOne(orderedPages)
    partTwo(unorderedPages, beforeToAfters)
}

private fun partOne(orderedPages: List<List<Int>>) {
    val sum = orderedPages.sumOf { page -> page[page.size / 2] }
    println("Part 1: $sum")
}

private fun isInCorrectOrder(page: List<Int>, beforeToAfters: Map<Int, List<Int>>): Boolean {
    for (leftIdx in 0 until page.size - 1) {
        for (rightIdx in leftIdx + 1 until page.size) {
            if (beforeToAfters.getOrElse(page[rightIdx]) { emptySet() }.contains(page[leftIdx])) {
                println("found bad order: (${page[leftIdx]}, ${page[rightIdx]}) in $page")
                return false
            }
        }
    }
    println("page is good (middle: ${page[page.size / 2]}): $page")
    return true
}

private fun partTwo(unorderedPages: List<List<Int>>, beforeToAfters: Map<Int, List<Int>>) {
    val reorderedPages = unorderedPages.map {
        it.sortedWith { a, b ->
            if (beforeToAfters[a]?.contains(b) == true) return@sortedWith -1
            if (beforeToAfters[a]?.contains(b) == true) 1 else 0
        }
    }
    reorderedPages.forEach { page -> println("page is good (middle: ${page[page.size / 2]}): $page") }
    val sum = reorderedPages.sumOf { page -> page[page.size / 2] }
    println("Part 2: $sum")
}
