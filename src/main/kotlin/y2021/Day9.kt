package y2021

import util.FileType
import util.getFile
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream
import kotlin.system.measureTimeMillis

private data class Pos(val row: Int, val col: Int) {
    override fun toString() = "($row,$col)"
}
private operator fun List<List<Int>>.get(pos: Pos) = this[pos.row][pos.col]
private val List<List<Int>>.rows: Int 
    get() = this.size
private val List<List<Int>>.cols: Int
    get() = this[0].size

fun main() {
    val heightMap = getFile(object{}, FileType.INPUT).readLines()
        .map { row -> row.map { char -> char.digitToInt() } }

    println("${measureTimeMillis { partOne(heightMap) }} ms")
    println("${measureTimeMillis { partTwo(heightMap) }} ms")
}

private fun partOne(heightMap: List<List<Int>>) {
    val lowPoints = findLowPoints(heightMap)
    println("Part 1: sum of all (low point + 1): ${lowPoints.sumOf { heightMap[it] + 1 }}")
}

private fun partTwo(heightMap: List<List<Int>>) {
    val lowPoints = findLowPoints(heightMap)
    val visitedPoints = BitSet(heightMap.rows * heightMap.cols)
    val basinsToSize = lowPoints.associateWith { findBasinSize(heightMap, visitedPoints, it) }
    val largestBasinsToSize = basinsToSize.toList().sortedByDescending { it.second }.take(3)
    println("Part 2: largest basin sizes multiplied: ${largestBasinsToSize.fold(1) { acc, x -> acc * x.second}}")
}

private fun findLowPoints(heightMap: List<List<Int>>): List<Pos> {
    val (height, width) = heightMap.rows to heightMap.cols
    return IntStream.range(0, height)
        .parallel()
        .boxed()
        .flatMap { row ->
            IntStream.range(0, width)
                .parallel()
                .filter { col -> isLowPoint(heightMap, row, col) }
                .mapToObj { col -> Pos(row, col) }
        }.collect(Collectors.toList())
}

private fun isLowPoint(heightMap: List<List<Int>>, row: Int, col: Int): Boolean {
    return (heightMap[row][col] < getOrDefault(heightMap, row, col - 1)
            && heightMap[row][col] < getOrDefault(heightMap, row, col + 1)
            && heightMap[row][col] < getOrDefault(heightMap, row - 1, col)
            && heightMap[row][col] < getOrDefault(heightMap, row + 1, col))
}

private fun getOrDefault(heightMap: List<List<Int>>, row: Int, col: Int): Int {
    return if (isOutOfBounds(heightMap, row, col)) 9 else heightMap[row][col]
}

private fun isOutOfBounds(heightMap: List<List<Int>>, row: Int, col: Int): Boolean {
    return row < 0 || heightMap.rows <= row || col < 0 || heightMap.cols <= col
}

private fun findBasinSize(heightMap: List<List<Int>>, visitedPoints: BitSet, lowPoint: Pos): Int {
    val basinEdge = LinkedList(listOf(lowPoint))
    val basin = mutableMapOf(lowPoint to heightMap[lowPoint])

    while (basinEdge.isNotEmpty()) {
        val (row, col) = basinEdge.pollFirst()
        appendIfInBasin(heightMap, visitedPoints, basinEdge, basin, row, col - 1)
        appendIfInBasin(heightMap, visitedPoints, basinEdge, basin, row, col + 1)
        appendIfInBasin(heightMap, visitedPoints, basinEdge, basin, row - 1, col)
        appendIfInBasin(heightMap, visitedPoints, basinEdge, basin, row + 1, col)
    }
    return basin.size
}

private fun appendIfInBasin(
    heightMap: List<List<Int>>, visitedPoints: BitSet, basinEdge: LinkedList<Pos>, basin: MutableMap<Pos, Int>, row: Int, col: Int
) {
    if (getOrDefault(heightMap, row, col) != 9 && !visitedPoints[row * heightMap.cols + col]) {
        val pos = Pos(row, col)
        basin[pos] = heightMap[pos]
        basinEdge += pos
        visitedPoints[row * heightMap.cols + col] = true
    }
}