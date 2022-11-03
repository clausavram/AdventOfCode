package y2021

import util.FileType
import util.getFile

private class Solution(val caveLinks: MutableList<String>, var smallCaveVisitedTwice: Boolean = false)

fun main() {
    val caveLinks = getFile(object{}, FileType.INPUT).readLines().map { val nodes = it.split('-', limit = 2); nodes[0] to nodes[1] }
    partOne(caveLinks)
    partTwo(caveLinks)
}

private fun partOne(caveLinks: List<Pair<String, String>>) {
    val caveToNeighbors = extractCaveToNeighbors(caveLinks)
    val solution = Solution(ArrayList())
    val allSolutions = mutableListOf<List<String>>()

    buildAllPaths(caveToNeighbors, "start", solution, allSolutions, false)
    println("Part 1: ${allSolutions.size} solutions")
}

private fun partTwo(caveLinks: List<Pair<String, String>>) {
    val caveToNeighbors = extractCaveToNeighbors(caveLinks)
    val solution = Solution(ArrayList())
    val allSolutions = mutableListOf<List<String>>()

    buildAllPaths(caveToNeighbors, "start", solution, allSolutions, true)
    println("Part 2: ${allSolutions.size} solutions")
}

private fun buildAllPaths(
    caveToNeighbors: Map<String, List<String>>,
    crtCave: String,
    solution: Solution,
    solutions: MutableList<List<String>>,
    allowOneSmallCaveAgain: Boolean,
) {
    if (crtCave == "end") {
//        println("Found solution ${solutions.size}: ${solution.caveLinks} + $crtCave")
        solutions += solution.caveLinks + crtCave
        return
    }

    if (crtCave[0].isLowerCase() && crtCave in solution.caveLinks) {
        if (allowOneSmallCaveAgain && crtCave != "start" && !solution.smallCaveVisitedTwice) {
            solution.smallCaveVisitedTwice = true
        } else {
            return
        }
    }

    solution.caveLinks += crtCave

    caveToNeighbors[crtCave]!!.forEach { neighbor -> buildAllPaths(caveToNeighbors, neighbor, solution, solutions, allowOneSmallCaveAgain) }

    solution.caveLinks -= crtCave
    if (allowOneSmallCaveAgain && crtCave[0].isLowerCase() && crtCave in solution.caveLinks) {
        solution.smallCaveVisitedTwice = false
    }
}

private fun extractCaveToNeighbors(caveLinks: List<Pair<String, String>>): Map<String, List<String>> {
    val caveToNeighbors = LinkedHashMap<String, MutableSet<String>>()

    caveLinks.forEach {
        caveToNeighbors.computeIfAbsent(it.first) { LinkedHashSet() }.add(it.second)
        caveToNeighbors.computeIfAbsent(it.second) { LinkedHashSet() }.add(it.first)
    }
    return caveToNeighbors.mapValues { it.value.toList() }
}
