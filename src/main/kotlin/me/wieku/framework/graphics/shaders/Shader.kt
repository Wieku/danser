package me.wieku.framework.graphics.shaders

import me.wieku.framework.graphics.buffers.VertexAttribute
import me.wieku.framework.graphics.buffers.VertexAttributeType
import me.wieku.framework.resource.FileHandle
import org.lwjgl.opengl.GL33.*
import java.nio.FloatBuffer

class Shader(private val vertex: String, private val fragment: String) {
    var shaderId: Int = 0
        private set

    private var vertexHandle: Int = 0
    private var fragmentHandle: Int = 0

    var attributes: HashMap<String, VertexAttribute>
        private set

    private var uniforms: HashMap<String, VertexAttribute>

    private var bound = false

    constructor(vertex: FileHandle, fragment: FileHandle) : this(vertex.asString(), fragment.asString())

    init {
        val resultV = ShaderHelper.loadShader(ShaderType.Vertex, vertex)
        if (!resultV.successful) {
            throw RuntimeException("Failed to compile vertex shader:\n" + resultV.log)
        }

        val resultH = ShaderHelper.loadShader(ShaderType.Fragment, fragment)
        if (!resultH.successful) {
            throw RuntimeException("Failed to compile fragment shader:\n" + resultH.log)
        }

        vertexHandle = resultV.glId
        fragmentHandle = resultH.glId

        shaderId = ShaderHelper.createProgram()

        if (shaderId == 0) {
            throw RuntimeException("Failed to create shaderId")
        }

        val linkResult = ShaderHelper.linkShader(shaderId, vertexHandle, fragmentHandle)

        if (!linkResult.successful) {
            throw RuntimeException("Failed to link shaders:\n" + linkResult.log)
        }

        attributes = ShaderHelper.getAttributesLocations(shaderId)
        uniforms = ShaderHelper.getUniformsLocations(shaderId)
    }

    fun bind() {
        if (bound) {
            throw IllegalStateException("Shader is already bound")
        }

        glUseProgram(shaderId)
        bound = true
    }

    fun unbind() {
        if (!bound) {
            throw IllegalStateException("Shader is already not bound")
        }

        glUseProgram(0)
        bound = false
    }

    fun dispose() {
        ShaderHelper.removeProgram(shaderId)
    }

    fun setUniform(name: String, vararg values: Float) {
        if (!bound) {
            throw IllegalStateException("Shader is not bound")
        }

        when (uniforms[name]!!.attributeType) {
            VertexAttributeType.GlInt -> glUniform1i(uniforms[name]!!.location, values[0].toInt())
            VertexAttributeType.GlFloat -> glUniform1f(uniforms[name]!!.location, values[0])
            VertexAttributeType.Vec2 -> glUniform2fv(uniforms[name]!!.location, values)
            VertexAttributeType.Vec3 -> glUniform3fv(uniforms[name]!!.location, values)
            VertexAttributeType.Vec4 -> glUniform4fv(uniforms[name]!!.location, values)
            VertexAttributeType.Mat2 -> glUniformMatrix2fv(uniforms[name]!!.location, false, values)
            VertexAttributeType.Mat23 -> glUniformMatrix2x3fv(uniforms[name]!!.location, false, values)
            VertexAttributeType.Mat24 -> glUniformMatrix2x4fv(uniforms[name]!!.location, false, values)
            VertexAttributeType.Mat3 -> glUniformMatrix3fv(uniforms[name]!!.location, false, values)
            VertexAttributeType.Mat32 -> glUniformMatrix3x2fv(uniforms[name]!!.location, false, values)
            VertexAttributeType.Mat34 -> glUniformMatrix3x4fv(uniforms[name]!!.location, false, values)
            VertexAttributeType.Mat4 -> glUniformMatrix4fv(uniforms[name]!!.location, false, values)
            VertexAttributeType.Mat42 -> glUniformMatrix4x2fv(uniforms[name]!!.location, false, values)
            VertexAttributeType.Mat43 -> glUniformMatrix4x3fv(uniforms[name]!!.location, false, values)
        }
    }

    fun setUniform(name: String, values: FloatBuffer) {
        if (!bound) {
            throw IllegalStateException("Shader is not bound")
        }

        when (uniforms[name]!!.attributeType) {
            VertexAttributeType.GlInt -> glUniform1i(uniforms[name]!!.location, values[0].toInt())
            VertexAttributeType.GlFloat -> glUniform1fv(uniforms[name]!!.location, values)
            VertexAttributeType.Vec2 -> glUniform2fv(uniforms[name]!!.location, values)
            VertexAttributeType.Vec3 -> glUniform3fv(uniforms[name]!!.location, values)
            VertexAttributeType.Vec4 -> glUniform4fv(uniforms[name]!!.location, values)
            VertexAttributeType.Mat2 -> glUniformMatrix2fv(uniforms[name]!!.location, false, values)
            VertexAttributeType.Mat23 -> glUniformMatrix2x3fv(uniforms[name]!!.location, false, values)
            VertexAttributeType.Mat24 -> glUniformMatrix2x4fv(uniforms[name]!!.location, false, values)
            VertexAttributeType.Mat3 -> glUniformMatrix3fv(uniforms[name]!!.location, false, values)
            VertexAttributeType.Mat32 -> glUniformMatrix3x2fv(uniforms[name]!!.location, false, values)
            VertexAttributeType.Mat34 -> glUniformMatrix3x4fv(uniforms[name]!!.location, false, values)
            VertexAttributeType.Mat4 -> glUniformMatrix4fv(uniforms[name]!!.location, false, values)
            VertexAttributeType.Mat42 -> glUniformMatrix4x2fv(uniforms[name]!!.location, false, values)
            VertexAttributeType.Mat43 -> glUniformMatrix4x3fv(uniforms[name]!!.location, false, values)
        }
    }

}