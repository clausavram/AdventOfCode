package util

class Map2D<T>(private val underlyingArray: Array<T>, val rows: Int, val cols: Int) {
    companion object {
        inline fun <reified T> fromListOfLists(map: List<List<T>>): Map2D<T> {
            val (rows, cols) = map.size to map[0].size
            val array = Array(rows * cols) { destIdx -> map[destIdx / cols][destIdx % cols] }
            return Map2D(array, rows, cols)
        }

        inline fun <reified T> withValues(rows: Int, cols: Int, valueProvider: (Int, Int) -> T): Map2D<T> {
            return Map2D(Array(rows * cols) { index -> valueProvider(index / cols, index % cols) }, rows, cols)
        }
    }

    operator fun get(coord: Coord2D): T = get(coord.row, coord.col)
    operator fun get(row: Int, col: Int): T = underlyingArray[getFlatIndex(row, col)]

    operator fun set(coord: Coord2D, newValue: T): T = set(coord.row, coord.col, newValue)
    operator fun set(row: Int, col: Int, newValue: T): T {
        val oldValue = get(row, col)
        underlyingArray[getFlatIndex(row, col)] = newValue
        return oldValue
    }

    private fun getFlatIndex(row: Int, col: Int) = row * cols + col
    fun copy() = Map2D(underlyingArray.clone(), rows, cols)

    fun onEachNeighbor(coord: Coord2D, includeDiagonals: Boolean = true, action: Map2D<T>.(Coord2D) -> Unit) {
        onEachNeighbor(coord.row, coord.col, includeDiagonals) { neighborRow, neighborCol -> this.action(neighborRow by neighborCol) }
    }
    fun onEachNeighbor(row: Int, col: Int, includeDiagonals: Boolean = true, action: Map2D<T>.(Int, Int) -> Unit) {
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

    fun forEach(action: (row: Int, col: Int, value: T) -> Unit) {
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                action(row, col, this[row, col])
            }
        }
    }

    override fun toString(): String = toString { it.toString() }

    fun toString(formatter: (T) -> String): String {
        val result = StringBuilder()
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                result.append(formatter(this[row, col]))
            }
            result.append('\n')
        }
        return result.toString()
    }
}

//@JvmInline
data class Coord2D(private val rowAndCol: Long) {
    val row: Int
        get() = rowAndCol.shr(Int.SIZE_BITS).toInt()
    val col: Int
        get() = rowAndCol.toInt()

    operator fun plus(that: Coord2D): Coord2D = this.row + that.row by this.col + that.col
    override fun toString(): String = "($row, $col)"
}

infix fun Int.by(col: Int) = Coord2D(this.toLong().shl(Int.SIZE_BITS).or(col.toLong().and((-1L).ushr(Int.SIZE_BITS))))
