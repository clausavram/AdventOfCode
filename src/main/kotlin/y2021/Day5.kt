package y2021

import util.FileType
import util.getFile
import kotlin.math.sign

class Day5

data class Point(val x: Int, val y: Int) {
    operator fun plus(that: Point) = Point(this.x + that.x, this.y + that.y)
    operator fun minus(that: Point) = Point(this.x - that.x, this.y - that.y)
    override fun toString(): String = "($x,$y)"
}

data class Segment(val start: Point, val end: Point) {
    constructor(start: Pair<Int, Int>, end: Pair<Int, Int>) : this(Point(start.first, start.second), Point(end.first, end.second))

    val vector = end - start
    override fun toString(): String = "$start -> $end"
}

fun main() {
    val lines = getFile(Day5::class, FileType.INPUT).readLines()
    val regex = Regex("""(\d+),(\d+) -> (\d+),(\d+)""")
    val segments = lines.map {
        val (x1, y1, x2, y2) = regex.find(it)!!.destructured
        Segment(x1.toInt() to y1.toInt(), x2.toInt() to y2.toInt())
    }

    println("Part 1:")
    countOverlappingPoints(segments.filter { it.start.x == it.end.x || it.start.y == it.end.y })
    println("Part 2:")
    countOverlappingPoints(segments)
}

private fun countOverlappingPoints(segments: List<Segment>) {
    val diagram = mutableMapOf<Point, Int>()
    for (segment in segments) {
        val step = Point(segment.vector.x.sign, segment.vector.y.sign)
        var previousPoint = segment.start
        diagram.compute(previousPoint) { _, old -> (old ?: 0) + 1 }
        while (previousPoint != segment.end) {
            previousPoint += step
            diagram.compute(previousPoint) { _, old -> (old ?: 0) + 1 }
        }
    }

    val overlappedPointCount = diagram.count { it.value >= 2 }
    println("# of overlapped points: $overlappedPointCount")
}