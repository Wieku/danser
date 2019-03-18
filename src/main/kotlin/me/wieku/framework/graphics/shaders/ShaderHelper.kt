package me.wieku.framework.graphics.shaders

import me.wieku.framework.graphics.buffers.VertexAttribute
import me.wieku.framework.graphics.buffers.VertexAttributeType
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil

enum class ShaderType(val glId: Int) {
    Vertex(GL_VERTEX_SHADER),
    Fragment(GL_FRAGMENT_SHADER),
    Geometry(GL_GEOMETRY_SHADER)
}

data class ShaderResult(val glId: Int, val successful: Boolean, val log: String)

object ShaderHelper {

    fun loadShader(type: ShaderType, source: String): ShaderResult {
        var shaderId = glCreateShader(type.glId)
        glShaderSource(shaderId, source)
        glCompileShader(shaderId)

        var status = glGetShaderi(shaderId, GL_COMPILE_STATUS)

        var log = ""

        if (status == 0) {
            log = glGetShaderInfoLog(shaderId)
        }

        return ShaderResult(shaderId, status == 1, log)
    }

    fun createProgram(): Int = glCreateProgram()
    fun removeProgram(program: Int) = glDeleteProgram(program)

    fun linkShader(program: Int, vararg shaders: Int): ShaderResult {
        for (shader in shaders) {
            glAttachShader(program, shader)
        }

        glLinkProgram(program)

        val status = glGetProgrami(program, GL_LINK_STATUS)

        var log = ""

        if (status == 0) {
            log = glGetProgramInfoLog(program)
        }

        return ShaderResult(program, status == 1, log)
    }

    fun getAttributesLocations(program: Int): HashMap<String, VertexAttribute> {
        val max = glGetProgrami(program, GL_ACTIVE_ATTRIBUTES)

        val attributes = HashMap<String, VertexAttribute>()

        val size = MemoryUtil.memAllocInt(1)
        val type = MemoryUtil.memAllocInt(1)

        for (i in 0 until max) {
            size.clear()
            type.clear()

            val name = glGetActiveAttrib(program, i, size, type)
            val location = glGetAttribLocation(program, name)
            attributes[name] = VertexAttribute(
                name,
                VertexAttributeType.getAttributeByGlType(type.get()),
                i,
                location
            )
        }

        MemoryUtil.memFree(size)
        MemoryUtil.memFree(type)

        return attributes
    }

    fun getUniformsLocations(program: Int): HashMap<String, VertexAttribute> {
        val max = glGetProgrami(program, GL_ACTIVE_UNIFORMS)

        val uniforms = HashMap<String, VertexAttribute>()

        val size = MemoryUtil.memAllocInt(1)
        val type = MemoryUtil.memAllocInt(1)

        for (i in 0 until max) {
            size.clear()
            size.put(0, 1)
            type.clear()

            val name = glGetActiveUniform(program, i, size, type)
            val location = glGetUniformLocation(program, name)
            uniforms[name] = VertexAttribute(
                name,
                VertexAttributeType.getAttributeByGlType(type.get()),
                i,
                location
            )
        }

        MemoryUtil.memFree(size)
        MemoryUtil.memFree(type)

        return uniforms
    }

}