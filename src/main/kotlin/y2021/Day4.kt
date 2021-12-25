package y2021

import util.FileType
import util.getFile
import java.util.*

class Day4

const val BOARD_SIZE = 5
val SEPARATOR_PATTERN = Regex(" +")

class Board(private val grid: Array<IntArray>) {
    private val unmarkedCountPerRow = IntArray(BOARD_SIZE) { BOARD_SIZE }
    private val unmarkedCountColumn = IntArray(BOARD_SIZE) { BOARD_SIZE }
    private val unmarkedNumbers = grid.flatMap { it.asSequence() }.toMutableSet()
    val unmarkedSum: Int
        get() = unmarkedNumbers.sum()

    fun copy() = Board(grid)
    
    override fun toString(): String {
        return grid.joinToString("\n", postfix = "\n") { row -> row.joinToString(" ") }
    }

    /** returns `true` if the board has a complete row/column */
    fun addDrawnNumber(drawnNumber: Int): Boolean {
        if (unmarkedNumbers.remove(drawnNumber)) {
            outerLoop@for (row in 0 until BOARD_SIZE) {
                for (col in 0 until BOARD_SIZE) {
                    if (grid[row][col] == drawnNumber) {
                        unmarkedCountPerRow[row]--
                        unmarkedCountColumn[col]--
                        if (unmarkedCountPerRow[row] == 0 || unmarkedCountColumn[col] == 0) {
                            return true
                        }
                        break@outerLoop
                    }
                }
            }
        }
        return false
    }

    companion object {
        fun parseGrid(lines: LinkedList<String>): Array<IntArray> {
            return Array(BOARD_SIZE) {
                lines.removeFirst()
                    .trim()
                    .split(SEPARATOR_PATTERN, BOARD_SIZE)
                    .map { it.toInt() }
                    .toIntArray()
            }
        }
    }
}

fun main() {
    val lines = LinkedList(getFile(Day4::class, FileType.INPUT).readLines())
    val drawnNumbers = lines.removeFirst()
        .split(",")
        .map { it.toInt() }
    val boards = mutableListOf<Board>()
    while (lines.isNotEmpty()) {
        lines.removeFirst() // empty line
        boards += Board(Board.parseGrid(lines))
    }

    // due to Board's mutable internal state, parts must be run separately
    partOne(drawnNumbers, boards)
    partTwo(drawnNumbers, boards.map { it.copy() })
}

private fun partOne(drawnNumbers: List<Int>, boards: List<Board>) {
    println("Part 1:")
    for (drawnNumber in drawnNumbers) {
        for ((index, board) in boards.withIndex()) {
            if (board.addDrawnNumber(drawnNumber)) {
                println(
                    "Board #$index finishes first with drawn number $drawnNumber, unmarked sum: ${board.unmarkedSum} and score: " +
                            "${board.unmarkedSum * drawnNumber}"
                )
                return
            }
        }
    }
}

private fun partTwo(drawnNumbers: List<Int>, allBoards: List<Board>) {
    println("Part 2:")
    val remainingBoards = allBoards.withIndex().toMutableList()

    for (drawnNumber in drawnNumbers) {
        val remainingBoardsIterator = remainingBoards.iterator()
        while (remainingBoardsIterator.hasNext()) {
            val (boardIdx, board) = remainingBoardsIterator.next()

            if (board.addDrawnNumber(drawnNumber)) {
                remainingBoardsIterator.remove()
            }
            if (remainingBoards.isEmpty()) {
                println("Board #$boardIdx finishes last with drawn number: $drawnNumber, sum: ${board.unmarkedSum}, score: ${board.unmarkedSum * drawnNumber}")
            }
        }
    }
}