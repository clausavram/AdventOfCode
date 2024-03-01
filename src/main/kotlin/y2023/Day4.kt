package y2023

import util.FileType
import util.getFile
import kotlin.math.pow

private data class Card(
    val name: String, val winningNumbers: LinkedHashSet<Int>, val heldNumbers: LinkedHashSet<Int>, var count: Int = 1
) {
    val wonNumberCount = heldNumbers.intersect(winningNumbers).size
    val exponentialScore: Int by lazy {
        when (wonNumberCount) {
            0 -> 0
            else -> 2.0.pow((wonNumberCount - 1).toDouble()).toInt()
        }
    }
}

private fun parseCard(str: String): Card {
    val (cardName, winningStr, heldStr) = str.split(regex = " *([:|]) *".toRegex())
    val winningNumbers = winningStr.trim().split(" +".toRegex()).map { it.toInt() }.toCollection(LinkedHashSet())
    val heldNumbers = heldStr.trim().split(" +".toRegex()).map { it.toInt() }.toCollection(LinkedHashSet())
    return Card(cardName.trim(), winningNumbers, heldNumbers)
}

fun main() {
    val file = getFile(object {}, FileType.INPUT)
    val cards = file.readLines().map { parseCard(it) }
    for (crtIdx in cards.indices) {
        val crtCard = cards[crtIdx]
        for (nextIdx in crtIdx + 1..crtIdx + crtCard.wonNumberCount) {
            if (nextIdx < cards.size) {
                cards[nextIdx].count += crtCard.count
            }
        }
    }

//    println("cards:\n${cards.joinToString("\n") { "$it -> ${it.exponentialScore}" }}")

    println("Part 1: ${cards.sumOf { it.exponentialScore }}")
    println("Part 2: ${cards.sumOf { it.count }}")
}
