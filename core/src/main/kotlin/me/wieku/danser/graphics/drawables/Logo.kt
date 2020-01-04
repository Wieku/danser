package me.wieku.danser.graphics.drawables

import me.wieku.framework.animation.Glider
import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.graphics.buffers.IndexBufferObject
import me.wieku.framework.graphics.buffers.VertexArrayObject
import me.wieku.framework.graphics.buffers.VertexAttribute
import me.wieku.framework.graphics.buffers.VertexAttributeType
import me.wieku.framework.graphics.drawables.containers.Container
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.graphics.shaders.Shader
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.graphics.textures.store.TextureStore
import me.wieku.framework.input.MouseButton
import me.wieku.framework.input.event.MouseDownEvent
import me.wieku.framework.input.event.MouseUpEvent
import me.wieku.framework.math.*
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import org.joml.Vector2f
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.lwjgl.opengl.GL33
import org.lwjgl.system.MemoryUtil

class Logo : Container(), KoinComponent {
    
    private val textureStore: TextureStore by inject()

    private val logoShader: Shader
    private val logoVAO = VertexArrayObject()
    private val logoIBO = IndexBufferObject(6)

    private val helperBuffer = MemoryUtil.memAllocFloat(16)
    
    private val quadRaw = MemoryUtil.memAllocFloat(6*5)
    
    private val animation = Glider(1f)
    private val highlight = Glider(1f)

    private var lastAnimated = -2000f
    
    private var texture = textureStore.getResourceOrLoad("misc/logo-test.png")

    init {

        fillMode = Scaling.Stretch

        logoVAO.addVBO(
            "default", 4, 0, arrayOf(
                VertexAttribute("in_position", VertexAttributeType.Vec3, 0),
                VertexAttribute("in_tex_coord", VertexAttributeType.Vec2, 1)
            )
        )

        logoShader = Shader(
            FileHandle("assets/shaders/logo.vsh", FileType.Classpath),
            FileHandle("assets/shaders/logo.fsh", FileType.Classpath)
        )

        logoVAO.bind()
        logoVAO.bindToShader(logoShader)
        logoVAO.unbind()

        logoIBO.bind()
        logoIBO.setData(shortArrayOf(0, 1, 2, 2, 3, 0))
        logoIBO.unbind()

    }

    override fun update() {
        super.update()

        if (clock.currentTime - lastAnimated > 2000f) {
            highlight.addEvent(clock.currentTime, clock.currentTime+900f, 0f, 1f, Easing.OutQuint)
            animation.addEvent(clock.currentTime, clock.currentTime+1000f, 0f, 1f, Easing.OutQuad)
            lastAnimated = clock.currentTime
        }
        
        animation.update(clock.currentTime)
        highlight.update(clock.currentTime)
    }

    private fun addVertex(position: Vector2f, texCoords: Vector2f) {
        quadRaw.put(position.x)
        quadRaw.put(position.y)
        quadRaw.put(0f)
        quadRaw.put(texCoords.x)
        quadRaw.put(texCoords.y)
    }

    private var tmp = Vector2f()
    private var tmp1 = Vector2f()
    private var tmp2 = Vector2f()
    
    fun genQuad() {

        quadRaw.clear()

        val region = texture.region
        
        tmp2.set(drawPosition).add(drawOrigin)

        val u1 = if (flipY) region.U2 else region.U1
        val u2 = if (flipY) region.U1 else region.U2
        val v1 = if (flipX) region.V2 else region.V1
        val v2 = if (flipX) region.V1 else region.V2

        var halfShearX = 0f
        var halfShearY = 0f

        if (shearX != 0f) {
            halfShearX = vector2fRad(Math.PI.toFloat() / 2 * (1 - shearX), drawSize.y / 2).x / drawSize.x / 2
        }

        if (shearY != 0f) {
            halfShearY = vector2fRad(shearY * Math.PI.toFloat() / 2, drawSize.x / 2).y / drawSize.y / 2
        }

        tmp.set(0f + halfShearX, 0f + halfShearY).mul(drawSize).sub(drawOrigin)
            .rot(rotation).add(tmp2)
        tmp1.set(u1, v1)
        addVertex(tmp, tmp1)

        tmp.set(1f + halfShearX, 0f - halfShearY).mul(drawSize).sub(drawOrigin)
            .rot(rotation).add(tmp2)
        tmp1.set(u2, v1)
        addVertex(tmp, tmp1)

        tmp.set(1f - halfShearX, 1f - halfShearY).mul(drawSize).sub(drawOrigin)
            .rot(rotation).add(tmp2)
        tmp1.set(u2, v2)
        addVertex(tmp, tmp1)

        tmp.set(0f - halfShearX, 1f + halfShearY).mul(drawSize).sub(drawOrigin)
            .rot(rotation).add(tmp2)
        tmp1.set(u1, v2)
        addVertex(tmp, tmp1)
    }
    
    override fun draw(batch: SpriteBatch) {
        super.draw(batch)

        batch.end()
        
        GL33.glEnable(GL33.GL_BLEND)
        GL33.glBlendEquation(GL33.GL_ADD)
        GL33.glBlendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA)

        texture.bind(0)

        logoVAO.bind()

        genQuad()
        quadRaw.flip()
        logoVAO.setData("default", quadRaw)

        logoShader.bind()
        helperBuffer.clear()
        logoShader.setUniform("proj", batch.camera.projectionView.get(helperBuffer))
        logoShader.setUniform("tex", 0f)
        logoShader.setUniform("animation", animation.value)
        logoShader.setUniform("highlight", highlight.value)

        logoIBO.bind()
        logoIBO.draw()
        logoIBO.unbind()

        logoShader.unbind()
        logoVAO.unbind()

        batch.begin()
    }

    override fun dispose() {
        MemoryUtil.memFree(quadRaw)
    }
}