package me.wieku.framework.graphics.drawables.sprite

import me.wieku.framework.graphics.buffers.*
import me.wieku.framework.graphics.shaders.Shader
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.graphics.textures.TextureRegion
import me.wieku.framework.math.rot
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import me.wieku.framework.utils.Disposable
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer

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

    private var basePosition: Vector2f = Vector2f()
    private var scale: Vector2f = Vector2f()
    private var subscale: Vector2f = Vector2f()

    private var rotation: Float = 0f

    private var projectionView: Matrix4f = Matrix4f().ortho2D(-1f, 1f, -1f, 1f)
    private var helperBuffer: FloatBuffer

    private var color: Vector4f = Vector4f(1f, 1f, 1f, 1f)

    private var currentTexture: Texture? = null

    init {
        var location = "frameworkAssets/sprite"
        shader =
                Shader(FileHandle("$location.vsh", FileType.Classpath), FileHandle("$location.fsh", FileType.Classpath))

        var attributes = arrayOf(
            VertexAttribute("in_position", VertexAttributeType.Vec3, 0),
            VertexAttribute("in_tex_coord", VertexAttributeType.Vec3, 1),
            VertexAttribute("in_color", VertexAttributeType.Vec4, 2),
            VertexAttribute("in_additive", VertexAttributeType.GlFloat, 3)
        )

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
        shader.setUniform("proj", projectionView.get(helperBuffer))
        vao.bind()
        ibo.bind()


        preBlendState = GL11.glIsEnabled(GL_BLEND)
        preSFactor = glGetInteger(GL_BLEND_SRC_ALPHA)
        preDFactor = glGetInteger(GL_BLEND_SRC_ALPHA)

        if (!preBlendState)
            glEnable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)

        if (currentTexture != null) {
            shader.setUniform("tex", currentTexture!!.getLocation().toFloat())
        }
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

        val fX = if (sprite.flipX) -1f else 1f
        val fY = if (sprite.flipY) -1f else 1f

        tmp.set(0f, 0f).sub(sprite.origin).mul(sprite.scale)
            .mul(sprite.width * fX, sprite.height * fY).rot(sprite.rotation).add(sprite.position)
        tmp1.set(region.getU1(), region.getV1(), region.getLayer().toFloat())
        addVertex(tmp, tmp1, sprite.color, sprite.additive)

        tmp.set(1f, 0f).sub(sprite.origin).mul(sprite.scale)
            .mul(sprite.width * fX, sprite.height * fY).rot(sprite.rotation).add(sprite.position)
        tmp1.set(region.getU2(), region.getV1(), region.getLayer().toFloat())
        addVertex(tmp, tmp1, sprite.color, sprite.additive)

        tmp.set(1f, 1f).sub(sprite.origin).mul(sprite.scale)
            .mul(sprite.width * fX, sprite.height * fY).rot(sprite.rotation).add(sprite.position)
        tmp1.set(region.getU2(), region.getV2(), region.getLayer().toFloat())
        addVertex(tmp, tmp1, sprite.color, sprite.additive)

        tmp.set(0f, 1f).sub(sprite.origin).mul(sprite.scale)
            .mul(sprite.width * fX, sprite.height * fY).rot(sprite.rotation).add(sprite.position)
        tmp1.set(region.getU1(), region.getV2(), region.getLayer().toFloat())
        addVertex(tmp, tmp1, sprite.color, sprite.additive)
    }

    override fun dispose() {
        ibo.dispose()
        vao.dispose()
        shader.dispose()
        MemoryUtil.memFree(vertexBuffer)
        MemoryUtil.memFree(helperBuffer)
    }

}