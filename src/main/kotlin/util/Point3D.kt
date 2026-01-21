package util

import kotlin.math.sqrt

data class Point3D(val x: Int, val y: Int, val z: Int) {
    operator fun plus(that: Point3D) = Point3D(this.x + that.x, this.y + that.y, this.z + that.z)
    operator fun minus(that: Point3D) = Point3D(this.x - that.x, this.y - that.y, this.z - that.z)
    override fun toString(): String = "($x,$y,$z)"
}

infix fun Point3D.distanceTo(that: Point3D): Double {
    val squaredDistance =
        (this.x - that.x) * (this.x - that.x) + (this.y - that.y) * (this.y - that.y) + (this.z - that.z) * (this.z - that.z)
    return sqrt(squaredDistance.toDouble())
}