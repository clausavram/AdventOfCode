package y2021

import util.FileType
import util.Point2D
import util.getFile

private enum class Axis { X, Y }
private data class Fold(val axis: Axis, val coordinate: Int)
private data class DotsAndFolds(val dots: Set<Point2D>, val folds: List<Fold>)

private fun Point2D.fold(fold: Fold): Point2D {
    return if (fold.axis == Axis.X) {
        if (this.x < fold.coordinate) this else Point2D(2 * fold.coordinate - this.x, this.y)
    } else {
        if (this.y < fold.coordinate) this else Point2D(this.x, 2 * fold.coordinate - this.y)
    }
}

fun main() {
    val dotsAndFolds = parseInput(getFile(object{}, FileType.INPUT).readLines())
    partOne(dotsAndFolds)
    partTwo(dotsAndFolds)
}

private fun parseInput(dotsAndFolds: List<String>): DotsAndFolds {
    val foldRegex = Regex("""fold along ([xy])=(\d+)""")
    val dots = LinkedHashSet<Point2D>()
    val folds = mutableListOf<Fold>()
    var readingDots = true
    for (dotOrFold in dotsAndFolds) {
        if (dotOrFold.isEmpty()) {
            readingDots = false
            continue
        }
        if (readingDots) {
            val (x, y) = dotOrFold.split(',')
            dots += Point2D(x.toInt(), y.toInt())
        } else {
            val match = foldRegex.matchEntire(dotOrFold)!!
            folds += Fold(Axis.valueOf(match.groups[1]!!.value.uppercase()), match.groups[2]!!.value.toInt())
        }
    }
    return DotsAndFolds(dots, folds)
}

private fun partOne(dotsAndFolds: DotsAndFolds) {
    val foldedDots = foldDots(dotsAndFolds.dots, dotsAndFolds.folds.first())
    println("Part 1: after 1st fold, ${foldedDots.size} dots remain")
}

private fun partTwo(dotsAndFolds: DotsAndFolds) {
    var foldedDots = dotsAndFolds.dots
    for (fold in dotsAndFolds.folds) {
        foldedDots = foldDots(foldedDots, fold)
    }
    println("Part 2: result of doing all folds:")
    printDots(foldedDots)
}

private fun foldDots(dots: Set<Point2D>, fold: Fold) = dots.mapTo(LinkedHashSet()) { dot -> dot.fold(fold) }

private fun printDots(dots: Collection<Point2D>) {
    val maxX = dots.maxOf { it.x }
    val maxY = dots.maxOf { it.y }
    val xToY = dots.groupBy { it.y }.mapValues { entry -> entry.value.map { dot -> dot.x }.toSet() }
    for (y in 0..maxY) {
        for (x in 0..maxX) {
            print(if (x in xToY.getOrDefault(y, emptySet())) "#" else ".")
        }
        println()
    }
}
