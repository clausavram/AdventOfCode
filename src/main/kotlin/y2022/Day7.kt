package y2022

import util.FileType
import util.getFile

private sealed interface FsEntry {
    val name: String
    var parent: Dir?
}

private data class File(
    override val name: String,
    override var parent: Dir?,
    val size: Int,
) : FsEntry {

    override fun toString(): String {
        return "File(name='$name', parent='${parent?.name}', size=$size)"
    }
}

private data class Dir(
    override val name: String,
    override var parent: Dir?,
    var children: MutableList<FsEntry> = mutableListOf(),
    var computedSize: Int = 0
) : FsEntry {
    override fun toString(): String {
        return "Dir(name='$name', parent='${parent?.name}', children=${children.map { it.name }})"
    }
    fun sizeString(): String {
        return "$name:$computedSize"
    }
}

private val cmdRegex = "\\$ (.*)".toRegex()
private val cdCmdRegex = "cd ([a-zA-Z.]+|/)".toRegex()
private val lsCmdRegex = "ls".toRegex()
private val fileListingRegex = "([0-9]+) ([a-zA-Z.]+)".toRegex()
private val dirListingRegex = "dir ([a-zA-Z.]+)".toRegex()

fun main() {
    val lines = getFile(object {}, FileType.INPUT).readLines()
    val rootDir = parseFileSystemFromCommandLine(lines)
    computeAndGetDirSize(rootDir)

    // part 1:
    val smallDirs = extractDirsThat(rootDir) { it.computedSize <= 100000}
    println("Small dirs (<=100000): ${smallDirs.map { it.sizeString() }}")
    println("Part 1: sum of small dirs' sizes: ${smallDirs.sumOf { it.computedSize }}\n")

    // part 2:
    val excessOccupiedSize = rootDir.computedSize - 40_000_000
    val deleteDirCandidates = extractDirsThat(rootDir) { it.computedSize >= excessOccupiedSize }
        .sortedBy { it.computedSize }
    println("Deletion candidates to free up space: ${deleteDirCandidates.map { it.sizeString() }}")
    println("Part 2: smallest dir that frees up sufficient space: ${deleteDirCandidates.first().sizeString()}")

}

private fun parseFileSystemFromCommandLine(lines: List<String>): Dir {
    val rootDir = Dir("/", null)
    var crtDir = rootDir
    lines.forEach { line ->
        when {
            line matches cmdRegex -> {
                val cmd = cmdRegex.matchEntire(line)!!.groupValues[1]
                when {
                    cmd matches cdCmdRegex -> {
                        val newCrtDirName = cdCmdRegex.matchEntire(cmd)!!.groupValues[1]
                        crtDir = when (newCrtDirName) {
                            "/" -> rootDir
                            ".." -> crtDir.parent!!
                            else -> crtDir.children.first { it is Dir && it.name == newCrtDirName } as Dir
                        }
//                        println("Entered dir:  $crtDir")
                    }

                    cmd matches lsCmdRegex -> {
//                        println("Listing dir contents")
                    }

                    else -> {
                        error("Unrecognized command: '$cmd' in line '$line'")
                    }
                }
            }
            // not doing any validation if there was a prior `ls`
            // not doing any validation if `ls` is run multiple times in the same dir (duplicating FS entries)
            line matches fileListingRegex -> {
                val fileSize = fileListingRegex.matchEntire(line)!!.groupValues[1].toInt()
                val fileName = fileListingRegex.matchEntire(line)!!.groupValues[2]
                val newFile = File(fileName, crtDir, fileSize)
//                println("Created file: $newFile")
                crtDir.children += newFile
            }

            line matches dirListingRegex -> {
                val dirName = dirListingRegex.matchEntire(line)!!.groupValues[1]
                val newDir = Dir(dirName, crtDir)
//                println("Created dir:  $newDir")
                crtDir.children += newDir
            }
        }
    }
    return rootDir
}

private fun computeAndGetDirSize(dir: Dir): Int {
    dir.computedSize = dir.children.sumOf { child ->
        when (child) {
            is File -> child.size
            is Dir -> computeAndGetDirSize(child)
        }
    }
    println("Dir ${dir.name} has computed size: ${dir.computedSize}")
    return dir.computedSize
}

private fun extractDirsThat(dir: Dir, condition: (Dir) -> Boolean): List<Dir> {
    val result = mutableListOf<Dir>()
    extractDirsThatRecursive(dir, condition, result)
    return result
}

private fun extractDirsThatRecursive(dir: Dir, condition: (Dir) -> Boolean, result: MutableList<Dir>) {
    if (condition(dir)) {
        result += dir
    }
    dir.children.asSequence()
        .mapNotNull { it as? Dir }
        .forEach { extractDirsThatRecursive(it, condition, result) }
}