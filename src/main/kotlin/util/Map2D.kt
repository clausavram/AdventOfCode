package util

class Map2D private constructor(private val underlyingArray: IntArray, val rows: Int, val cols: Int) {
    companion object {
        fun fromListOfLists(map: List<List<Int>>): Map2D {
            val (rows, cols) = map.size to map[0].size
            val array = IntArray(rows * cols)
            map.forEachIndexed { rowIdx, rowValues -> rowValues.toIntArray().copyInto(array, rowIdx * cols) }
            return Map2D(array, rows, cols)
        }

        fun withValues(rows: Int, cols: Int, valueProvider: (Int, Int) -> Int): Map2D {
            return Map2D(IntArray(rows * cols) { index -> valueProvider(index / cols, index % cols) }, rows, cols)
        }
    }

    operator fun get(coord: Coord2D): Int = get(coord.row, coord.col)
    operator fun get(row: Int, col: Int): Int = underlyingArray[getFlatIndex(row, col)]

    operator fun set(coord: Coord2D, newValue: Int): Int = set(coord.row, coord.col, newValue)
    operator fun set(row: Int, col: Int, newValue: Int): Int {
        val oldValue = get(row, col)
        underlyingArray[getFlatIndex(row, col)] = newValue
        return oldValue
    }

    private fun getFlatIndex(row: Int, col: Int) = row * cols + col
    fun copy() = Map2D(underlyingArray.clone(), rows, cols)

    fun onEachNeighbor(coord: Coord2D, includeDiagonals: Boolean = true, action: Map2D.(Coord2D) -> Unit) {
        onEachNeighbor(coord.row, coord.col, includeDiagonals) { neighborRow, neighborCol -> this.action(neighborRow by neighborCol) }
    }
    fun onEachNeighbor(row: Int, col: Int, includeDiagonals: Boolean = true, action: Map2D.(Int, Int) -> Unit) {
        for (neighborRow in row - 1..row + 1) {
            for (neighborCol in col - 1..col + 1) {
                val isCrtLocation = neighborRow != row || neighborCol != col
                val rowInsideMap = neighborRow in 0 until rows
                val colInsideMap = neighborCol in 0 until cols
                val diagonalCheckPassed = includeDiagonals || neighborRow == row || neighborCol == col
                if (isCrtLocation && diagonalCheckPassed && rowInsideMap && colInsideMap) {
                    this.action(neighborRow, neighborCol)
                }
            }
        }
    }

    override fun toString(): String {
        val result = StringBuilder()
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                result.append(this[row, col])
            }
            result.append('\n')
        }
        return result.toString()
    }
}


@JvmInline
value class Coord2D(private val rowAndCol: Long) {
    val row: Int
        get() = rowAndCol.shr(Int.SIZE_BITS).toInt()
    val col: Int
        get() = rowAndCol.toInt()

    override fun toString(): String {
        return "($row, $col)"
    }
}
infix fun Int.by(col: Int) = Coord2D(this.toLong().shl(Int.SIZE_BITS).or(col.toLong()))
