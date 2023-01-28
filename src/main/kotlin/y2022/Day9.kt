package y2022

import util.FileType
import util.Point2D
import util.getFile
import kotlin.math.max
import kotlin.math.min

private enum class Direction(val str: String) {
    Left("L"),
    Right("R"),
    Up("U"),
    Down("D");

    companion object {
        val strToDir = values().associateBy { it.str }
        fun fromStr(str: String): Direction = strToDir[str]!!
    }

    override fun toString() = str
}

private data class Movement(val direction: Direction, val distance: Int) {
    companion object {
        fun fromStr(str: String): Movement {
            val (dirStr, distStr) = str.split(" ")
            return Movement(Direction.fromStr(dirStr), distStr.toInt())
        }
    }
}

fun main() {
    val file = getFile(object {}, FileType.EXAMPLE)
    val moves = file.readLines().map { Movement.fromStr(it) }

    var head = Point2D(0, 0)
    var tail = head
    val tailHistory = LinkedHashSet<Point2D>()
    tailHistory += tail

    for (move in moves) {
        for (i in 1..move.distance) {
            when (move.direction) {
                Direction.Left -> {
                    head += Point2D(-1, 0)
                    if (tail.x - head.x > 1) {
                        tail = Point2D(head.x + 1, head.y)
                    }
                }

                Direction.Right -> {
                    head += Point2D(1, 0)
                    if (head.x - tail.x > 1) {
                        tail = Point2D(head.x - 1, head.y)
                    }
                }

                Direction.Up -> {
                    head += Point2D(0, 1)
                    if (head.y - tail.y > 1) {
                        tail = Point2D(head.x, head.y - 1)
                    }
                }

                Direction.Down -> {
                    head += Point2D(0, -1)
                    if (tail.y - head.y > 1) {
                        tail = Point2D(head.x, head.y + 1)
                    }
                }
            }
            tailHistory += tail
        }
    }
    printTailTrace(tailHistory, head, tail)
    println("Part 1: ${tailHistory.size}")
}

private fun printTailTrace(visitedPoints: LinkedHashSet<Point2D>, head: Point2D, tail: Point2D) {
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
            if (crtPoint == head) print('H')
            else if (crtPoint == tail) print('T')
            else if (x == 0 && y == 0) print('s')
            else if (crtPoint in visitedPoints) print('X')
            else print('.')
        }
        println()
    }
    println()
}
