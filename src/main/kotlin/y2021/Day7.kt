package y2021

import util.FileType
import util.getFile
import java.util.stream.Collectors
import kotlin.math.abs
import kotlin.system.measureTimeMillis

fun main() {
    val submarinePositions = getFile(object{}, FileType.INPUT).readText()
        .split(',')
        .map { it.toInt() }

    println("Took: ${measureTimeMillis { partOne(submarinePositions) }} ms")
    println("Took: ${measureTimeMillis { partTwo(submarinePositions) }} ms")
}

private fun partOne(submarinePositions: List<Int>) {
    val (solutionPos, solutionCost) = computeOptimalDestination(submarinePositions) { start: Int, dest: Int -> abs(dest - start) }
    println("Part 1: optimal position: $solutionPos, with minimal cost: $solutionCost")
}

private fun partTwo(submarinePositions: List<Int>) {
    val (solutionPos, solutionCost) = computeOptimalDestination(submarinePositions) { start: Int, dest: Int -> abs(dest - start) * (abs(dest - start) + 1) / 2 }
    println("Part 2: optimal position: $solutionPos, with minimal cost: $solutionCost")
}

private fun computeOptimalDestination(submarinePositions: List<Int>, fuelCostFunction: (Int, Int) -> Int): Pair<Int, Int> {
    var (min, max) = submarinePositions[0] to submarinePositions[0]
    for (crtPos in submarinePositions) {
        if (crtPos > max) max = crtPos
        if (crtPos < min) min = crtPos
    }

    val solution = (min..max).toList()
        .parallelStream()
        .map { solutionCandidate ->
            solutionCandidate to submarinePositions.parallelStream().collect(Collectors.summingInt { fuelCostFunction(it, solutionCandidate) })
        }
//        .sequential()
//        .sorted(Comparator.comparing { it.first })
//        .peek { println("Pos ${it.first} has total fuel cost: ${it.second}") }
        .min(Comparator.comparing { it.second })
        .get()
    return solution
}

