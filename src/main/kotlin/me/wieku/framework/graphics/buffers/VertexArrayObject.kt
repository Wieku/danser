package me.wieku.framework.graphics.buffers

import me.wieku.framework.graphics.shaders.Shader
import me.wieku.framework.utils.Disposable
import org.lwjgl.opengl.ARBBaseInstance
import org.lwjgl.opengl.GL33.*
import java.nio.FloatBuffer
import java.util.*

class VertexArrayObject : Disposable {
    private var vaoHandle: Int = glGenVertexArrays()

    private var isBound = false
    private var disposed = false

    private val vboMap = HashMap<String, VBOHolder>()

    private var currentVertices = 0
    private var currentInstances = 1

    init {
        bind()
        unbind()
    }

    fun bindToShader(shader: Shader) {
        check(isBound) { "VBO is not bound" }

        vboMap.values.forEach { vboHolder ->

            vboHolder.vbo.bind()

            var offset = 0

            vboHolder.attributes.forEach {
                require(it.attributeType.size <= 16) {
                    "Cannot use ${it.attributeType.name} as an attribute"
                }

                val location = shader.attributes[it.attributeName]!!.location

                glVertexAttribPointer(
                    location,
                    it.attributeType.size / 4,
                    if (it.attributeType == VertexAttributeType.GlInt) GL_INT else GL_FLOAT,
                    false,
                    vboHolder.attributes.vertexSize(),
                    offset.toLong()
                )
                glVertexAttribDivisor(location, vboHolder.divisor)
                glEnableVertexAttribArray(location)

                offset += it.attributeType.size
            }

            vboHolder.vbo.unbind()
        }

    }

    fun addVBO(name: String, maxVertices: Int, divisor: Int, attributes: Array<VertexAttribute>) {
        check(!vboMap.containsKey(name)) { "VBO with that name already exists" }

        attributes.sortBy { it.attributeIndex }
        val holder = VBOHolder(
            VertexBufferObject(maxVertices * attributes.vertexSize() / 4),
            maxVertices,
            divisor,
            attributes.vertexSize() / 4,
            attributes
        )

        vboMap[name] = holder
    }

    fun setData(vboName: String, data: FloatArray) {
        require(vboMap.containsKey(vboName)) { "VBO with that name does not exist" }
        val vboHolder = vboMap[vboName]!!

        require(data.isNotEmpty()) { "Empty array was given" }
        require(data.size <= vboHolder.maxVertices * vboHolder.vertexSize) { "Input data exceeds buffer size" }
        require(data.size % vboHolder.vertexSize == 0) { "Vertex size does not match" }

        when (vboHolder.divisor) {
            0 -> currentVertices = data.size / vboHolder.vertexSize
            else -> currentInstances = data.size / vboHolder.vertexSize * vboHolder.divisor
        }

        vboHolder.vbo.bind()
        vboHolder.vbo.setData(data)
        vboHolder.vbo.unbind()
    }

    /**
     * NOTE: Data have to be flipped before calling this function
     */
    fun setData(vboName: String, data: FloatBuffer) {
        require(vboMap.containsKey(vboName)) { "VBO with that name does not exist" }
        val vboHolder = vboMap[vboName]!!

        require(data.position() == 0) { "Unflipped buffer was given" }
        require(data.limit() <= vboHolder.maxVertices * vboHolder.vertexSize) { "Input data exceeds buffer size" }
        require(data.limit() % vboHolder.vertexSize == 0) { "Vertex size does not match" }

        when (vboHolder.divisor) {
            0 -> currentVertices = data.limit() / vboHolder.vertexSize
            else -> currentInstances = data.limit() / vboHolder.vertexSize * vboHolder.divisor
        }

        vboHolder.vbo.bind()
        vboHolder.vbo.setData(data)
        vboHolder.vbo.unbind()
    }

    fun bind() {
        check(!disposed) { "Can't bind disposed VAO" }
        check(!isBound) { "VBO is already bound" }

        stack.push(glGetInteger(GL_VERTEX_ARRAY_BINDING))
        glBindVertexArray(vaoHandle)
        isBound = true
    }

    fun unbind() {
        if (disposed || !isBound) return

        val binding = stack.pop()
        glBindVertexArray(binding ?: 0)
        isBound = false
    }

    fun draw(from: Int = 0, to: Int = currentVertices, fromInstance: Int = 0, toInstance: Int = currentInstances) {
        check(from in 0..to/* && to <= maxVertices*/) { "Drawing data out of buffer's memory" }

        ARBBaseInstance.glDrawArraysInstancedBaseInstance(
            GL_TRIANGLES,
            from,
            to - from,
            toInstance - fromInstance,
            fromInstance
        )
    }

    override fun dispose() {
        if (disposed) return
        vboMap.forEach { it.value.vbo.dispose() }
        glDeleteVertexArrays(vaoHandle)
        disposed = true
    }

    @Suppress("ProtectedInFinal", "Unused")
    protected fun finalize() {
        dispose()
    }

    /**
     * Companion to store vao stack (useful to restore the context of the previous one)
     */
    private companion object {
        private var stack = ArrayDeque<Int>()
    }

}