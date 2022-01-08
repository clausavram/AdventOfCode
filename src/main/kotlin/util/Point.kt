package util

data class Point(val x: Int, val y: Int) {
    operator fun plus(that: Point) = Point(this.x + that.x, this.y + that.y)
    operator fun minus(that: Point) = Point(this.x - that.x, this.y - that.y)
    override fun toString(): String = "($x,$y)"
}