package me.wieku.danser.graphics.drawables

import me.wieku.framework.animation.Glider
import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.graphics.buffers.VertexArrayObject
import me.wieku.framework.graphics.buffers.VertexAttribute
import me.wieku.framework.graphics.buffers.VertexAttributeType
import me.wieku.framework.graphics.drawables.containers.Container
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.graphics.helpers.blend.BlendEquation
import me.wieku.framework.graphics.helpers.blend.BlendFactor
import me.wieku.framework.graphics.helpers.blend.BlendHelper
import me.wieku.framework.graphics.shaders.Shader
import me.wieku.framework.graphics.textures.store.TextureStore
import me.wieku.framework.input.InputManager
import me.wieku.framework.input.MouseButton
import me.wieku.framework.input.event.MouseDownEvent
import me.wieku.framework.input.event.MouseUpEvent
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import me.wieku.framework.utils.synchronized
import org.joml.Vector2f
import org.joml.Vector4f
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.lwjgl.system.MemoryUtil
import java.util.*
import kotlin.math.floor

class CursorWithTrail : Container(), KoinComponent {

    //region TODO: Move those values to settings

    private val trailDensity = 1f
    private val trailRemoveSpeed = 1f
    private val trailMaxLength = 2000
    private val trailColor1 = Vector4f(0f, 0f, 0.8f, 1f)
    private val trailColor2 = Vector4f(0f, 1f, 1f, 1f)
    private val cursorSize = 30f
    private val innerSizeMult = 0.75f
    private val innerTrailMult = 0.9f
    private val trailEndScale = 0.1f

    //endregion

    private val inputManager: InputManager by inject()
    private val textureStore: TextureStore by inject()

    private val points = ArrayList<Vector2f>((trailMaxLength * trailDensity).toInt())

    private val currentPosition = Vector2f()
    private val lastPosition = Vector2f(Float.NaN)

    private var removeCounter = 0f

    private val tempCursor = Sprite("cursor/cursortrail.png") {}
    private val tempCursorTop = Sprite("cursor/cursor-top.png") {}

    private val cursorShader: Shader
    private val cursorVAO = VertexArrayObject()

    private var dirty = false
    private val pointsRaw = MemoryUtil.memAllocFloat((trailMaxLength * trailDensity).toInt() * 2 * 2)
    private val attribsRaw = MemoryUtil.memAllocFloat(12)
    private var pointsNum = 0

    private val cursorInflate = Glider(1f)

    init {

        fillMode = Scaling.Stretch

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
            "points", (trailMaxLength / trailDensity).toInt() * 2, 1, arrayOf(
                VertexAttribute("in_mid", VertexAttributeType.Vec2, 0)
            )
        )

        cursorVAO.addVBO(
            "attribs", 12, 1, arrayOf(
                VertexAttribute("in_color", VertexAttributeType.Vec4, 0),
                VertexAttribute("in_scale", VertexAttributeType.GlFloat, 0),
                VertexAttribute("in_lengthScale", VertexAttributeType.GlFloat, 0)
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

        val before = cursorInflate.value

        cursorInflate.update(clock.currentTime)

        var dirtyLocal = cursorInflate.value != before

        currentPosition.set(inputManager.getPositionF())

        if (lastPosition.x.isNaN()) lastPosition.set(currentPosition)

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

                points.forEach {
                    pointsRaw.put(it.x)
                    pointsRaw.put(it.y)
                }

                attribsRaw.clear()

                attribsRaw.put(trailColor1.x)
                attribsRaw.put(trailColor1.y)
                attribsRaw.put(trailColor1.z)
                attribsRaw.put(trailColor1.w)
                attribsRaw.put(cursorSize * cursorInflate.value)
                attribsRaw.put(1f)

                attribsRaw.put(trailColor2.x)
                attribsRaw.put(trailColor2.y)
                attribsRaw.put(trailColor2.z)
                attribsRaw.put(trailColor2.w)
                attribsRaw.put(cursorSize * innerSizeMult * cursorInflate.value)
                attribsRaw.put(innerTrailMult)

                dirty = true
            }
        }

    }

    override fun draw(batch: SpriteBatch) {
        super.draw(batch)

        val inflated = cursorSize * cursorInflate.value

        var tempOv = inflated

        tempCursor.drawPosition.set(currentPosition).sub(tempOv / 2, tempOv / 2)
        tempCursor.drawSize.set(tempOv)
        tempCursor.drawOrigin.set(tempOv / 2)
        tempCursor.drawColor.set(trailColor1)
        tempCursor.draw(batch)

        tempOv *= innerSizeMult

        tempCursor.drawPosition.set(currentPosition).sub(tempOv / 2, tempOv / 2)
        tempCursor.drawSize.set(tempOv)
        tempCursor.drawOrigin.set(tempOv / 2)
        tempCursor.drawColor.set(trailColor2)
        tempCursor.draw(batch)

        batch.end()

        BlendHelper.pushBlend()
        BlendHelper.enable()
        BlendHelper.setEquation(BlendEquation.Add)
        BlendHelper.setFunction(
            BlendFactor.SrcAlpha,
            BlendFactor.OneMinusSrcAlpha,
            BlendFactor.One,
            BlendFactor.OneMinusSrcAlpha
        )

        val texture = textureStore.getResourceOrLoad("cursor/cursortrail.png")
        texture.bind(0)
        cursorVAO.bind()

        pointsRaw.synchronized {
            if (dirty) {
                pointsRaw.flip()
                cursorVAO.setData("points", pointsRaw)

                pointsNum = pointsRaw.limit() / 4

                attribsRaw.flip()
                cursorVAO.setData("attribs", attribsRaw)

                if (pointsNum > 0)
                    cursorVAO.changeVBODivisor("attribs", pointsNum)

                dirty = false
            }
        }

        cursorShader.bind()
        cursorShader.setUniformMatrix4("projView", batch.camera.projectionView)
        cursorShader.setUniform1i("texture", 0)
        cursorShader.setUniform1i("points", pointsNum)
        cursorShader.setUniform1f("endScale", trailEndScale)

        cursorVAO.draw(
            toInstance = pointsNum * 2
        )

        cursorShader.unbind()
        cursorVAO.unbind()

        BlendHelper.popBlend()

        batch.begin()

        tempOv = inflated

        tempCursorTop.drawPosition.set(currentPosition).sub(tempOv / 2, tempOv / 2)
        tempCursorTop.drawSize.set(tempOv)
        tempCursorTop.drawOrigin.set(tempOv / 2)
        tempCursorTop.draw(batch)
    }

    override fun onMouseDown(e: MouseDownEvent): Boolean {
        if (e.button == MouseButton.ButtonLeft || e.button == MouseButton.ButtonRight) {
            cursorInflate.addEvent(clock.currentTime + 100, 1.3f)
            addChild(
                Sprite("cursor/ripple.png") {
                    fillMode = Scaling.Fit
                    scale = Vector2f(0f)

                    anchor = Origin.Custom
                    customAnchor.set(e.cursorPosition.x.toFloat(), e.cursorPosition.y.toFloat())
                        .sub(this@CursorWithTrail.drawPosition)
                        .mul(1f / this@CursorWithTrail.drawSize.x, 1f / this@CursorWithTrail.drawSize.y)

                    drawForever = false

                    addTransform(Transform(TransformType.Fade, clock.currentTime, clock.currentTime + 500, 0.5f, 0f))
                    addTransform(Transform(TransformType.Scale, clock.currentTime, clock.currentTime + 500, 0f, 0.15f))
                }
            )
        }

        return super.onMouseDown(e)
    }

    override fun onMouseUp(e: MouseUpEvent): Boolean {
        cursorInflate.addEvent(clock.currentTime + 100, 1f)
        return super.onMouseUp(e)
    }

    override fun dispose() {
        MemoryUtil.memFree(pointsRaw)
        MemoryUtil.memFree(attribsRaw)
    }
}