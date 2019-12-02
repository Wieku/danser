package me.wieku.framework.graphics.effects

import me.wieku.framework.graphics.buffers.Framebuffer
import me.wieku.framework.graphics.buffers.VertexArrayObject
import me.wieku.framework.graphics.buffers.VertexAttribute
import me.wieku.framework.graphics.buffers.VertexAttributeType
import me.wieku.framework.graphics.shaders.Shader
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import org.joml.Vector2f
import org.joml.Vector2i
import org.joml.Vector4f
import org.lwjgl.opengl.GL33.*
import kotlin.math.exp

class BlurEffect(var width: Int, var height: Int) {

    private var blurShader: Shader = Shader(FileHandle("frameworkAssets/fbopass.vsh", FileType.Classpath), FileHandle("frameworkAssets/blur.fsh", FileType.Classpath))

    private var fbo1: Framebuffer
    private var fbo2: Framebuffer

    private var kernelSize = Vector2f()
    private var sigma = Vector2f()
    private var size = Vector2i()
    
    var vao = VertexArrayObject()

    private var previousViewport = intArrayOf(0, 0, 0, 0)

    init {

        var attributes = arrayOf(
            VertexAttribute("in_position", VertexAttributeType.Vec3, 0),
            VertexAttribute("in_tex_coord", VertexAttributeType.Vec2, 1)
        )

        vao.addVBO("default", 12, 0, attributes)

        vao.setData("default", floatArrayOf(
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
        )) //we need two quads, one is flipping fbo texture

        vao.bind()
        vao.bindToShader(blurShader)
        vao.unbind()

        fbo1 = Framebuffer(width, height)
        fbo2 = Framebuffer(width, height)
        size = Vector2i(width, height)
        setBlur(0f, 0f)
    }

    fun begin() {
        fbo1.bind(true, Vector4f(0f, 0f, 0f, 0f))
        glGetIntegerv(GL_VIEWPORT, previousViewport)
        glViewport(0, 0, size.x, size.y)
    }

    fun resize(width:Int, height:Int) {
        fbo1.dispose()
        fbo1 = Framebuffer(width, height)

        fbo2.dispose()
        fbo2 = Framebuffer(width, height)

        size.set(width, height)
    }

    fun endAndProcess(): Texture {
        fbo1.unbind()

        blurShader.bind()
        blurShader.setUniform("tex", 0f)
        blurShader.setUniform("kernelSize", kernelSize.x, kernelSize.y)
        blurShader.setUniform("direction", 1f, 0f)
        blurShader.setUniform("sigma", sigma.x, sigma.y)
        blurShader.setUniform("size", size.x.toFloat(), size.y.toFloat())

        vao.bind()

        fbo2.bind(clearColor = Vector4f(0f))

        fbo1.getTexture()!!.bind(0)

        vao.draw(to = 6)

        fbo2.unbind()

        fbo1.bind(clearColor = Vector4f(0f))

        fbo2.getTexture()!!.bind(0)

        blurShader.setUniform("direction", 0f, 1f)
        vao.draw(from = 6)

        fbo1.unbind()

        vao.unbind()
        blurShader.unbind()
        glViewport(previousViewport[0], previousViewport[1], previousViewport[2], previousViewport[3])
        return fbo1.getTexture()!!
    }

    fun setBlur(blurX: Float, blurY: Float) {
        var sigmaX = blurX*25
        var sigmaY = blurY*25

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
        return (factor * exp(-0.5f*(x*x)/(sigma*sigma))) / sigma
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