package y2021

import util.*
import java.util.*
import kotlin.math.abs
import kotlin.system.measureTimeMillis

class Day15

fun main() {
    val riskMap = Map2D.fromListOfLists(
        getFile(Day15::class, FileType.INPUT).readLines().map { line -> line.map { char -> char.digitToInt() } })

    println("Took ${measureTimeMillis { partOne(riskMap) }} ms")
    println("Took ${measureTimeMillis { partTwo(riskMap) }} ms")
}

private fun partOne(riskMap: Map2D) {
    val finalCost = findAStarCost(riskMap)
    println("Part 1: cost of start -> finish = $finalCost")
}

private fun partTwo(inputMap: Map2D) {
    val enlargedRiskMap = Map2D.withValues(inputMap.rows * 5, inputMap.cols * 5) { row, col ->
        (inputMap[row % inputMap.rows, col % inputMap.cols] + row / inputMap.rows + col / inputMap.cols - 1) % 9 + 1
    }

    val finalCost = findAStarCost(enlargedRiskMap)
    println("Part 2: cost of start -> finish = $finalCost")
}

private fun findAStarCost(riskMap: Map2D): Int {
    // heuristic = manhattan distance
    val heuristic: (Coord2D, Coord2D) -> Int = { a: Coord2D, b: Coord2D -> abs(a.row - b.row) + abs(a.col - b.col) }

    val start = 0 by 0
    val finish = riskMap.rows - 1 by riskMap.cols - 1

    val frontier = PriorityQueue<Pair<Coord2D, Int>>(Comparator.comparing { it.second })
    frontier += start to 0

    val costSoFar = Map2D.withValues(riskMap.rows, riskMap.cols) { _, _ -> Int.MAX_VALUE }.apply { this[0, 0] = 0 }

    while (frontier.isNotEmpty()) {
        val (current, _) = frontier.remove()

        if (current == finish) {
            break
        }

        riskMap.onEachNeighbor(current, includeDiagonals = false) { next ->
            val newCost = costSoFar[current] + riskMap[next]
            if (newCost < costSoFar[next]) {
                costSoFar[next] = newCost
                val priority = newCost + heuristic(next, finish)
                frontier += next to priority
                // parent node would normally be registered here (next -> current), but it introduces huge delays
            }
        }
    }
    return costSoFar[finish]
}
