package me.wieku.framework.graphics.buffers

import me.wieku.framework.graphics.shaders.Shader
import me.wieku.framework.utils.Disposable
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer

class VertexArrayObject(private var maxVertices: Int, private val attributes: Array<VertexAttribute>) : Disposable {
    private var vertexSize = attributes.vertexSize()
    private var byteSize = maxVertices * attributes.vertexSize()

    private var vaoHandle: Int = 0
    private var vboHandle: Int = 0

    private var currentVertices = 0

    init {
        vaoHandle = glGenVertexArrays()

        glBindVertexArray(vaoHandle)

        vboHandle = glGenBuffers()

        glBindBuffer(GL_ARRAY_BUFFER, vboHandle)

        val buffer = MemoryUtil.memAlloc(byteSize)
        glBufferData(GL_ARRAY_BUFFER, buffer.asFloatBuffer(), GL_DYNAMIC_DRAW)
        MemoryUtil.memFree(buffer)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)

        attributes.sortBy { it.attributeIndex }
    }

    fun bindToShader(shader: Shader) {

        var offset = 0

        attributes.forEach {
            if (it.attributeType.size > 16)
                throw IllegalStateException("Cannot use ${it.attributeType.name} as an attribute")

            val location = shader.attributes[it.attributeName]!!.location

            glVertexAttribPointer(
                location,
                it.attributeType.size / 4,
                if (it.attributeType == VertexAttributeType.GlInt) GL_INT else GL_FLOAT,
                false,
                attributes.vertexSize(),
                offset.toLong()
            )
            glEnableVertexAttribArray(location)

            offset += it.attributeType.size
        }

    }

    fun setData(data: FloatArray) {
        if (data.isEmpty()) {
            throw IllegalArgumentException("Empty array was given")
        }

        if (data.size > byteSize / 4) {
            throw IllegalArgumentException("Input data exceeds buffer size")
        }

        if (data.size % vertexSize != 0) {
            throw IllegalArgumentException("Vertex size does not match")
        }

        currentVertices = 4 * data.size / vertexSize
        glBufferSubData(GL_ARRAY_BUFFER, 0, data)
    }

    /**
     * NOTE: Data have to be flipped before calling this function
     */
    fun setData(data: FloatBuffer) {
        if (data.position() != 0) {
            throw IllegalArgumentException("Unflipped buffer was given")
        }

        if (data.limit() > byteSize / 4) {
            throw IllegalArgumentException("Input data exceeds buffer size")
        }

        if (data.limit() % vertexSize != 0) {
            throw IllegalArgumentException("Vertex size does not match")
        }

        currentVertices = 4 * data.limit() / vertexSize
        glBufferSubData(GL_ARRAY_BUFFER, 0, data)
    }

    fun bind() {
        glBindVertexArray(vaoHandle)
        glBindBuffer(GL_ARRAY_BUFFER, vboHandle)
    }

    fun bindRender() {
        glBindVertexArray(vaoHandle)
    }

    fun unbind() {
        glBindVertexArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    fun unbindRender() {
        glBindVertexArray(0)
    }

    fun draw() {
        glDrawArrays(GL_TRIANGLES, 0, maxVertices)
    }

    fun draw(from: Int, to: Int) {
        if (to < from || from < 0 || to > maxVertices || from > maxVertices)
            throw IndexOutOfBoundsException("Drawing data out of buffer's memory")

        glDrawArrays(GL_TRIANGLES, from, to - from)
    }

    override fun dispose() {
        glDeleteBuffers(vboHandle)
        glDeleteVertexArrays(vaoHandle)
    }

}