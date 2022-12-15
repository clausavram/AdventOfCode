package util

import java.io.File

@Suppress("unused")
enum class FileType {
    EXAMPLE, INPUT, EXAMPLE2, EXAMPLE3;

    override fun toString() = super.toString().lowercase()
}

fun getFile(enclosedObj: Any, fileType: FileType): File {
    val enclosingClass = enclosedObj.javaClass.enclosingClass
    val fileNameBase = enclosingClass.simpleName.removeSuffix("Kt").lowercase()
    val fileName = "$fileNameBase.$fileType.txt"
    val url = enclosingClass.getResource(fileName) ?: throw IllegalArgumentException("file '$fileName' not found")
    return File(url.toURI())
}
