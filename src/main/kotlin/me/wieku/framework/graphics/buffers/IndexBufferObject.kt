package me.wieku.framework.graphics.buffers

import me.wieku.framework.utils.Disposable
import org.lwjgl.opengl.ARBBaseInstance
import org.lwjgl.opengl.GL33.*
import java.nio.IntBuffer
import java.nio.ShortBuffer
import java.util.*

class IndexBufferObject(private val maxIndices: Int, private val useInts: Boolean = false, drawMode: DrawMode = DrawMode.DynamicDraw) : Disposable {
    private var indexSize = if (useInts) 4 else 2
    private var byteSize = maxIndices * indexSize

    private var iboHandle = glGenBuffers()

    private var isBound = false
    private var disposed = false
    private var currentIndices = 0

    init {
        bind()
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, byteSize.toLong(), drawMode.glEnumID)
        unbind()
    }

    fun bind() {
        if (disposed) throw IllegalStateException("Can't bind disposed IBO")
        if (isBound) throw IllegalStateException("IBO is already bound")

        stack.push(glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING))
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboHandle)
        isBound = true
    }

    fun unbind() {
        if (disposed || !isBound) return

        val binding = stack.pop()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, binding ?: 0)
        isBound = false
    }

    fun drawInstanced(fromInstance: Int = 0, toInstance: Int = 1, from: Int = 0, to: Int = currentIndices) {
        if (!isBound) throw IllegalStateException("IBO is not bound")

        ARBBaseInstance.glDrawElementsInstancedBaseInstance(
            GL_TRIANGLES,
            to - from,
            if (useInts) GL_UNSIGNED_INT else GL_UNSIGNED_SHORT,
            from.toLong(),
            toInstance - fromInstance,
            fromInstance
        )
    }

    fun draw(from: Int = 0, to: Int = currentIndices) {
        if (!isBound) throw IllegalStateException("IBO is not bound")

        if (to < from || from < 0 || to > maxIndices || from > maxIndices)
            throw IndexOutOfBoundsException("Drawing data out of buffer's memory")

        glDrawElements(GL_TRIANGLES, to - from, if (useInts) GL_UNSIGNED_INT else GL_UNSIGNED_SHORT, from.toLong())
    }

    override fun dispose() {
        if (disposed) return
        glDeleteBuffers(iboHandle)
        disposed = true
    }

    /**
     * Pushes the data to IBO.
     * It can be more efficient than [setData(data: IntBuffer)]
     *
     * @param data [IntArray] containing data
     * @throws [java.lang.IllegalArgumentException] if IBO was instantiated with [useInts] as false
     */
    fun setData(data: IntArray) {
        if (!isBound) throw IllegalStateException("IBO is not bound")
        if (!useInts) throw IllegalArgumentException("Wrong array was given")
        if (data.isEmpty()) throw IllegalArgumentException("Empty array was given")
        if (data.size > maxIndices) throw IllegalArgumentException("Input data exceeds buffer size")

        currentIndices = data.size
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, data)
    }

    /**
     * Pushes the data to IBO.
     * It can be more efficient than [setData(data: ShortBuffer)]
     *
     * @param data [ShortArray] containing data
     * @throws [java.lang.IllegalArgumentException] if IBO was instantiated with [useInts] as true
     */
    fun setData(data: ShortArray) {
        if (!isBound) throw IllegalStateException("IBO is not bound")
        if (useInts) throw IllegalArgumentException("Wrong array was given")
        if (data.isEmpty()) throw IllegalArgumentException("Empty array was given")
        if (data.size > maxIndices) throw IllegalArgumentException("Input data exceeds buffer size")

        currentIndices = data.size
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, data)
    }

    /**
     * Pushes the data to IBO.
     * It can be less efficient than [setData(data: IntArray)]
     *
     * @param data [java.nio.IntBuffer] containing data. It has to be flipped before calling this method
     * @throws [java.lang.IllegalArgumentException] if IBO was instantiated with [useInts] as false
     */
    fun setData(data: IntBuffer) {
        if (!isBound) throw IllegalStateException("IBO is not bound")
        if (!useInts) throw IllegalArgumentException("Wrong buffer was given")
        if (data.position() != 0) throw IllegalArgumentException("Unflipped buffer was given")
        if (data.limit() > maxIndices) throw IllegalArgumentException("Input data exceeds buffer size")

        currentIndices = data.limit()
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, data)
    }

    /**
     * Pushes the data to IBO.
     * It can be less efficient than [setData(data: ShortArray)]
     *
     * @param data [java.nio.ShortBuffer] containing data. It has to be flipped before calling this method
     * @throws [java.lang.IllegalArgumentException] if IBO was instantiated with [useInts] as true
     */
    fun setData(data: ShortBuffer) {
        if (!isBound) throw IllegalStateException("IBO is not bound")
        if (useInts) throw IllegalArgumentException("Wrong buffer was given")
        if (data.position() != 0) throw IllegalArgumentException("Unflipped buffer was given")
        if (data.limit() > maxIndices) throw IllegalArgumentException("Input data exceeds buffer size")

        currentIndices = data.limit()
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, data)
    }

    @Suppress("ProtectedInFinal", "Unused")
    protected fun finalize() {
        dispose()
    }

    /**
     * Companion to store ibo stack (useful to restore the context of the previous one)
     */
    private companion object {
        private var stack = ArrayDeque<Int>()
    }

}