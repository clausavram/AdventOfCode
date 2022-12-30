package y2022

import util.FileType
import util.getFile
import java.util.concurrent.atomic.AtomicInteger

fun main() {
    val signal = getFile(object {}, FileType.INPUT).readText()
    println("Signal: $signal")
    println("Part 1: Start of Packet marker position end: ${findPrefixLength(signal, 4)}")
    println("Part 2: Start of Message marker position end: ${findPrefixLength(signal, 14)}")
}

private fun findPrefixLength(signal: String, markerWidth: Int): Int {
    val charCountsInWindow = mutableMapOf<Char, AtomicInteger>()
    val duplicatedChars = mutableSetOf<Char>()

    signal.subSequence(0 until markerWidth)
        .forEach { addCharToWindow(it, charCountsInWindow, duplicatedChars) }

    for (crtIdx in 0 until (signal.length - markerWidth)) {
        removeCharFromWindow(signal[crtIdx], charCountsInWindow, duplicatedChars)
        addCharToWindow(signal[crtIdx + markerWidth], charCountsInWindow, duplicatedChars)
        if (duplicatedChars.isEmpty()) {
            val startOfPacketMarkerStartIdx = crtIdx + 1
            return startOfPacketMarkerStartIdx + markerWidth
        }
    }
    error("ERROR: reached end of signal w/o finding marker!")
}

private fun addCharToWindow(
    char: Char, charCountsInWindow: MutableMap<Char, AtomicInteger>, duplicatedChars: MutableSet<Char>
) {
    val charCount = charCountsInWindow.computeIfAbsent(char) { AtomicInteger(0) }.incrementAndGet()
    if (charCount > 1) {
        duplicatedChars.add(char)
    }
}

private fun removeCharFromWindow(
    char: Char, charCountsInWindow: MutableMap<Char, AtomicInteger>, duplicatedChars: MutableSet<Char>
) {
    val charCount = charCountsInWindow[char]!!.decrementAndGet()
    if (charCount <= 1) {
        duplicatedChars.remove(char)
    }
}
