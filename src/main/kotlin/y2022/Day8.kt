package y2022

import util.*

private class Tree private constructor(val height: Int, var visible: Boolean, var visibility: Int) {
    constructor(height: Int, visible: Boolean) : this(height, visible, 1)

    override fun toString(): String = height.toString()
    fun toFullString() = "Tree($height, $visible, $visibility)"
}

fun main() {
    val forestMap: Map2D<Tree> = Map2D.fromListOfLists(
        getFile(object {}, FileType.INPUT).readLines()
            .map { line -> line.map { char -> Tree(char.digitToInt(), false) } })

    markVisibleTrees(forestMap)
    val visibleTreesCount = countVisibleTrees(forestMap)
    println("Part 1: count of visible trees: $visibleTreesCount\n")

    computeForestVisibility(forestMap)
    val (bestTree, bestPosition) = findHighestVisibilityTree(forestMap)
    println("Part 2: best visibility tree: ${bestTree.toFullString()} at position $bestPosition")
}

private fun markVisibleTrees(forestMap: Map2D<Tree>) {
    // mark borders as visible
    for (col in 0 until forestMap.cols) {
        forestMap[0, col].visible = true
        forestMap[forestMap.rows - 1, col].visible = true
    }
    for (row in 0 until forestMap.rows) {
        forestMap[row, 0].visible = true
        forestMap[row, forestMap.cols - 1].visible = true
    }

    // scan horizontally
    for (row in 1 until forestMap.rows - 1) {
        // left -> right
        var leftMaxHeight = forestMap[row, 0].height
        for (col in 1 until forestMap.cols) {
            if (forestMap[row, col].height > leftMaxHeight) {
                forestMap[row, col].visible = true
                leftMaxHeight = forestMap[row, col].height
            }
        }
        // right -> left
        var rightMaxHeight = forestMap[row, forestMap.cols - 1].height
        for (col in forestMap.cols - 2 downTo 1) {
            if (forestMap[row, col].height > rightMaxHeight) {
                forestMap[row, col].visible = true
                rightMaxHeight = forestMap[row, col].height
            }
        }
    }

    // scan vertically
    for (col in 1 until forestMap.rows - 1) {
        // top -> bottom
        var topMaxHeight = forestMap[0, col].height
        for (row in 1 until forestMap.cols) {
            if (forestMap[row, col].height > topMaxHeight) {
                forestMap[row, col].visible = true
                topMaxHeight = forestMap[row, col].height
            }
        }
        // bottom -> top
        var bottomMaxHeight = forestMap[forestMap.rows - 1, col].height
        for (row in forestMap.cols - 2 downTo 1) {
            if (forestMap[row, col].height > bottomMaxHeight) {
                forestMap[row, col].visible = true
                bottomMaxHeight = forestMap[row, col].height
            }
        }
    }

    // print the forest with hidden trees
//    println("All trees:")
//    println(forestMap)
//    println("Hidden trees:")
//    println(forestMap.toString { tree -> if (!tree.visible) tree.height.toString() else "-" })
}

private fun countVisibleTrees(forestMap: Map2D<Tree>): Int {
    var result = 0
    forestMap.forEach { _, _, tree -> if (tree.visible) result++ }
    return result
}

private fun computeForestVisibility(forestMap: Map2D<Tree>) {
    // mark borders with zeroes
    for (col in 0 until forestMap.cols) {
        forestMap[0, col].visibility = 0
        forestMap[forestMap.rows - 1, col].visibility = 0
    }
    for (row in 0 until forestMap.rows) {
        forestMap[row, 0].visibility = 0
        forestMap[row, forestMap.cols - 1].visibility = 0
    }

    // for each inner tree, compute visibility
    for (row in 1 until forestMap.rows - 1) {
        for (col in 1 until forestMap.cols - 1) {
            forestMap[row, col].visibility = computeTreeVisibility(forestMap, row, col)
        }
    }
}

private fun computeTreeVisibility(forestMap: Map2D<Tree>, targetTreeRow: Int, targetTreeCol: Int): Int {
    // 0 visibility is only on the edge, so stop there
    val filterLastVisibleTreeDistance: (Coord2D) -> Boolean =
        { forestMap[it].visibility == 0 || forestMap[it].height >= forestMap[targetTreeRow, targetTreeCol].height }
    val leftCol = (targetTreeCol - 1 downTo 0).first { filterLastVisibleTreeDistance(targetTreeRow by it) }
    val rightCol = (targetTreeCol + 1 until forestMap.cols).first { filterLastVisibleTreeDistance(targetTreeRow by it) }
    val topRow = (targetTreeRow - 1 downTo 0).first { filterLastVisibleTreeDistance(it by targetTreeCol) }
    val bottomRow = (targetTreeRow + 1 until forestMap.rows).first { filterLastVisibleTreeDistance(it by targetTreeCol) }
    val leftVisibility = targetTreeCol - leftCol
    val rightVisibility = rightCol - targetTreeCol
    val topVisibility = targetTreeRow - topRow
    val bottomVisibility = bottomRow - targetTreeRow
    return leftVisibility * rightVisibility * topVisibility * bottomVisibility
}

private fun findHighestVisibilityTree(forestMap: Map2D<Tree>): Pair<Tree, Coord2D> {
    var bestTree = forestMap[0, 0]
    var bestPosition = 0 by 0
    forestMap.forEach { row, col, crtTree ->
        if (crtTree.visibility > bestTree.visibility) {
            bestTree = crtTree
            bestPosition = row by col
        }
    }
    return bestTree to bestPosition
}