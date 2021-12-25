package y2021

import util.FileType
import util.getFile
import java.util.*

class Day6

const val DAYS_TO_SIMULATE_PART_1 = 80
const val DAYS_TO_SIMULATE_PART_2 = 256

fun main() {
    val initialFishSchool = getFile(Day6::class, FileType.INPUT).readText()
        .split(',')
        .map { it.toInt() }

    partOne(initialFishSchool)
    partTwo(initialFishSchool)
}

/** naive approach */
private fun partOne(initialFishSchool: List<Int>) {
    val currentFishSchool = initialFishSchool.toMutableList()
    for (day in 1..DAYS_TO_SIMULATE_PART_1) {
        for (crtFishIdx in 0 until currentFishSchool.size) {
            if (currentFishSchool[crtFishIdx] == 0) {
                currentFishSchool += 8
                currentFishSchool[crtFishIdx] = 7
            }
            currentFishSchool[crtFishIdx]--
        }
    }
    println("Part 1: Fish count on day $DAYS_TO_SIMULATE_PART_1: ${currentFishSchool.size}")
}

/** more efficient, using buckets for days */
private fun partTwo(initialProcreationPeriods: List<Int>) {
    val fishSchool = FishSchool(initialProcreationPeriods)
    for (crtDay in 1..DAYS_TO_SIMULATE_PART_2) {
        fishSchool.advanceDay()
    }
    println("Part 2: Fish count on day $DAYS_TO_SIMULATE_PART_2: ${fishSchool.totalFishCount}")
}

class FishSchool(initialProcreationPeriods: List<Int>) {
    /**
     * - value = how many fish there are in the group
     * - index = how many day there still are to them giving birth
     */
    private val fishByDaysToProcreate: LinkedList<Long> = LinkedList((0..8).map { 0L }.toList())
    val totalFishCount: Long
        get() = fishByDaysToProcreate.sum()

    init {
        initialProcreationPeriods.forEach {
            fishByDaysToProcreate[it]++
        }
    }

    fun advanceDay() {
        val fishProcreatingNow = fishByDaysToProcreate.removeFirst()
        fishByDaysToProcreate.addLast(0)
        // re-add the old fish to the school using their 7-day procreation period
        fishByDaysToProcreate[6] += fishProcreatingNow
        // add the new fish using their one-time 9-day procreation period
        fishByDaysToProcreate[8] += fishProcreatingNow
    }
}