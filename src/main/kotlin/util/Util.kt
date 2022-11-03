package util

import java.io.File

@Suppress("unused")
enum class FileType {
    EXAMPLE, INPUT, EXAMPLE2, EXAMPLE3;

    override fun toString() = super.toString().lowercase()
}

fun getFile(enclosedObj: Any, fileType: FileType): File {
    val enclosingClass = enclosedObj.javaClass.enclosingClass
    val fileName = enclosingClass.simpleName.removeSuffix("Kt").lowercase()
    return File(enclosingClass.getResource("$fileName.$fileType.txt")!!.toURI())
}
