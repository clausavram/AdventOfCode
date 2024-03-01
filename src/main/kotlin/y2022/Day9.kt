package y2022

import util.FileType
import util.Point2D
import util.getFile
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

private enum class Direction(val str: String, val vector: Point2D) {
    Left("L", Point2D(-1, 0)),
    Right("R", Point2D(1, 0)),
    Up("U", Point2D(0, 1)),
    Down("D", Point2D(0, -1));

    companion object {
        val strToDir = values().associateBy { it.str }
        fun fromStr(str: String): Direction = strToDir[str]!!
    }

    override fun toString() = str
}

private data class Movement(val direction: Direction, val distance: Int, val str: String) {
    val vector
        get() = direction.vector

    companion object {
        fun fromStr(str: String): Movement {
            val (dirStr, distStr) = str.split(" ")
            return Movement(Direction.fromStr(dirStr), distStr.toInt(), dirStr)
        }
    }
}

fun main() {
    val file = getFile(object {}, FileType.EXAMPLE)
    val moves = file.readLines().map { Movement.fromStr(it) }

    solve("Part 1", 2, moves)
//    solve("Part 2", 10, moves)
}

private fun solve(problemPart: String, ropeLength: Int, moves: List<Movement>) {
    // part 2
    val knots = Array(ropeLength) { Point2D(0, 0) }
    val endTailHistory = LinkedHashSet(setOf(Point2D(0, 0)))
    println("== Initial State ==")
    printRope(knots, -11..14, -5..15)
    for (headMove in moves) {

        for (i in 1..headMove.distance) {
            knots[0] += headMove.vector

            for (crtKnotIdx in 1 until knots.size) {
                val oldTail = knots[crtKnotIdx]
                val newHead = knots[crtKnotIdx - 1]
                val newTail = advanceTail(oldTail, newHead)

                if (oldTail == newTail) {
                    break // if this knot didn't move, so the ones after it won't as well
                }

                knots[crtKnotIdx] = newTail
            }
            endTailHistory += knots.last()
        }
        // start-row:90, start-col:11   total-rows:424, total-cols:195
        println("== ${headMove.direction} ${headMove.distance} ==")
        printRope(knots, -11..14, -5..15)
//        printRope(knots, -89..14, -5..15)
    }

    printTailTrace(endTailHistory)
    println("${problemPart}: 10-knot rope's tail visited ${endTailHistory.size} positions")
}

private fun advanceTail(oldTail: Point2D, newHead: Point2D): Point2D {
    val (xDist, yDist) = newHead - oldTail
    return when {
        abs(xDist) > 1 -> Point2D(newHead.x - xDist.sign, newHead.y) // move horizontally
        abs(yDist) > 1 -> Point2D(newHead.x, newHead.y - yDist.sign) // move vertically
        else -> oldTail // don't move tail
    }
}

private fun printRope(knots: Array<Point2D>, xRange: IntRange, yRange: IntProgression) {
    println()
    for (y in yRange.reversed()) {
        for (x in xRange) {
            val crtPoint = Point2D(x, y)
            if (crtPoint == knots[0]) print('H')
            else {
                var matchedAnyKnot = false
                for (crtKnotIdx in 1 until knots.size) {
                    if (crtPoint == knots[crtKnotIdx]) {
                        print(crtKnotIdx)
                        matchedAnyKnot = true
                        break
                    }
                }
                if (!matchedAnyKnot) {
                    if (x == 0 && y == 0) {
                        print('s')
                    } else {
                        print('.')
                    }
                }
            }
        }
        println()
    }
    println()
}

private fun printTailTrace(visitedPoints: LinkedHashSet<Point2D>) {
    println("== Tail trace:")
    var (xMin, xMax, yMin, yMax) = listOf(0, 0, 0, 0)
    visitedPoints.forEach {
        xMin = min(xMin, it.x)
        xMax = max(xMax, it.x)
        yMin = min(yMin, it.y)
        yMax = max(yMax, it.y)
    }

    for (y in yMax downTo yMin) {
        for (x in xMin..xMax) {
            val crtPoint = Point2D(x, y)
            if (x == 0 && y == 0) print('s')
            else if (crtPoint in visitedPoints) print('X')
            else print('.')
        }
        println()
    }
    println()
}
