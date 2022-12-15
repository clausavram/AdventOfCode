package y2022

import util.FileType
import util.getFile

private enum class Hand(val intrinsicScore: Int) {
    Rock(1),
    Paper(2),
    Scissors(3);
}

private fun Hand.scoreAgainst(otherHand: Hand): Int {
    val winScore = when (this) {
        Hand.Rock -> when (otherHand) {Hand.Rock -> 3; Hand.Paper -> 0; Hand.Scissors -> 6 }
        Hand.Paper -> when (otherHand) {Hand.Rock -> 6; Hand.Paper -> 3; Hand.Scissors -> 0 }
        Hand.Scissors -> when (otherHand) {Hand.Rock -> 0; Hand.Paper -> 6; Hand.Scissors -> 3 }
    }
    return winScore + intrinsicScore
}

private val opponentMapping = mapOf(
    "A" to Hand.Rock,
    "B" to Hand.Paper,
    "C" to Hand.Scissors
)

private val personalHandMapping = mapOf(
    "X" to Hand.Rock,
    "Y" to Hand.Paper,
    "Z" to Hand.Scissors
)

private val outcomeToPersonalHandMapping: Map<String, (Hand) -> Hand> = mapOf(
    // lose
    "X" to { opponentHand ->
        when (opponentHand) {
            Hand.Rock -> Hand.Scissors; Hand.Paper -> Hand.Rock; Hand.Scissors -> Hand.Paper
        }
    },
    // tie
    "Y" to { opponentHand -> opponentHand },
    "Z" to { opponentHand ->
        when (opponentHand) {
            Hand.Rock -> Hand.Paper; Hand.Paper -> Hand.Scissors; Hand.Scissors -> Hand.Rock
        }
    }
)

fun main() {
    val lines = getFile(object {}, FileType.INPUT).readLines()

    val totalScore1 = lines.map { it.split(' ', limit = 2) }
        .map { opponentMapping[it[0]]!! to personalHandMapping[it[1]]!! }
        .sumOf { it.second.scoreAgainst(it.first) }
    println("Part 1: $totalScore1")

    val totalScore2 = lines.map { it.split(' ', limit = 2) }
        .map { opponentMapping[it[0]]!! to it[1] }
        .map { it.first to outcomeToPersonalHandMapping[it.second]!!(it.first) }
        .sumOf { it.second.scoreAgainst(it.first) }
    println("Part 2: $totalScore2")

}


