package y2021

import util.FileType
import util.getFile
import kotlin.system.measureTimeMillis

fun main() {
    val (polymerTemplate, rules) = parseInput(getFile(object{}, FileType.INPUT).readLines())
    println(measureTimeMillis { partOne(polymerTemplate, rules) })
    println(measureTimeMillis { partTwo(polymerTemplate, rules) })
}

private fun parseInput(input: List<String>): Pair<String, Map<Pair<Char, Char>, Char>> {
    val pairStrings = input.toMutableList()
    val polymerTemplate = pairStrings.removeFirst()
    pairStrings.removeFirst()
    val rules = pairStrings.associate { (it[0] to it[1]) to it[6] }
    return polymerTemplate to rules
}

private class Node(val value: Char, var left: Node? = null, var right: Node? = null) {
    override fun toString(): String = "$value"
}

private fun partOne(polymerTemplate: String, rulesMap: Map<Pair<Char, Char>, Char>) {
    val firstNode: Node? = buildPolymerList(polymerTemplate)
    (1..10).forEach { _ -> growPolymer(firstNode, rulesMap) }
    val countMap = countElements(firstNode)
    val countList = countMap.toList().sortedByDescending { it.second }
    println("Part 1: ${countList.first().second - countList.last().second}")
}
private fun buildPolymerList(polymerTemplate: String): Node? {
    var firstNode: Node? = null
    var lastNode: Node? = null
    for (element in polymerTemplate) {
        val crtNode = Node(element)
        if (firstNode == null) {
            firstNode = crtNode
        } else {
            lastNode!!.right = crtNode
            crtNode.left = lastNode
        }
        lastNode = crtNode
    }
    return firstNode
}

private fun growPolymer(firstNode: Node?, rulesMap: Map<Pair<Char, Char>, Char>) {
    var crtNode: Node? = firstNode!!.right
    while (crtNode != null) {
        rulesMap[crtNode.left!!.value to crtNode.value]?.let { insertedElement ->
            val newNode = Node(insertedElement, crtNode!!.left, crtNode)
            crtNode!!.left!!.right = newNode
            crtNode!!.left = newNode
        }

        crtNode = crtNode.right
    }
}

@Suppress("unused")
private fun printPolymer(firstNode: Node?) {
    var crtNode: Node? = firstNode
    print("[")
    while (crtNode != null) {
        print(crtNode.value)
        crtNode = crtNode.right
    }
    print("]")
}

private fun countElements(firstNode: Node?): Map<Char, Long> {
    val elementCounts = sortedMapOf<Char, Long>()
    var crtNode: Node? = firstNode
    while (crtNode != null) {
        elementCounts.compute(crtNode.value) { _, oldCount -> (oldCount ?: 0L) + 1L}
        crtNode = crtNode.right
    }
    return elementCounts
}

private fun partTwo(polymerTemplate: String, rulesMap: Map<Pair<Char, Char>, Char>) {
    val startingBigramCounters = stringToBigramCounters(polymerTemplate)
    val finalBigramCounters: Map<Pair<Char, Char>, Long> = growBigramPolymer(startingBigramCounters, rulesMap, 40)
    val elementCounters: Map<Char, Long> = bigramToElementCounters(finalBigramCounters, polymerTemplate)
    val sortedCounters = elementCounters.toList().sortedByDescending { it.second }
    println("Part 2: ${sortedCounters.first().second - sortedCounters.last().second}")
    println("Part 2: total length: ${elementCounters.values.sum()}, each count: $elementCounters")
}

private fun stringToBigramCounters(polymerTemplate: String) = polymerTemplate.zipWithNext()
    .groupingBy { it }
    .eachCount()
    .mapValues { it.value.toLong() }

private fun growBigramPolymer(
    startingBigramCounters: Map<Pair<Char, Char>, Long>,
    rulesMap: Map<Pair<Char, Char>, Char>,
    @Suppress("SameParameterValue") iterations: Long
): Map<Pair<Char, Char>, Long> {
    var currentCounters = startingBigramCounters.toMutableMap()
    var newCounters = mutableMapOf<Pair<Char, Char>, Long>()

    for (currentIteration in 1..iterations) {
        newCounters.clear()
        for ((bigram, currentCount) in currentCounters) {
            rulesMap[bigram]?.let { newChar ->
                newCounters.merge(bigram.first to newChar, currentCount, Long::plus)
                newCounters.merge(newChar to bigram.second, currentCount, Long::plus)
            }
        }
        currentCounters = newCounters.also { newCounters = currentCounters }
    }

    return currentCounters
}

fun bigramToElementCounters(bigramCounters: Map<Pair<Char, Char>, Long>, polymerTemplate: String): Map<Char, Long> {
    val elementCounters = mutableMapOf<Char, Long>()
    bigramCounters.forEach {
        elementCounters.merge(it.key.first, it.value, Long::plus)
        elementCounters.merge(it.key.second, it.value, Long::plus)
    }
    elementCounters.compute(polymerTemplate.first()) { _, old -> old!! + 1 }
    elementCounters.compute(polymerTemplate.last()) { _, old -> old!! + 1 }
    elementCounters.replaceAll { _, count -> count / 2 }
    return elementCounters
}


