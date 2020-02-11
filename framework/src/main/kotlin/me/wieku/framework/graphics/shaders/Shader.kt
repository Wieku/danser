package me.wieku.framework.graphics.shaders

import me.wieku.framework.graphics.buffers.VertexAttribute
import me.wieku.framework.graphics.buffers.VertexAttributeType
import me.wieku.framework.resource.FileHandle
import org.joml.*
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer
import java.util.*

class Shader(vertex: String, fragment: String) {
    var shaderId: Int = 0
        private set

    private var vertexHandle: Int = 0
    private var fragmentHandle: Int = 0

    var attributes: HashMap<String, VertexAttribute>
        private set

    private var uniforms: HashMap<String, VertexAttribute>

    private var isBound = false

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
        check(!isBound) {
            "Shader is already bound"
        }

        stack.push(glGetInteger(GL_CURRENT_PROGRAM))
        glUseProgram(shaderId)
        isBound = true
    }

    fun unbind() {
        check(isBound) {
            "Shader is already not bound"
        }

        val binding = stack.pop()
        glUseProgram(binding ?: 0)
        isBound = false
    }

    fun dispose() {
        ShaderHelper.removeProgram(shaderId)
    }

    private fun getUniform(name: String, expectedType: VertexAttributeType): VertexAttribute {
        val uniform = uniforms[name] ?: throw IllegalArgumentException("Uniform does not exist")

        require(uniform.attributeType == expectedType) {
            "Wrong attribute type. Required :${uniform.attributeType}, given: $expectedType"
        }

        return uniform
    }

    fun setUniform1i(name: String, value: Int) {
        check(isBound) {
            "Shader is not bound"
        }

        val uniform = getUniform(name, VertexAttributeType.GlInt)
        glUniform1i(uniform.location, value)
    }

    fun setUniform1f(name: String, value: Float) {
        check(isBound) {
            "Shader is not bound"
        }

        val uniform = getUniform(name, VertexAttributeType.GlFloat)
        glUniform1f(uniform.location, value)
    }

    //region Vector uniforms

    fun setUniform2f(name: String, value: Vector2f) {
        check(isBound) {
            "Shader is not bound"
        }

        val uniform = getUniform(name, VertexAttributeType.Vec2)
        glUniform2f(uniform.location, value.x, value.y)
    }

    fun setUniform3f(name: String, value: Vector3f) {
        check(isBound) {
            "Shader is not bound"
        }

        val uniform = getUniform(name, VertexAttributeType.Vec3)
        glUniform3f(uniform.location, value.x, value.y, value.z)
    }

    fun setUniform4f(name: String, value: Vector4f) {
        check(isBound) {
            "Shader is not bound"
        }

        val uniform = getUniform(name, VertexAttributeType.Vec4)
        glUniform4f(uniform.location, value.x, value.y, value.z, value.w)
    }

    //endregion

    //region Matrix uniforms

    fun setUniformMatrix3(name: String, value: Matrix3f, transposed: Boolean = false) {
        check(isBound) {
            "Shader is not bound"
        }

        val uniform = getUniform(name, VertexAttributeType.Mat3)
        glUniformMatrix3fv(uniform.location, transposed, value.get(helperBuffer))
    }

    fun setUniformMatrix3x2(name: String, value: Matrix3x2f, transposed: Boolean = false) {
        check(isBound) {
            "Shader is not bound"
        }

        val uniform = getUniform(name, VertexAttributeType.Mat32)
        glUniformMatrix3x2fv(uniform.location, transposed, value.get(helperBuffer))
    }

    fun setUniformMatrix4(name: String, value: Matrix4f, transposed: Boolean = false) {
        check(isBound) {
            "Shader is not bound"
        }

        val uniform = getUniform(name, VertexAttributeType.Mat4)
        glUniformMatrix4fv(uniform.location, transposed, value.get(helperBuffer))
    }

    fun setUniformMatrix4x3(name: String, value: Matrix4x3f, transposed: Boolean = false) {
        check(isBound) {
            "Shader is not bound"
        }

        val uniform = getUniform(name, VertexAttributeType.Mat43)
        glUniformMatrix4x3fv(uniform.location, transposed, value.get(helperBuffer))
    }

    //endregion

    //region Universal uniform setters

    fun setUniform(name: String, vararg values: Float) {
        check(isBound) {
            "Shader is not bound"
        }

        require(values.isNotEmpty()) {
            "No values given"
        }

        val uniform = uniforms[name] ?: throw IllegalArgumentException("Uniform does not exist")

        when (uniform.attributeType) {
            VertexAttributeType.GlInt -> glUniform1i(uniform.location, values[0].toInt())
            VertexAttributeType.GlFloat -> glUniform1f(uniform.location, values[0])
            VertexAttributeType.Vec2 -> glUniform2fv(uniform.location, values)
            VertexAttributeType.Vec3 -> glUniform3fv(uniform.location, values)
            VertexAttributeType.Vec4 -> glUniform4fv(uniform.location, values)
            VertexAttributeType.Mat2 -> glUniformMatrix2fv(uniform.location, false, values)
            VertexAttributeType.Mat23 -> glUniformMatrix2x3fv(uniform.location, false, values)
            VertexAttributeType.Mat24 -> glUniformMatrix2x4fv(uniform.location, false, values)
            VertexAttributeType.Mat3 -> glUniformMatrix3fv(uniform.location, false, values)
            VertexAttributeType.Mat32 -> glUniformMatrix3x2fv(uniform.location, false, values)
            VertexAttributeType.Mat34 -> glUniformMatrix3x4fv(uniform.location, false, values)
            VertexAttributeType.Mat4 -> glUniformMatrix4fv(uniform.location, false, values)
            VertexAttributeType.Mat42 -> glUniformMatrix4x2fv(uniform.location, false, values)
            VertexAttributeType.Mat43 -> glUniformMatrix4x3fv(uniform.location, false, values)
        }
    }

    fun setUniform(name: String, values: FloatBuffer) {
        check(isBound) {
            "Shader is not bound"
        }

        require(values.limit() > 0) {
            "Buffer is empty"
        }

        val uniform = uniforms[name] ?: throw IllegalArgumentException("Uniform does not exist")

        when (uniform.attributeType) {
            VertexAttributeType.GlInt -> glUniform1i(uniform.location, values[0].toInt())
            VertexAttributeType.GlFloat -> glUniform1f(uniform.location, values[0])
            VertexAttributeType.Vec2 -> glUniform2fv(uniform.location, values)
            VertexAttributeType.Vec3 -> glUniform3fv(uniform.location, values)
            VertexAttributeType.Vec4 -> glUniform4fv(uniform.location, values)
            VertexAttributeType.Mat2 -> glUniformMatrix2fv(uniform.location, false, values)
            VertexAttributeType.Mat23 -> glUniformMatrix2x3fv(uniform.location, false, values)
            VertexAttributeType.Mat24 -> glUniformMatrix2x4fv(uniform.location, false, values)
            VertexAttributeType.Mat3 -> glUniformMatrix3fv(uniform.location, false, values)
            VertexAttributeType.Mat32 -> glUniformMatrix3x2fv(uniform.location, false, values)
            VertexAttributeType.Mat34 -> glUniformMatrix3x4fv(uniform.location, false, values)
            VertexAttributeType.Mat4 -> glUniformMatrix4fv(uniform.location, false, values)
            VertexAttributeType.Mat42 -> glUniformMatrix4x2fv(uniform.location, false, values)
            VertexAttributeType.Mat43 -> glUniformMatrix4x3fv(uniform.location, false, values)
        }
    }

    //endregion

    private companion object {
        private var stack = ArrayDeque<Int>()
        private val helperBuffer = MemoryUtil.memAllocFloat(16)
        get() {
            field.clear()
            return field
        }
    }

}