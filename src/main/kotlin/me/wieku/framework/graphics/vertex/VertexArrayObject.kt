package me.wieku.framework.graphics.vertex

import me.wieku.framework.graphics.shaders.Shader
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer

class VertexArrayObject(private var maxVertices: Int, private val attributes: Array<VertexAttribute>) {
    private var byteSize = maxVertices * attributes.vertexSize()

    private var vaoHandle: Int = 0
    private var vboHandle: Int = 0

    init {
        vaoHandle = glGenVertexArrays()

        glBindVertexArray(vaoHandle)

        vboHandle = glGenBuffers()

        glBindBuffer(GL_ARRAY_BUFFER, vboHandle)

        val buffer = MemoryUtil.memAlloc(byteSize).asFloatBuffer() as FloatBuffer
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW)
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
            throw IllegalStateException("Drawing out of memory data")

        glDrawArrays(GL_TRIANGLES, from, to - from)
    }

}