package me.wieku.framework.graphics.drawables.sprite

import me.wieku.framework.graphics.buffers.*
import me.wieku.framework.graphics.shaders.Shader
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.graphics.textures.TextureRegion
import me.wieku.framework.math.rot
import me.wieku.framework.math.view.Camera
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import me.wieku.framework.utils.Disposable
import me.wieku.framework.utils.MaskingInfo
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer
import java.util.*

//TODO: support Int IBOs
/**
 * @property maxSprites - maximum batch size in sprites, which mean x4 vertices and x6 indices
 */
class SpriteBatch(private var maxSprites: Int = 2000) : Disposable {

    private var shader: Shader
    private var vertexSize: Int
    private var vao: VertexArrayObject
    private var ibo: IndexBufferObject
    private var vertexBuffer: FloatBuffer

    private var drawing = false
    private var vertexCount: Int = 0

    var camera: Camera = Camera()
        set(value) {
            if (drawing) {
                flush()
                helperBuffer.clear()
                shader.setUniform("proj", value.projectionView.get(helperBuffer))
            }
            field = value
        }

    private var helperBuffer: FloatBuffer

    private var color: Vector4f = Vector4f(1f, 1f, 1f, 1f)

    private var currentTexture: Texture? = null

    init {
        val location = "frameworkAssets/sprite"
        shader = Shader(
            FileHandle("$location.vsh", FileType.Classpath),
            FileHandle("$location.fsh", FileType.Classpath)
        )

        val attributes = arrayOf(
            VertexAttribute("in_position", VertexAttributeType.Vec3, 0),
            VertexAttribute("in_tex_coord", VertexAttributeType.Vec3, 1),
            VertexAttribute("in_color", VertexAttributeType.Vec4, 3),
            VertexAttribute("in_additive", VertexAttributeType.GlFloat, 4)
        )

        camera.setViewport(2, 2, false)
        camera.position = Vector2f(0f)
        camera.update()

        vertexSize = attributes.vertexSize() / 4

        vao = VertexArrayObject()
        vao.addVBO("default", maxSprites * 4, 0, attributes)

        vao.bind()
        vao.bindToShader(shader)
        vao.unbind()

        ibo = IndexBufferObject(maxSprites * 6)

        vertexBuffer = MemoryUtil.memAllocFloat(maxSprites * 4 * vertexSize)

        val indexBuffer = MemoryUtil.memAllocShort(maxSprites * 6)

        var j = 0
        for (i in 0 until maxSprites * 6 step 6) {
            indexBuffer.put(j.toShort())
            indexBuffer.put((j + 1).toShort())
            indexBuffer.put((j + 2).toShort())
            indexBuffer.put((j + 2).toShort())
            indexBuffer.put((j + 3).toShort())
            indexBuffer.put(j.toShort())
            j += 4
        }
        indexBuffer.flip()
        ibo.bind()
        ibo.setData(indexBuffer)
        ibo.unbind()

        MemoryUtil.memFree(indexBuffer)

        helperBuffer = MemoryUtil.memAllocFloat(16)
    }

    private var preBlendState: Boolean = false
    private var preSFactor: Int = 0
    private var preDFactor: Int = 0

    private fun bind(texture: Texture) {
        if (currentTexture != null) {
            if (currentTexture!!.getID() == texture.getID()) {
                return
            }

            flush()
        }

        //We are assuming that textures with location higher than 0 are already bound
        if (texture.getLocation() == 0) {
            texture.bind(0)
        }

        currentTexture = texture
        shader.setUniform("tex", currentTexture!!.getLocation().toFloat())
    }

    fun begin() {
        if (drawing) {
            throw IllegalStateException("Batching already began")
        }

        drawing = true

        shader.bind()
        helperBuffer.clear()
        shader.setUniform("proj", camera.projectionView.get(helperBuffer))
        vao.bind()
        ibo.bind()

        preBlendState = GL11.glIsEnabled(GL_BLEND)
        preSFactor = glGetInteger(GL_BLEND_SRC)
        preDFactor = glGetInteger(GL_BLEND_DST)

        if (!preBlendState)
            glEnable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)

        currentTexture?.let {
            if (it.getLocation() == 0) {
                it.bind(0)
            }
            shader.setUniform("tex", it.getLocation().toFloat())
        }
        applyMaskingInfo()
    }

    fun flush() {
        if (vertexCount == 0) {
            return
        }

        vertexBuffer.flip()
        vao.setData("default", vertexBuffer)

        ibo.draw(to = vertexCount / 4 * 6)

        vertexBuffer.clear()
        vertexCount = 0
    }

    fun reset() {
        vertexBuffer.clear()
        vertexCount = 0
    }

    fun end() {
        if (!drawing) {
            throw IllegalStateException("Batching already ended")
        }

        drawing = false

        flush()

        ibo.unbind()
        vao.unbind()

        shader.unbind()

        if (!preBlendState)
            glDisable(GL_BLEND)
        glBlendFunc(preSFactor, preDFactor)
    }

    private fun addVertex(position: Vector2f, texCoords: Vector3f, color: Vector4f, additive: Boolean = false) {

        vertexBuffer.put(position.x)
        vertexBuffer.put(position.y)
        vertexBuffer.put(0f)
        vertexBuffer.put(texCoords.x)
        vertexBuffer.put(texCoords.y)
        vertexBuffer.put(texCoords.z)
        vertexBuffer.put(color.x)
        vertexBuffer.put(color.y)
        vertexBuffer.put(color.z)
        vertexBuffer.put(color.w)
        vertexBuffer.put(if (additive) 0f else 1f)

        vertexCount += 1
    }

    private var tmp = Vector2f()
    private var tmp1 = Vector3f()
    private var tmp2 = Vector2f()

    fun draw(texture: Texture, x: Float, y: Float, scaleX: Float, scaleY: Float, color: Vector4f) {
        draw(texture.region, x, y, scaleX, scaleY, color)
    }

    //TODO: More draw options

    fun draw(region: TextureRegion, x: Float, y: Float, scaleX: Float, scaleY: Float, color: Vector4f) {
        if (!drawing) {
            throw IllegalStateException("Batching not started")
        }

        bind(region.getTexture())
        if (vertexCount / 4 >= maxSprites) {
            flush()
        }

        tmp.set(-1f, -1f).mul(scaleX, scaleY).mul(region.getWidth() / 2, region.getHeight() / 2).add(x, y)
        tmp1.set(region.getU1(), region.getV1(), region.getLayer().toFloat())
        addVertex(tmp, tmp1, color)

        tmp.set(1f, -1f).mul(scaleX, scaleY).mul(region.getWidth() / 2, region.getHeight() / 2).add(x, y)
        tmp1.set(region.getU2(), region.getV1(), region.getLayer().toFloat())
        addVertex(tmp, tmp1, color)

        tmp.set(1f, 1f).mul(scaleX, scaleY).mul(region.getWidth() / 2, region.getHeight() / 2).add(x, y)
        tmp1.set(region.getU2(), region.getV2(), region.getLayer().toFloat())
        addVertex(tmp, tmp1, color)

        tmp.set(-1f, 1f).mul(scaleX, scaleY).mul(region.getWidth() / 2, region.getHeight() / 2).add(x, y)
        tmp1.set(region.getU1(), region.getV2(), region.getLayer().toFloat())
        addVertex(tmp, tmp1, color)
    }

    fun draw(sprite: Sprite) {
        if (!drawing) {
            throw IllegalStateException("Batching not started")
        }

        val region = sprite.texture!!

        bind(region.getTexture())
        if (vertexCount / 4 >= maxSprites) {
            flush()
        }

        tmp2.set(sprite.drawPosition).add(sprite.drawOrigin)

        val u1 = if (sprite.flipY) region.getU2() else region.getU1()
        val u2 = if (sprite.flipY) region.getU1() else region.getU2()
        val v1 = if (sprite.flipX) region.getV2() else region.getV1()
        val v2 = if (sprite.flipX) region.getV1() else region.getV2()

        tmp.set(0f, 0f).mul(sprite.drawSize).sub(sprite.drawOrigin)
            .rot(sprite.rotation).add(tmp2)
        tmp1.set(u1, v1, region.getLayer().toFloat())
        addVertex(tmp, tmp1, sprite.drawColor, sprite.additive)

        tmp.set(1f, 0f).mul(sprite.drawSize).sub(sprite.drawOrigin)
            .rot(sprite.rotation).add(tmp2)
        tmp1.set(u2, v1, region.getLayer().toFloat())
        addVertex(tmp, tmp1, sprite.drawColor, sprite.additive)

        tmp.set(1f, 1f).mul(sprite.drawSize).sub(sprite.drawOrigin)
            .rot(sprite.rotation).add(tmp2)
        tmp1.set(u2, v2, region.getLayer().toFloat())
        addVertex(tmp, tmp1, sprite.drawColor, sprite.additive)

        tmp.set(0f, 1f).mul(sprite.drawSize).sub(sprite.drawOrigin)
            .rot(sprite.rotation).add(tmp2)
        tmp1.set(u1, v2, region.getLayer().toFloat())
        addVertex(tmp, tmp1, sprite.drawColor, sprite.additive)
    }

    private var maskingStack = ArrayDeque<MaskingInfo>()

    fun pushMaskingInfo(info: MaskingInfo) {
        maskingStack.push(info)
        applyMaskingInfo(info)
    }

    private fun applyMaskingInfo(info: MaskingInfo? = null) {
        flush()
        val inf: MaskingInfo? = info ?: maskingStack.peek()
        if (inf != null) {
            shader.setUniform("maskRect", inf.rect.x, inf.rect.y, inf.rect.z, inf.rect.w)
            shader.setUniform("g_CornerRadius", inf.radius)
        } else {
            shader.setUniform("maskRect", 0f, 0f, 1f, 1f)
            shader.setUniform("g_CornerRadius", 0f)
        }
    }

    fun popMaskingInfo() {
        maskingStack.pop()
        applyMaskingInfo()
    }

    override fun dispose() {
        ibo.dispose()
        vao.dispose()
        shader.dispose()
        MemoryUtil.memFree(vertexBuffer)
        MemoryUtil.memFree(helperBuffer)
    }

}