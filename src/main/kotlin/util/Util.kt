package util

import java.io.File
import kotlin.reflect.KClass

@Suppress("unused")
enum class FileType {
    EXAMPLE, INPUT, SMALL_SAMPLE;

    override fun toString() = super.toString().lowercase()
}

fun getFile(clazz: KClass<*>, fileType: FileType): File {
    return File(clazz.java.getResource("${clazz.simpleName!!.lowercase()}.$fileType.txt")!!.toURI())
}
