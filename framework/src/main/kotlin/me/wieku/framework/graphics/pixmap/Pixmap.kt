package me.wieku.framework.graphics.pixmap

import me.wieku.framework.resource.FileHandle
import org.lwjgl.stb.STBImage.*
import org.lwjgl.stb.STBImageWrite.*
import org.lwjgl.system.MemoryStack
import java.lang.IllegalArgumentException
import java.nio.ByteBuffer
import java.nio.IntBuffer

class Pixmap {

    var width: Int = 0
        private set

    var height: Int = 0
        private set

    /**
     * One dimension array for performance
     */
    lateinit var pixels: ByteArray

    constructor(width: Int, height: Int) {
        this.width = width
        this.height = height
        pixels = ByteArray(width*height)
    }

    constructor(file: FileHandle) {
        val imageBuffer = file.toBuffer()

        MemoryStack.stackPush().use { stack ->
            val w: IntBuffer = stack.mallocInt(1)
            val h: IntBuffer = stack.mallocInt(1)
            val comp: IntBuffer = stack.mallocInt(1)

            stbi_set_flip_vertically_on_load(false)

            if (!stbi_info_from_memory(imageBuffer, w, h, comp)) {
                throw RuntimeException("Failed to read image information: " + stbi_failure_reason())
            }

            var image: ByteBuffer? =
                stbi_load_from_memory(imageBuffer, w, h, comp, 4)
                    ?: throw RuntimeException("Failed to load image: " + stbi_failure_reason())

            width = w[0]
            height = h[0]
            pixels = ByteArray(width*height*4)
            image!!.get(pixels)
        }
    }

    fun export(file: FileHandle) {
        MemoryStack.stackPush().use { stack ->
            val buffer = stack.malloc(pixels.size)
            buffer.put(pixels)
            buffer.flip()
            when(file.file.extension) {
                "png" -> stbi_write_png(file.file.absolutePath, width, height, 4, buffer, 0)
                "jpg" -> stbi_write_jpg(file.file.absolutePath, width, height, 4, buffer, 90)
                else -> throw IllegalArgumentException("Wrong format specified!")
            }
        }
    }

}