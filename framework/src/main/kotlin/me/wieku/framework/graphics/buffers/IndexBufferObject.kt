package me.wieku.framework.graphics.buffers

import me.wieku.framework.utils.Disposable
import org.lwjgl.opengl.ARBBaseInstance
import org.lwjgl.opengl.GL33.*
import java.nio.IntBuffer
import java.nio.ShortBuffer
import java.util.*

class IndexBufferObject(
    private val maxIndices: Int,
    private val useInts: Boolean = false,
    drawMode: DrawMode = DrawMode.DynamicDraw
) : Disposable {

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
        check(!disposed) { "Can't bind disposed IBO" }
        check(!isBound) { "IBO is already bound" }

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

    fun draw(from: Int = 0, to: Int = currentIndices, fromInstance: Int = 0, toInstance: Int = 1) {
        check(isBound) { "IBO is not bound" }
        check(from in 0..to && to <= maxIndices) { "Drawing data out of buffer's memory" }

        ARBBaseInstance.glDrawElementsInstancedBaseInstance(
            GL_TRIANGLES,
            to - from,
            if (useInts) GL_UNSIGNED_INT else GL_UNSIGNED_SHORT,
            from.toLong(),
            toInstance - fromInstance,
            fromInstance
        )
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
        check(isBound) { "IBO is not bound" }
        require(useInts) { "Wrong array was given" }
        require(!data.isEmpty()) { "Empty array was given" }
        require(data.size <= maxIndices) { "Input data exceeds buffer size" }

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
        check(isBound) { "IBO is not bound" }
        require(!useInts) { "Wrong array was given" }
        require(!data.isEmpty()) { "Empty array was given" }
        require(data.size <= maxIndices) { "Input data exceeds buffer size" }

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
        check(isBound) { "IBO is not bound" }
        require(useInts) { "Wrong buffer was given" }
        require(data.position() == 0) { "Unflipped buffer was given" }
        require(data.limit() <= maxIndices) { "Input data exceeds buffer size" }

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
        check(isBound) { "IBO is not bound" }
        require(!useInts) { "Wrong buffer was given" }
        require(data.position() == 0) { "Unflipped buffer was given" }
        require(data.limit() <= maxIndices) { "Input data exceeds buffer size" }

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