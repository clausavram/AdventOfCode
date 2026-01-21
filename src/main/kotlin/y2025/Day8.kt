package y2025

import util.FileType
import util.Point3D
import util.distanceTo
import util.getFile
import java.util.LinkedList

fun main() {
    val points = getFile(object {}, FileType.EXAMPLE).readLines()
        .map { it.split(",") }
        .map { Point3D(it[0].toInt(), it[1].toInt(), it[2].toInt()) }

    solvePart1(points)
}

private fun solvePart1(points: List<Point3D>) {
    val allDistances = extractDistances(points)
    connectPoints(points, allDistances, 10)
}

private fun extractDistances(points: List<Point3D>): MutableList<PointDistance> {
    val allDistances = mutableListOf<PointDistance>()
    for (firstIdx in 0 until points.size - 1) {
        for (secondIdx in firstIdx + 1 until points.size) {
            val firstPoint = points[firstIdx]
            val secondPoint = points[secondIdx]
            val distance: Double = firstPoint distanceTo secondPoint
            allDistances += PointDistance(firstPoint, secondPoint, distance)
        }
    }
    allDistances.sortBy { it.distance }
//    println(allDistances.joinToString("\n"))
//    println()
    return allDistances
}

private fun connectPoints(
    points: List<Point3D>,
    allDistances: List<PointDistance>,
    totalConnectionCount: Int
) {
    println("Points:\n${points.mapIndexed { index, point -> "$index: $point" }.joinToString("\n") }")
    println()
    println("Connections:\n${allDistances.joinToString("\n") { "${points.indexOf(it.firstPoint)}-${points.indexOf(it.secondPoint)} = ${it.distance}" }}")
    println()

    val groupsByPoint: MutableMap<Point3D, Set<Point3D>> = points.associateWithTo(mutableMapOf()) { setOf(it) }
    val remainingDistances: LinkedList<PointDistance> = LinkedList(allDistances)

    var addedConnections = 1
    while (addedConnections <= totalConnectionCount) {
        val connection = remainingDistances.removeFirst()
        val (firstPoint, secondPoint, _) = connection
        if (firstPoint in groupsByPoint[secondPoint]!!) {
            println("Skipping [$connection], already in group: ${groupsByPoint[secondPoint]}")
            continue
        }

        val unionGroup = groupsByPoint[firstPoint]!! union groupsByPoint[secondPoint]!!
        println("Connecting groups: ${groupsByPoint[firstPoint]} + ${groupsByPoint[secondPoint]}")
        unionGroup.forEach { groupPoint -> groupsByPoint[groupPoint] = unionGroup }
        addedConnections++
    }
    val sortedGroups = groupsByPoint.values.distinct().sortedByDescending { it.size }
    println()
    println(sortedGroups.joinToString("\n") { "${it.size}: $it" })
}

private data class PointDistance(val firstPoint: Point3D, val secondPoint: Point3D, val distance: Double) {
    override fun toString(): String = "$firstPoint <-> $secondPoint = $distance"
}