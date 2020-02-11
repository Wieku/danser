package me.wieku.framework.graphics.effects

import me.wieku.framework.graphics.buffers.Framebuffer
import me.wieku.framework.graphics.buffers.VertexArrayObject
import me.wieku.framework.graphics.buffers.VertexAttribute
import me.wieku.framework.graphics.buffers.VertexAttributeType
import me.wieku.framework.graphics.helpers.ViewportHelper
import me.wieku.framework.graphics.helpers.blend.BlendHelper
import me.wieku.framework.graphics.shaders.Shader
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import org.joml.Vector2f
import org.joml.Vector4f
import kotlin.math.exp

class BlurEffect(__width: Int, __height: Int) {

    private lateinit var blurShader: Shader

    private lateinit var fbo1: Framebuffer
    private lateinit var fbo2: Framebuffer

    private var kernelSize = Vector2f()
    private var sigma = Vector2f()

    private var _width = __width
    private var _height = __height

    var width: Int
        get() = _width
        set(value) {
            resize(value, _height)
        }

    var height: Int
        get() = _height
        set(value) {
            resize(_width, value)
        }

    private lateinit var vao: VertexArrayObject

    private fun initialize() {
        blurShader = Shader(
            FileHandle("frameworkAssets/fbopass.vsh", FileType.Classpath),
            FileHandle("frameworkAssets/blur.fsh", FileType.Classpath)
        )

        var attributes = arrayOf(
            VertexAttribute("in_position", VertexAttributeType.Vec3, 0),
            VertexAttribute("in_tex_coord", VertexAttributeType.Vec2, 1)
        )

        vao = VertexArrayObject()

        vao.addVBO("default", 12, 0, attributes)

        vao.setData(
            "default",
            floatArrayOf(
                -1f, -1f, 0f, 0f, 0f,
                1f, -1f, 0f, 1f, 0f,
                1f, 1f, 0f, 1f, 1f,
                1f, 1f, 0f, 1f, 1f,
                -1f, 1f, 0f, 0f, 1f,
                -1f, -1f, 0f, 0f, 0f,
                -1f, -1f, 0f, 0f, 1f,
                1f, -1f, 0f, 1f, 1f,
                1f, 1f, 0f, 1f, 0f,
                1f, 1f, 0f, 1f, 0f,
                -1f, 1f, 0f, 0f, 0f,
                -1f, -1f, 0f, 0f, 1f
            )
        ) //we need two quads, one is flipping fbo texture

        vao.bind()
        vao.bindToShader(blurShader)
        vao.unbind()

        fbo1 = Framebuffer(_width, _height)
        fbo2 = Framebuffer(_width, _height)
    }

    fun begin() {
        if (!::blurShader.isInitialized) {
            initialize()
        }

        fbo1.bind(true, Vector4f(0f, 0f, 0f, 0f))
        ViewportHelper.pushViewport(_width, _height)
    }

    fun resize(width: Int, height: Int) {
        if (::fbo1.isInitialized) {
            fbo1.dispose()
            fbo1 = Framebuffer(width, height)

            fbo2.dispose()
            fbo2 = Framebuffer(width, height)
        }

        _width = width
        _height = height
    }

    fun endAndProcess(): Texture {
        fbo1.unbind()

        BlendHelper.pushBlend()
        BlendHelper.disable()

        blurShader.bind()
        blurShader.setUniform1i("tex", 0)
        blurShader.setUniform2f("kernelSize", kernelSize)
        blurShader.setUniform2f("direction", Vector2f(1f, 0f))
        blurShader.setUniform2f("sigma", sigma)
        blurShader.setUniform2f("size", Vector2f(_width.toFloat(), _height.toFloat()))

        vao.bind()

        fbo2.bind(clearColor = Vector4f(0f))

        fbo1.getTexture()!!.bind(0)

        vao.draw(to = 6)

        fbo2.unbind()

        fbo1.bind(clearColor = Vector4f(0f))

        fbo2.getTexture()!!.bind(0)

        blurShader.setUniform2f("direction", Vector2f(0f, 1f))
        vao.draw(from = 6)

        fbo1.unbind()

        vao.unbind()
        blurShader.unbind()

        BlendHelper.popBlend()

        ViewportHelper.popViewport()

        return fbo1.getTexture()!!
    }

    fun setBlur(blurX: Float, blurY: Float) {
        var sigmaX = blurX * 25
        var sigmaY = blurY * 25

        val kX = kernelSize(sigmaX)
        if (kX == 0) {
            sigmaX = 1.0f
        }

        val kY = kernelSize(sigmaY)
        if (kY == 0) {
            sigmaY = 1.0f
        }

        kernelSize = Vector2f(kX.toFloat(), kY.toFloat())
        sigma = Vector2f(sigmaX, sigmaY)
    }

    private fun gauss(x: Int, sigma: Float): Float {
        val factor = 0.398942f
        return factor * exp(-0.5f * (x * x) / (sigma * sigma)) / sigma
    }

    private fun kernelSize(sigma: Float): Int {
        if (sigma == 0f) {
            return 0
        }

        val baseG = gauss(0, sigma) * 0.1f
        val max = 200

        for (i in 1..max) {
            if (gauss(i, sigma) < baseG) {
                return i - 1
            }
        }
        return max
    }

}