package me.wieku.framework.graphics.buffers

import me.wieku.framework.utils.Disposable
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL33.*
import java.nio.FloatBuffer
import java.util.*

class VertexBufferObject(private val floats: Int, drawMode: DrawMode = DrawMode.DynamicDraw) : Disposable {

    private var vboHandle = glGenBuffers()
    private var isBound = false
    private var disposed = false

    init {
        bind()
        glBufferData(GL_ARRAY_BUFFER, floats.toLong() * 4, drawMode.glEnumID)
        unbind()
    }

    fun bind() {
        check(!disposed) { "Can't bind disposed VBO" }
        check(!isBound) { "VBO is already bound" }

        stack.push(glGetInteger(GL_ARRAY_BUFFER_BINDING))
        glBindBuffer(GL_ARRAY_BUFFER, vboHandle)
        isBound = true
    }

    fun unbind() {
        if (disposed || !isBound) return

        val binding = stack.pop()
        GL15.glBindBuffer(GL_ARRAY_BUFFER, binding ?: 0)
        isBound = false
    }

    override fun dispose() {
        if (disposed) return
        glDeleteBuffers(vboHandle)
        disposed = true
    }

    /**
     * Pushes the data to VBO.
     * It can be more efficient than [setData(data: FloatBuffer)]
     *
     * @param data [FloatArray] containing data
     */
    fun setData(data: FloatArray) {
        check(isBound) { "VBO is not bound" }
        require(data.isNotEmpty()) { "Empty array was given" }
        require(data.size <= floats) { "Input data exceeds buffer size" }

        glBufferSubData(GL_ARRAY_BUFFER, 0, data)
    }

    /**
     * Pushes the data to VBO.
     * It can be less efficient than [setData(data: FloatArray)]
     *
     * @param data [java.nio.FloatBuffer] containing data. It has to be flipped before calling this method
     */
    fun setData(data: FloatBuffer) {
        check(isBound) { "VBO is not bound" }
        require(data.position() == 0) { "Unflipped buffer was given" }
        require(data.limit() <= floats) { "Input data exceeds buffer size" }

        glBufferSubData(GL_ARRAY_BUFFER, 0, data)
    }

    @Suppress("ProtectedInFinal", "Unused")
    protected fun finalize() {
        dispose()
    }

    /**
     * Companion to store vbo stack (useful to restore the context of the previous one)
     */
    private companion object {
        private var stack = ArrayDeque<Int>()
    }

}