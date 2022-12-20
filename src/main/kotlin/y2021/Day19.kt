package y2021

import util.FileType
import util.getFile


//@JvmInline
private class Beacon(val storage: Long) {
    constructor(
        x: Long, y: Long, z: Long
    ) : this(
        ((x + STORAGE_OFFSET) and COMPONENT_BIT_MASK shl (COMPONENT_STORAGE_BITS * 2))
                or ((y + STORAGE_OFFSET) and COMPONENT_BIT_MASK shl COMPONENT_STORAGE_BITS)
                or ((z + STORAGE_OFFSET) and COMPONENT_BIT_MASK)
    )

    val x: Long
        get() = (storage and 0x7FFF_FC00_0000_0000 shr (COMPONENT_STORAGE_BITS * 2)) - STORAGE_OFFSET
    val y: Long
        get() = (storage and 0x0000_03FF_FFE0_0000 shr COMPONENT_STORAGE_BITS) - STORAGE_OFFSET
    val z: Long
        get() = (storage and 0x0000_0000_001F_FFFF) - STORAGE_OFFSET

    operator fun plus(other: Beacon): Beacon {
        return Beacon(x + other.x, y + other.y, z + other.z)
    }

    operator fun minus(other: Beacon): Beacon {
        return Beacon(x - other.x, y - other.y, z - other.z)
    }

    companion object {
        const val COMPONENT_STORAGE_BITS: Int = 21
        const val COMPONENT_BIT_MASK: Long = 0x001F_FFFF // this is also the COMPONENT_MAX_UNSIGNED_VALUE
        const val STORAGE_OFFSET: Long = COMPONENT_BIT_MASK / 2
        val REGEX = "(-?\\d+),(-?\\d+),(-?\\d+)".toRegex()

        fun parse(pointStr: String): Beacon {
            val matchResult = REGEX.matchEntire(pointStr) ?: throw IllegalArgumentException("unparsable '$pointStr'")
            return Beacon(
                matchResult.groups[1]!!.value.toLong(),
                matchResult.groups[2]!!.value.toLong(),
                matchResult.groups[3]!!.value.toLong(),
            )
        }
    }

    override fun toString(): String {
        return "($x,$y,$z)"
    }
}

private val ALL_ROTATIONS: List<Beacon.() -> Beacon> = listOf(
    // X ->  X
    { Beacon(x =  x, y =  y, z =  z) },
    { Beacon(x =  x, y = -z, z =  y) },
    { Beacon(x =  x, y = -y, z = -z) },
    { Beacon(x =  x, y =  z, z = -y) },
    // X -> -X
    { Beacon(x = -x, y = -y, z =  z) },
    { Beacon(x = -x, y =  z, z =  y) },
    { Beacon(x = -x, y =  y, z = -z) },
    { Beacon(x = -x, y = -z, z = -y) },

    // X ->  Y
    { Beacon(x =  y, y = -x, z =  z) },
    { Beacon(x =  y, y = -z, z = -x) },
    { Beacon(x =  y, y =  x, z = -z) },
    { Beacon(x =  y, y =  z, z =  x) },

    // X -> -Y
    { Beacon(x = -y, y =  x, z =  z) },
    { Beacon(x = -y, y = -z, z =  x) },
    { Beacon(x = -y, y = -x, z = -z) },
    { Beacon(x = -y, y =  z, z = -x) },

    // X ->  Z
    { Beacon(x =  z, y =  y, z = -x) },
    { Beacon(x =  z, y =  x, z =  y) },
    { Beacon(x =  z, y = -y, z =  x) },
    { Beacon(x =  z, y = -x, z = -y) },

    // X -> -Z
    { Beacon(x = -z, y =  y, z =  x) },
    { Beacon(x = -z, y = -x, z =  y) },
    { Beacon(x = -z, y = -y, z = -x) },
    { Beacon(x = -z, y =  x, z = -y) },
)


fun main() {
    val input = getFile(object {}, FileType.EXAMPLE3).readText()
    val scannerTexts = input.replace("\r\n", "\n").split("\n\n".toRegex())
    val allBeaconsGroups: List<MutableList<Beacon>> = scannerTexts.map { linesInScanner ->
        linesInScanner.lineSequence().drop(1).take(1).map { Beacon.parse(it) }.toMutableList() // TODO remove take()
    }
    println("input beacons")
    println(allBeaconsGroups.joinToString(System.lineSeparator()))

    val (scanner0, scanner1) = allBeaconsGroups
    println("\nscanner0: $scanner0")
    val offset = Beacon(68, -1246, -43)
    println("\noffset: $offset")
    println("\nscanner0 - all rotations + offset")
    ALL_ROTATIONS.forEach { rot -> println(scanner0.map { rot(it) + offset }) }
    println("\nscanner0 - all rotations - offset")
    ALL_ROTATIONS.forEach { rot -> println(scanner0.map { rot(it) - offset }) }


//    val baseBeaconGroups: MutableList<Beacon> = allBeaconsGroups.first()
//    val remainingBeaconGroups: MutableList<MutableList<Beacon>> = allBeaconsGroups.asSequence().drop(1).toMutableList()
//    while (remainingBeacons.isNotEmpty()) {
//        val remainingIterator = remainingBeacons.listIterator()
//        while (remainingIterator.hasNext()) {
//            val crtBeacons = remainingIterator.next()
//
//        }
//
//        ALL_TRANSFORMATIONS[0].transformer.invoke()
//        remainingBeacons.remove { crtBeacons -> beaconsIntersect(baseBeacons, crtBeacons) }
//    }

}

private fun beaconsIntersect(
    baseBeacons: MutableList<Beacon>,
    testedBeacons: MutableList<Beacon>
) {
    TODO("Not yet implemented")
}
