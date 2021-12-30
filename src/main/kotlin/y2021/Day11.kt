package y2021

import util.FileType
import util.getFile
import java.util.*

class Day11

private class Map2D(private val underlyingArray: IntArray, val rows: Int, val cols: Int) {
    companion object {
        fun fromListOfLists(map: List<List<Int>>): Map2D {
            val (rows, cols) = map.size to map[0].size
            val array = IntArray(rows * cols)
            map.forEachIndexed { rowIdx, rowValues -> rowValues.toIntArray().copyInto(array, rowIdx * cols) }
            return Map2D(array, rows, cols)
        }
    }
    operator fun get(row: Int, col: Int): Int {
        return underlyingArray[getFlatIndex(row, col)]
    }
    operator fun set(row: Int, col: Int, newValue: Int): Int {
        val oldValue = get(row, col)
        underlyingArray[getFlatIndex(row, col)] = newValue
        return oldValue
    }
    fun getFlatIndex(row: Int, col: Int) = row * cols + col
    fun copy() = Map2D(underlyingArray.clone(), rows, cols)
    fun onEachNeighbor(row: Int, col: Int, action: Map2D.(Int, Int) -> Unit) {
        for (neighborRow in row - 1..row + 1) {
            for (neighborCol in col - 1..col + 1) {
                if ((neighborRow != row || neighborCol != col) && (neighborRow in 0 until rows) && (neighborCol in 0 until cols)) {
                    this.action(neighborRow, neighborCol)
                }
            }
        }
    }

    override fun toString(): String {
        val result = StringBuilder()
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                result.append(this[row, col])
            }
            result.append('\n')
        }
        return result.toString()
    }
}

fun main() {
    val energyMap = Map2D.fromListOfLists(getFile(Day11::class, FileType.INPUT).readLines().map { line -> line.map { char -> char.digitToInt() } })
    partOne(energyMap.copy())
    partTwo(energyMap.copy())
}

private fun partOne(energyMap: Map2D) {
    val daysCount = 100
    val totalFlashCount = (1..daysCount).sumOf {
        val flashes = advanceOneDay(energyMap)
//        println("On day $it, $flashes octopuses flashed => map:\n$energyMap")
        flashes
    }
    println("Part 1: total flash count after $daysCount: $totalFlashCount")
}

private fun partTwo(energyMap: Map2D) {
    var currentDay = 0
    while (true) {
        currentDay++
        val currentFlashCount = advanceOneDay(energyMap)
        if (currentFlashCount == energyMap.rows * energyMap.cols) {
            println("Part 2: all octopuses flashed at once (1st time) on day $currentDay")
            return
        }
    }
}

private fun advanceOneDay(energyMap: Map2D): Int {
    // increment each cell in the map => some octopuses may flash
    var todayFlashCount = 0
    val unhandledFlashedLocations = LinkedList<Pair<Int, Int>>()
    for (row in 0 until energyMap.rows) {
        for (col in 0 until energyMap.cols) {
            if (energyMap[row, col] == 9) {
                unhandledFlashedLocations += row to col
                todayFlashCount++
            }
            energyMap[row, col]++
        }
    }

    // cascading effect of already-flashed octopuses
    while (unhandledFlashedLocations.isNotEmpty()) {
        val (crtFlashRow, crtFlashCol) = unhandledFlashedLocations.pollFirst()
        energyMap.onEachNeighbor(crtFlashRow, crtFlashCol) { neighborRow, neighborCol ->
            if (this[neighborRow, neighborCol] == 9) {
                unhandledFlashedLocations += neighborRow to neighborCol
                todayFlashCount++
            }
            energyMap[neighborRow, neighborCol]++
        }
    }
    
    // truncate energy levels between 0..9
    for (row in 0 until energyMap.rows) {
        for (col in 0 until energyMap.cols) {
            if (energyMap[row, col] > 9) {
                energyMap[row, col] = 0
            }
        }
    }

    return todayFlashCount
}
