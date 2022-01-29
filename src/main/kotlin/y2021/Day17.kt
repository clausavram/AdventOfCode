package y2021

import util.FileType
import util.getFile
import kotlin.math.min

private class Day17

fun main() {
    val input = getFile(Day17::class, FileType.EXAMPLE).readText()
    val regex = Regex("""target area: x=(-?\d+)\.\.(-?\d+), y=(-?\d+)\.\.(-?\d+)""")
    val matchResult = regex.matchEntire(input)!!
    val xMin = matchResult.groups[1]!!.value.toInt()
    val xMax = matchResult.groups[2]!!.value.toInt()
    val yMin = matchResult.groups[3]!!.value.toInt()
    val yMax = matchResult.groups[4]!!.value.toInt()

    val maxXVelocity = findInitialMaxXVelocity(xMin, xMax)
    val minXVelocity = findInitialMinXVelocity(xMin, xMax)
    println("Max X velocity = $maxXVelocity")
    println("Min X velocity = $minXVelocity")
}

fun findInitialMaxXVelocity(xMin: Int, xMax: Int): Int {
    println("Finding max X velocity")
    var currentX = xMax
    var currentXVelocity = 0
//    println("x: $currentX, xVelocity: $currentXVelocity")

    val xPositions = mutableSetOf(currentX)
    while (currentX > currentXVelocity) { // we don't want X to < 0
        currentXVelocity++
        currentX -= currentXVelocity
//        println("x: $currentX, xVelocity: $currentXVelocity")
        xPositions += currentX
    }

    for (x in 0..xMax) {
        if (x == 0 && 0 in xPositions) print('s')
        else if (x == 0) print('S')
        else if (x >= xMin && x in xPositions) print('t')
        else if (x in xPositions) print('#')
        else if (x >= xMin) print('T')
        else print('.')
    }
    println('\n')

    return currentXVelocity
}

fun findInitialMinXVelocity(xMin: Int, xMax: Int): Int {
    println("Finding min X velocity")
    var currentX = xMin
    var currentXVelocity = 0
//    println("x: $currentX, xVelocity: $currentXVelocity")

    val xPositions = mutableSetOf(currentX)
    while (currentX > 0) { // we don't want X to < 0
        currentXVelocity++
        currentX -= currentXVelocity
//        println("x: $currentX, xVelocity: $currentXVelocity")
        xPositions += currentX
    }

    for (x in min(currentX, 0)..xMax) {
        if (x == 0 && 0 in xPositions) print('s')
        else if (x == 0) print('S')
        else if (x >= xMin && x in xPositions) print('t')
        else if (x in xPositions) print('#')
        else if (x >= xMin) print('T')
        else print('.')
    }
    println('\n')

    return currentXVelocity
}
