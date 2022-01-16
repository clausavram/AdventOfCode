package y2021

import util.FileType
import util.getFile
import java.lang.IllegalArgumentException

private class Day16

private const val TYPE_LITERAL_VALUE = 4

fun main() {
    val hexData = getFile(Day16::class, FileType.INPUT).readText()
    val reader = BinaryReader(hexData)
    var packetVersionSum = 0
    val evaluationResult = reader.evaluatePacket { packetVersion -> packetVersionSum += packetVersion }
    println("Part 1: sum of packet versions = $packetVersionSum")
    println("Part 2: packet evaluated to: $evaluationResult")
}

private class BinaryReader private constructor(private val bitList: List<Char>) {
    private var currentIndex: Int = 0

    constructor(hexData: String) : this(hexData.asSequence()
        .flatMap { nibble -> nibble.digitToInt(16).or(16).toString(2).substring(1).asSequence() }
        .toList()
    )

    fun evaluatePacket(packetVersionVisitor: (packetVersion: Int) -> Unit): Long {
        val packetVersion = nextInt(3)
        val packetTypeId = nextInt(3)
        packetVersionVisitor(packetVersion)
        return if (packetTypeId == TYPE_LITERAL_VALUE) {
            parseLiteralValue()
        } else {
            val arguments = parseOperatorArguments(packetVersionVisitor)
            return when (packetTypeId) {
                0 -> arguments.sum()
                1 -> arguments.fold(1, Long::times)
                2 -> arguments.minOf { it }
                3 -> arguments.maxOf { it }
                5 -> if (arguments[0] > arguments[1]) 1 else 0
                6 -> if (arguments[0] < arguments[1]) 1 else 0
                7 -> if (arguments[0] == arguments[1]) 1 else 0
                else -> throw IllegalArgumentException("Unknown operation type: $packetTypeId")
            }
        }
    }

    private fun parseLiteralValue(): Long {
        var literalValue = 0L
        var keepReading = true
        while (keepReading) {
            keepReading = nextBit()
            literalValue = literalValue.shl(4).or(nextInt(4).toLong())
        }
        return literalValue
    }

    private fun parseOperatorArguments(packetVersionVisitor: (packetVersion: Int) -> Unit): List<Long> {
        val arguments = mutableListOf<Long>()
        when (nextBit()) {
            false -> {
                val packetBitLength = nextInt(15)
                val finishIndex = currentIndex + packetBitLength
                while (currentIndex < finishIndex) {
                    arguments += evaluatePacket(packetVersionVisitor)
                }
            }
            true -> {
                val subPacketCount = nextInt(11)
                for (crtPacketIdx in 1..subPacketCount) {
                    arguments += evaluatePacket(packetVersionVisitor)
                }
            }
        }
        return arguments
    }

    private fun nextInt(bitCount: Int): Int {
        val result = bitList.subList(currentIndex, currentIndex + bitCount)
            .joinToString("") { it.toString() }
            .toInt(2)
        currentIndex += bitCount
        return result
    }

    private fun nextBit(): Boolean = bitList[currentIndex++] == '1'
}