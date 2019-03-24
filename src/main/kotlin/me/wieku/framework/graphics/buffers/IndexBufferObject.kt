package me.wieku.framework.graphics.buffers

import me.wieku.framework.utils.Disposable
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil
import java.nio.IntBuffer
import java.nio.ShortBuffer

class IndexBufferObject(private val maxIndices: Int, private val useInts: Boolean = false) : Disposable {
    private var indexSize = if (useInts) 4 else 2
    private var byteSize = maxIndices * indexSize

    private var iboHandle: Int = 0

    private var currentIndices = 0

    init {
        iboHandle = glGenBuffers()

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboHandle)

        val buffer = MemoryUtil.memAlloc(byteSize)

        if (useInts) {
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer.asIntBuffer(), GL_DYNAMIC_DRAW)
        } else {
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer.asShortBuffer(), GL_DYNAMIC_DRAW)
        }

        MemoryUtil.memFree(buffer)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    fun setData(data: IntArray) {
        if (!useInts) {
            throw IllegalArgumentException("Wrong array was given")
        }

        if (data.isEmpty()) {
            throw IllegalArgumentException("Empty array was given")
        }

        if (data.size > maxIndices) {
            throw IllegalArgumentException("Input data exceeds buffer size")
        }

        currentIndices = data.size
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, data)
    }

    fun setData(data: ShortArray) {
        if (useInts) {
            throw IllegalArgumentException("Wrong array was given")
        }

        if (data.isEmpty()) {
            throw IllegalArgumentException("Empty array was given")
        }

        if (data.size > maxIndices) {
            throw IllegalArgumentException("Input data exceeds buffer size")
        }

        currentIndices = data.size
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, data)
    }

    /**
     * NOTE: Data have to be flipped before calling this function
     */
    fun setData(data: IntBuffer) {
        if (!useInts) {
            throw IllegalArgumentException("Wrong buffer was given")
        }

        if (data.position() != 0) {
            throw IllegalArgumentException("Unflipped buffer was given")
        }

        if (data.limit() > maxIndices) {
            throw IllegalArgumentException("Input data exceeds buffer size")
        }

        currentIndices = data.limit()
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, data)
    }

    /**
     * NOTE: Data have to be flipped before calling this function
     */
    fun setData(data: ShortBuffer) {
        if (useInts) {
            throw IllegalArgumentException("Wrong buffer was given")
        }

        if (data.position() != 0) {
            throw IllegalArgumentException("Unflipped buffer was given")
        }

        if (data.limit() > maxIndices) {
            throw IllegalArgumentException("Input data exceeds buffer size")
        }

        currentIndices = data.limit()
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, data)
    }

    fun bind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboHandle)
    }

    fun unbind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    fun draw() {
        glDrawElements(GL_TRIANGLES, maxIndices, if (useInts) GL_UNSIGNED_INT else GL_UNSIGNED_SHORT, 0)
    }

    fun draw(from: Int, to: Int) {
        if (to < from || from < 0 || to > maxIndices || from > maxIndices)
            throw IndexOutOfBoundsException("Drawing data out of buffer's memory")

        glDrawElements(GL_TRIANGLES, to - from, if (useInts) GL_UNSIGNED_INT else GL_UNSIGNED_SHORT, from.toLong())
    }

    override fun dispose() {
        glDeleteBuffers(iboHandle)
    }

}