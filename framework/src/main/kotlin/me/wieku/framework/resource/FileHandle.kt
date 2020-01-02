package me.wieku.framework.resource

import org.lwjgl.BufferUtils
import java.io.File
import java.io.InputStream
import java.lang.IllegalStateException
import java.net.URL
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.file.Path
import java.nio.file.Paths

enum class FileType {
    Absolute,
    Classpath,
    Local
}

class FileHandle(path: String, val fileType: FileType) {
    var fileURL: URL
    var filePath: Path? = null
    var file: File = File(path)

    init {
        fileURL = when (fileType) {
            FileType.Absolute -> {
                if (!file.isAbsolute) {
                    throw IllegalStateException("Opening local file as absolute: ${file.absolutePath}")
                }

                filePath = Paths.get(path)
                filePath!!.toUri().toURL()
            }
            FileType.Classpath -> {
                filePath = Paths.get(path.replace('\\', '/'))
                FileHandle::class.java.getResource("/" + file.path.replace('\\', '/'))
            }
            FileType.Local -> {
                filePath = Paths.get(path)
                filePath!!.toUri().toURL()
            }
        }
    }

    fun asString(charset: Charset = Charsets.UTF_8) = fileURL.readText(charset)

    fun absolutePath(): String {
        if(fileType == FileType.Classpath) {
            throw IllegalStateException("Getting absolute path on classpath file")
        }

        return filePath!!.toAbsolutePath().toString()
    }

    fun toBuffer(flip: Boolean = true): ByteBuffer {
        val bytes = fileURL.readBytes()
        val buffer = BufferUtils.createByteBuffer(bytes.size).put(bytes)
        return if(flip) (buffer.flip() as ByteBuffer) else buffer
    }

    fun inputStream(): InputStream {
        return fileURL.openStream()
    }

}