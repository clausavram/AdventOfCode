package y2024

import util.*

private val directions: List<Coord2D> = listOf(-1 by 0, 0 by 1, 1 by 0, 0 by -1)

fun main() {
    val obstacleMap = getFile(object {}, FileType.EXAMPLE2).readLines()
    val mapSize = obstacleMap.size by obstacleMap[0].length
    val startPos = obstacleMap.indices.map { rowIdx -> rowIdx by obstacleMap[rowIdx].indexOf("^") }
        .first { it.col != -1 }
    val inputObstacles = obstacleMap.flatMapIndexed { rowIdx, rowStr ->
        rowStr.mapIndexed { colIdx, value -> if (value == '#') rowIdx by colIdx else -1 by -1 }.filter { it.row >= 0 }
    }.toSet()

    partOne(mapSize, inputObstacles, startPos)
    partTwo(mapSize, inputObstacles, startPos)
}

private fun partOne(mapSize: Coord2D, inputObstacles: Set<Coord2D>, startPos: Coord2D) {
    var crtPos = startPos
    var crtDir = -1 by 0
    val placesToVisitTime = mutableMapOf(crtPos to 0)

    while (true) {
        val (nextPos, nextDir) = next(mapSize, inputObstacles, crtPos, crtDir)
        if (isOutsideMap(nextPos, mapSize)) {
            println("Part 1: exit: $nextPos, visited places #: ${placesToVisitTime.size}")
            break
        }
        placesToVisitTime.putIfAbsent(nextPos, placesToVisitTime.size)
        crtPos = nextPos
        crtDir = nextDir
    }
}

private fun partTwo(mapSize: Coord2D, inputObstacles: Set<Coord2D>, startPos: Coord2D) {
    var crtPos = startPos
    var crtDir = -1 by 0
    val placesToVisitTime = mutableMapOf(crtPos to 0)
    val hypotheticalObstacles = inputObstacles.toMutableSet()
    val loopObstacles = mutableSetOf<Coord2D>()

    while (true) {
        val (nextPos, nextDir) = next(mapSize, inputObstacles, crtPos, crtDir)
        if (isOutsideMap(nextPos, mapSize)) {
            println("\nPart 2: exit: $nextPos, visited places#: ${placesToVisitTime.size}, loop obstacles#: ${loopObstacles.size}")
            break
        }
        println("\nstep ${placesToVisitTime.size}:$nextPos ") // TODO: stuck at 47
        if (true || placesToVisitTime.size == 47) {
            printMap(mapSize, inputObstacles, hypotheticalObstacles, startPos, crtPos, nextPos)
        }

        if (nextPos !in placesToVisitTime.keys) {
            val hypotheticalObstacle = nextPos
            hypotheticalObstacles += hypotheticalObstacle
            if (hasLoop(mapSize, inputObstacles, hypotheticalObstacles, crtPos, crtDir)) {
                println("Adding loop for $hypotheticalObstacle")
                loopObstacles += hypotheticalObstacle  // TODO: this doesn't get incremented
//                println("Found loop obstacle: $hypotheticalObstacle")
            }
            hypotheticalObstacles -= hypotheticalObstacle
        }

        placesToVisitTime.putIfAbsent(nextPos, placesToVisitTime.size)
        crtPos = nextPos
        crtDir = nextDir
    }
}

private fun hasLoop(mapSize: Coord2D, inputObstacles: Set<Coord2D>, hypotheticalObstacles: Set<Coord2D>, startPos: Coord2D, startDir: Coord2D): Boolean {
    var crtDir = startDir
    var crtPos = startPos
    val turnPointsWithNewDirs = mutableSetOf<Pair<Coord2D, Coord2D>>()

    while (true) {
        val (nextPos, nextDir, turned) = next(mapSize, hypotheticalObstacles, crtPos, crtDir)
        println("- hypothetical run:"); printMap(mapSize, inputObstacles, hypotheticalObstacles, startPos, crtPos, nextPos)
        if (isOutsideMap(nextPos, mapSize)) {
            return false
        }
        if (crtPos to nextDir in turnPointsWithNewDirs) {
            println("found loop separate from hypothetical obstacle $crtPos -> $nextDir")
            return true
        }
        if (turned) {
            turnPointsWithNewDirs += crtPos to nextDir
        }
        crtPos = nextPos
        crtDir = nextDir
    }
}

private fun next(mapSize: Coord2D, obstacles: Set<Coord2D>, crtPos: Coord2D, crtDir: Coord2D): Triple<Coord2D, Coord2D, Boolean> {
    var nextPos = crtPos + crtDir
    var nextDir = crtDir
    var turned = false

    if (isOutsideMap(nextPos, mapSize)) {
        return Triple(nextPos, nextDir, false)
    }
    while (nextPos in obstacles) {
        nextDir = directions[(directions.indexOf(nextDir) + 1).mod(directions.size)]
        nextPos = crtPos + nextDir
        if (isOutsideMap(nextPos, mapSize)) {
            return Triple(nextPos, nextDir, true)
        }
        turned = true
    }
    return Triple(nextPos, nextDir, turned)
}

private fun isOutsideMap(pos: Coord2D, mapSize: Coord2D) =
    pos.row !in 0 until mapSize.row || pos.col !in 0 until mapSize.col

private fun printMap(
    mapSize: Coord2D,
    inputObstacles: Set<Coord2D>,
    hypotheticalObstacles: Set<Coord2D>,
    startPos: Coord2D,
    crtPos: Coord2D,
    nextPos: Coord2D
) {
    for (row in 0 until mapSize.row) {
        for (col in 0 until mapSize.col) {
            print(getPosChar(row, col, inputObstacles, hypotheticalObstacles, startPos, crtPos, nextPos))
        }
        println()
    }
}

private fun getPosChar(
    row: Int,
    col: Int,
    inputObstacles: Set<Coord2D>,
    hypotheticalObstacles: Set<Coord2D>,
    startPos: Coord2D,
    crtPos: Coord2D,
    nextPos: Coord2D
): String {
    return when (val pos = row by col) {
        in inputObstacles -> "#"
        in hypotheticalObstacles -> "O"
        nextPos -> if (pos == startPos) "ṋ" else "n"
        crtPos -> if (pos == startPos) "ĉ" else "c"
        startPos -> "^"
        else -> "."
    }
}
