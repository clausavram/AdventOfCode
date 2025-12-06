package y2025

import util.FileType
import util.getFile
import kotlin.math.abs
import kotlin.math.sign

private enum class Direction { LEFT, RIGHT }
private data class Rotation(val direction: Direction, val distance: Int) {
    public constructor(str: String) : this(
        when (str[0]) {
            'L' -> Direction.LEFT
            'R' -> Direction.RIGHT
            else -> throw IllegalArgumentException("Invalid direction: '${str[0]}'")
        }, str.substring(1).toInt()
    )

    fun getDeltaClicks() = when (direction) {
        Direction.LEFT -> -distance
        Direction.RIGHT -> distance
    }

    override fun toString() = (if (direction == Direction.LEFT) "L" else "R") + distance
}

fun main() {
    val rotations = getFile(object {}, FileType.INPUT).readLines().map { Rotation(it) }

    solvePart1(rotations)
    solvePart2(rotations)
}

private fun solvePart1(rotations: List<Rotation>) {
    var zeroOverlaps = 0
    var crtPos = 50

    for (rotation in rotations) {
        crtPos = (crtPos + rotation.getDeltaClicks() + 100) % 100
        if (crtPos == 0) zeroOverlaps++
    }

    println("Part 1: $zeroOverlaps")
}

private fun solvePart2(rotations: List<Rotation>) {
    var zeroOverlaps = 0
    var crtPos = 50
    var onZero = false

    for (rotation in rotations) {
        val oldPos = crtPos
        val fullRevolutions = abs(rotation.getDeltaClicks() / 100)
        val remainderRotation = rotation.getDeltaClicks() % 100
        val finalCrossedZero = !onZero && (crtPos + remainderRotation !in 0..100)
        crtPos = (crtPos + remainderRotation + 100) % 100
        onZero = crtPos == 0

        val extraZeroOverlaps = (if (onZero) 1 else 0) + (if (finalCrossedZero) 1 else 0) + fullRevolutions

        println(
            "$rotation: $oldPos -> $crtPos  \t- extraZeroOverlaps: $extraZeroOverlaps: onZero=$onZero, " +
                "finalCrossedZero=$finalCrossedZero, fullRevolutions=$fullRevolutions")

        zeroOverlaps += extraZeroOverlaps
    }

    println("Part 2: $zeroOverlaps")
}
