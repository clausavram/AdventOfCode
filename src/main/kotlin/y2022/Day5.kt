package y2022

import util.FileType
import util.getFile
import java.lang.IllegalArgumentException
import java.util.LinkedList
import java.util.TreeMap

val noCrateRegex = " {3}".toRegex()
val crateRegex = "\\[([A-Z])]".toRegex()
val moveRegex = "move ([0-9]+) from ([0-9]) to ([0-9])".toRegex()

private fun parseCrateLine(line: String, crateStacks: MutableMap<Int, LinkedList<Char>>) {
    for (index in 0..line.length step 4) {
        val cratePosition = line.substring(index, index + 3)

        val match = crateRegex.matchEntire(cratePosition)
        if (match != null) {
            val stackIndex = index / 4 + 1
            val stack: LinkedList<Char> = crateStacks.getOrPut(stackIndex) { LinkedList() }
            stack.addFirst(match.groupValues[1][0])
        } else if (!cratePosition.matches(noCrateRegex)) {
            throw IllegalArgumentException("Unknown '$cratePosition' in line '$line'")
        }
    }
}

fun main() {
    val lines = getFile(object {}, FileType.INPUT).readLines()
    val inputCrateStacks = TreeMap<Int, LinkedList<Char>>()

    var lineIdx = 0
    while (lines[lineIdx].substring(0, 3).matches(noCrateRegex) ||
        lines[lineIdx].substring(0, 3).matches(crateRegex)) {
        parseCrateLine(lines[lineIdx], inputCrateStacks)
        lineIdx++
    }

    val part1CrateStacks = copyCrateStacks(inputCrateStacks)
    val part2CrateStacks = copyCrateStacks(inputCrateStacks)


    lineIdx += 2 // skip crate stack names/indexes and empty line

    while (lineIdx < lines.size) {
        val match = moveRegex.matchEntire(lines[lineIdx])!!
        val crateCount = match.groupValues[1].toInt()
        val srcStackIdx = match.groupValues[2].toInt()
        val destStackIdx = match.groupValues[3].toInt()

        // part 1:
        for (crtMove in 1..crateCount) {
            val movedCrate = part1CrateStacks[srcStackIdx]!!.removeLast()
            part1CrateStacks[destStackIdx]!!.addLast(movedCrate)
        }

        // part 2:
        val srcStack = part2CrateStacks[srcStackIdx]!!
        val destStack = part2CrateStacks[destStackIdx]!!
        val movedCratesInSrc = srcStack.subList(srcStack.size - crateCount, srcStack.size)
        destStack.addAll(movedCratesInSrc)
        movedCratesInSrc.clear()

        lineIdx++
    }

    printSolution("Part 1: ", part1CrateStacks)
    printSolution("Part 2: ", part2CrateStacks)
}

private fun printSolution(prefix: String, part1CrateStacks: TreeMap<Int, LinkedList<Char>>) {
    print(prefix)
    for (stackIdx in 1..part1CrateStacks.size) {
        print(part1CrateStacks[stackIdx]!!.last)
    }
    println()
}

private fun copyCrateStacks(inputCrateStacks: TreeMap<Int, LinkedList<Char>>): TreeMap<Int, LinkedList<Char>> {
    return TreeMap<Int, LinkedList<Char>>().apply {
        inputCrateStacks.forEach { (idx, cratesStack) -> this[idx] = LinkedList(cratesStack) }
    }
}
