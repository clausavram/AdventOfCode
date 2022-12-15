package util

data class Point2D(val x: Int, val y: Int) {
    operator fun plus(that: Point2D) = Point2D(this.x + that.x, this.y + that.y)
    operator fun minus(that: Point2D) = Point2D(this.x - that.x, this.y - that.y)
    override fun toString(): String = "($x,$y)"
}