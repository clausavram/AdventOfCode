package y2021

import util.FileType
import util.getFile
import y2021.day18.SnailFishNumberParser

private const val SHOULD_LOG = false
private fun log(str: () -> String) { if (SHOULD_LOG) println(str) }

sealed class SnailFishNumber(var parent: PairNumber?) {
    abstract fun firstSimpleNumberAboveTen(): SimpleNumber?
    abstract fun magnitude(): Int
    abstract fun copy(): SnailFishNumber
}

class PairNumber(var left: SnailFishNumber, var right: SnailFishNumber, parent: PairNumber?) : SnailFishNumber(parent) {
    constructor(left: SnailFishNumber, right: SnailFishNumber) : this(left, right, null)

    fun getChild(rightChild: Boolean): SnailFishNumber = if (rightChild) right else left
    override fun firstSimpleNumberAboveTen() = left.firstSimpleNumberAboveTen() ?: right.firstSimpleNumberAboveTen()
    override fun magnitude() = 3 * left.magnitude() + 2 * right.magnitude()
    override fun copy(): PairNumber = PairNumber(left.copy(), right.copy()).also {
        it.left.parent = it
        it.right.parent = it
    }
    override fun toString() = "[$left,$right]"
}

class SimpleNumber(var value: Int, parent: PairNumber?) : SnailFishNumber(parent) {
    constructor(value: Int) : this(value, null)

    override fun firstSimpleNumberAboveTen() = if (value >= 10) this else null
    override fun magnitude() = value
    override fun copy(): SimpleNumber = SimpleNumber(value)
    override fun toString(): String = "$value"
}

private operator fun SnailFishNumber.plus(right: SnailFishNumber): PairNumber {
    var result = PairNumber(this.copy(), right.copy()).also {
        it.left.parent = it
        it.right.parent = it
    }

    var iteration = 0
    var anythingChanged = true
    while (anythingChanged) {
        iteration++.also { log { "Running iteration ${it}, on: $result" } }

        val explodeResult = result.explode()
        result = explodeResult.second
        anythingChanged = explodeResult.first
        if (anythingChanged) continue

        val splitResult = result.split()
        result = splitResult.second
        anythingChanged = splitResult.first
    }
    log { "Finished!" }
    return result
}

private fun PairNumber.explode(): Pair<Boolean, PairNumber> {
    val result = this.copy()
    val explodedPairNumber = result.firstPairChildAtDepth(4) ?: return false to this
    log { "Exploding $result on $explodedPairNumber" }

    // add left SimpleNumber child to adjacent left SimpleNumber (right-most left SimpleNumber) + same for right child
    addSimpleValueToNextNeighbor(explodedPairNumber, true)
    addSimpleValueToNextNeighbor(explodedPairNumber, false)

    val immediateParent = explodedPairNumber.parent!!
    if (immediateParent.left == explodedPairNumber) {
        immediateParent.left = SimpleNumber(0, immediateParent)
    } else {
        immediateParent.right = SimpleNumber(0, immediateParent)
    }
    log { "Explosion result: $result" }
    return true to result
}

private fun addSimpleValueToNextNeighbor(explodedPairNumber: PairNumber, addRightChild: Boolean) {
    var prevParent = explodedPairNumber
    var crtParent: PairNumber? = explodedPairNumber.parent!!
    while (crtParent != null && crtParent.getChild(addRightChild) == prevParent) {
        prevParent = crtParent
        crtParent = crtParent.parent
    }
    if (crtParent != null) {
        var crtNumber = crtParent.getChild(addRightChild)
        while (crtNumber is PairNumber) {
            crtNumber = crtNumber.getChild(!addRightChild)
        }
        (crtNumber as SimpleNumber).value += (explodedPairNumber.getChild(addRightChild) as SimpleNumber).value
    }
}

private fun PairNumber.firstPairChildAtDepth(depth: Int): PairNumber? {
    if (depth <= 0 && left is SimpleNumber && right is SimpleNumber) return this
    return (left as? PairNumber)?.firstPairChildAtDepth(depth - 1) ?: (right as? PairNumber)?.firstPairChildAtDepth(depth - 1)
}

private fun PairNumber.split(): Pair<Boolean, PairNumber> {
    val result = this.copy()
    val numberToSlit: SimpleNumber = result.firstSimpleNumberAboveTen() ?: return false to this
    log { "Splitting $result on $numberToSlit" }
    val parent = numberToSlit.parent!!
    val newPairNumber = PairNumber(SimpleNumber(numberToSlit.value / 2), SimpleNumber((numberToSlit.value + 1) / 2), parent)
    newPairNumber.left.parent = newPairNumber
    newPairNumber.right.parent = newPairNumber
    if (parent.left == numberToSlit) parent.left = newPairNumber
    else parent.right = newPairNumber
    log { "Split result: $result" }
    return true to result
}

private fun parseNumber(str: String): PairNumber {
    return SnailFishNumberParser.parseString(str)
}

fun main() {
    val numbers = getFile(object{}, FileType.INPUT).readLines().map { parseNumber(it) }
    println("Part1: ${numbers.reduce(PairNumber::plus).magnitude()}")
    println("Part2: ${findAllArrangementsOfTwo(numbers).maxOf { it.first.plus(it.second).magnitude() }}")
}

fun <T> findAllArrangementsOfTwo(values: List<T>) = sequence {
    for (left in values) {
        for (right in values) {
            if (left !== right) {
                yield(left to right)
            }
        }
    }
}
