package me.wieku.danser.graphics.drawables

import me.wieku.framework.graphics.buffers.VertexArrayObject
import me.wieku.framework.graphics.buffers.VertexAttribute
import me.wieku.framework.graphics.buffers.VertexAttributeType
import me.wieku.framework.graphics.drawables.Drawable
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.graphics.shaders.Shader
import me.wieku.framework.graphics.textures.store.TextureStore
import me.wieku.framework.input.InputManager
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import me.wieku.framework.utils.synchronized
import org.joml.Vector2f
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.lwjgl.opengl.GL33
import org.lwjgl.system.MemoryUtil
import java.util.*
import kotlin.math.floor

class CursorWithTrail : Drawable(), KoinComponent {

    private val trailDensity = 1f
    private val trailRemoveSpeed = 1f
    private val trailMaxLength = 2000

    private val inputManager: InputManager by inject()
    private val textureStore: TextureStore by inject()

    private val points = ArrayList<Vector2f>((trailMaxLength * trailDensity).toInt())

    private val currentPosition = Vector2f()
    private val lastPosition = Vector2f()

    private var removeCounter = 0f

    private val tempCursor = Sprite("cursor/cursor.png") {}
    private val tempCursorTop = Sprite("cursor/cursor-top.png") {}

    private val cursorShader: Shader
    private val cursorVAO = VertexArrayObject()

    private val helperBuffer = MemoryUtil.memAllocFloat(16)

    private var dirty = false
    private val pointsRaw = MemoryUtil.memAllocFloat((trailMaxLength * trailDensity).toInt() * 2)
    private var pointsNum = 0

    init {
        cursorVAO.addVBO(
            "default", 6, 0, arrayOf(
                VertexAttribute("in_position", VertexAttributeType.Vec3, 0),
                VertexAttribute("in_tex_coord", VertexAttributeType.Vec2, 1)
            )
        )

        cursorVAO.setData(
            "default",
            floatArrayOf(
                -0.5f, -0.5f, 0f, 0f, 0f,
                0.5f, -0.5f, 0f, 1f, 0f,
                0.5f, 0.5f, 0f, 1f, 1f,
                0.5f, 0.5f, 0f, 1f, 1f,
                -0.5f, 0.5f, 0f, 0f, 1f,
                -0.5f, -0.5f, 0f, 0f, 0f
            )
        )

        cursorVAO.addVBO(
            "points", (trailMaxLength / trailDensity).toInt(), 1, arrayOf(
                VertexAttribute("in_mid", VertexAttributeType.Vec2, 0)
            )
        )

        cursorShader = Shader(
            FileHandle("assets/shaders/cursortrail.vsh", FileType.Classpath),
            FileHandle("assets/shaders/cursortrail.fsh", FileType.Classpath)
        )

        cursorVAO.bind()
        cursorVAO.bindToShader(cursorShader)
        cursorVAO.unbind()
    }

    override fun update() {
        super.update()

        var dirtyLocal = false

        currentPosition.set(inputManager.getPositionF())

        val distance = currentPosition.distance(lastPosition)

        val density = 1f / trailDensity

        if ((distance / density).toInt() > 0) {

            val lPos = Vector2f(lastPosition)

            var part = density

            while (part < distance) {

                lPos.set(currentPosition).sub(lastPosition).mul(part / distance).add(lastPosition)

                points.add(Vector2f(lPos))

                part += density
            }
            dirtyLocal = true
            lastPosition.set(lPos)
        }

        if (points.isNotEmpty()) {
            removeCounter += (points.size.toFloat() + 3) / (360f / clock.time.frameTime) * trailRemoveSpeed
            val times = floor(removeCounter).toInt()
            val lengthAdjusted = (trailMaxLength / density).toInt()

            if (points.size > lengthAdjusted) {
                points.subList(0, points.size - lengthAdjusted).clear()
                removeCounter = 0f
                dirtyLocal = true
            } else if (times > 0) {
                if (times < points.size) {
                    points.subList(0, times).clear()
                    removeCounter -= times.toFloat()
                } else {
                    points.clear()
                    removeCounter = 0f
                }

                dirtyLocal = true
            }

        }

        if (dirtyLocal) {
            pointsRaw.synchronized {
                pointsRaw.clear()

                points.forEach {
                    pointsRaw.put(it.x)
                    pointsRaw.put(it.y)
                }

                dirty = true
            }
        }

    }

    override fun draw(batch: SpriteBatch) {
        batch.end()
        GL33.glEnable(GL33.GL_BLEND)
        GL33.glBlendFunc(GL33.GL_ONE, GL33.GL_ONE_MINUS_SRC_ALPHA)

        val texture = textureStore.getResourceOrLoad("cursor/cursortrail.png")
        texture.bind(0)
        cursorVAO.bind()

        pointsRaw.synchronized {
            if (dirty) {
                pointsRaw.flip()
                pointsNum = pointsRaw.limit() / 2
                cursorVAO.setData("points", pointsRaw)
                dirty = false
            }
        }

        cursorShader.bind()
        helperBuffer.clear()
        cursorShader.setUniform("proj", batch.camera.projectionView.get(helperBuffer))
        cursorShader.setUniform("tex", 0f)
        cursorShader.setUniform("col_tint", 0.8f, 0f, 0.8f, 1f)
        cursorShader.setUniform("points", pointsNum.toFloat())
        cursorShader.setUniform("scale", 30f)
        cursorShader.setUniform("endScale", 0.4f)

        cursorVAO.draw(
            toInstance = pointsNum
        )

        cursorShader.unbind()
        cursorVAO.unbind()

        batch.begin()
        tempCursor.drawPosition.set(currentPosition).sub(15f, 15f)
        tempCursor.drawSize.set(30f)
        tempCursor.drawOrigin.set(15f)
        tempCursor.drawColor.set(0.8f, 0f, 0.8f, 1f)
        tempCursor.draw(batch)

        tempCursorTop.drawPosition.set(currentPosition).sub(15f, 15f)
        tempCursorTop.drawSize.set(30f)
        tempCursorTop.drawOrigin.set(15f)
        tempCursorTop.draw(batch)
    }

    override fun dispose() {
        MemoryUtil.memFree(pointsRaw)
    }
}