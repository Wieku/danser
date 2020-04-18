package me.wieku.framework.graphics.drawables

import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.input.InputHandler
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import me.wieku.framework.math.color.Color
import me.wieku.framework.time.IFramedClock
import me.wieku.framework.utils.Disposable
import me.wieku.framework.utils.synchronized
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector2i
import org.joml.Vector4f
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min
import kotlin.math.tan

abstract class Drawable : InputHandler(), Disposable, KoinComponent {

    val clock: IFramedClock by inject()

    /**
     * Relative position of drawable
     */
    open var position = Vector2f()
    var fixedPosition = Vector2f()
    var drawPosition = Vector2f()
    private var tempPosition = Vector2f()

    var childOffset = Vector2f()

    var anchor = Origin.Centre
    var customAnchor = Vector2f()
    private val drawAnchor = Vector2f()

    var origin = Origin.Centre
    var customOrigin = Vector2f()
    var drawOrigin = Vector2f()
    private var tempOrigin = Vector2f()

    var inheritScale = true
    var scale = Vector2f(1f)
    var drawScale = Vector2f(1f)

    var fillMode = Scaling.None
    var size = Vector2f(1f)
    var drawSize = Vector2f()
    private var tempSize = Vector2f()

    var inheritColor = true
    var color = Color()
    var drawColor = Color()
    private var tempColor = Color()

    protected val transformInfo = Matrix4f()

    var flipX = false
    var flipY = false

    var rotation = 0f

    var additive = false

    var shearX = 0f
    var shearY = 0f

    var startTime = 0.0
    var endTime = 0.0
    var drawForever = true

    var pixelSnap = false

    var transforms = ArrayList<Transform>()

    /**
     * parent of this drawable
     */
    var parent: Drawable? = null

    var isValid = false
        private set

    open var wasUpdated = false

    open fun invalidate() {
        isValid = false
    }

    open fun update() {
        wasUpdated = false
        update(clock.currentTime)
        if (!isValid) {
            updateDrawable()
            isValid = true
            wasUpdated = true
        }
    }

    protected open fun updateDrawable() {
        drawScale.set(scale)
        tempColor.set(color)

        parent?.let { parent ->

            tempSize.set(fillMode.apply(size.x, size.y, parent.drawSize.x, parent.drawSize.y)).mul(drawScale)
            tempOrigin.set(if (origin == Origin.Custom) customOrigin else origin.offset).mul(tempSize)

            drawAnchor.set(0f)

            if (anchor != Origin.None) {
                drawAnchor.set(if (anchor == Origin.Custom) customAnchor else anchor.offset)
                drawAnchor.mul(parent.drawSize).add(parent.drawPosition).add(parent.childOffset)
            }

            tempPosition.set(position).sub(tempOrigin).add(drawAnchor)
            if (inheritColor) {
                tempColor.mul(parent.drawColor)
            }
        }

        if (parent == null) {
            tempSize.set(size).mul(drawScale)
            tempOrigin.set(if (origin == Origin.Custom) customOrigin else origin.offset).mul(tempSize)
            tempPosition.set(position).sub(tempOrigin)
        }

        drawPosition.set(tempPosition)
        drawOrigin.set(tempOrigin)
        drawSize.set(tempSize)
        drawColor.set(tempColor)

        if (pixelSnap) {
            drawPosition.floor()
            drawOrigin.ceil()
            drawSize.floor()
        }

        updateTransformInfo()
    }

    protected fun updateTransformInfo() {
        if (rotation != 0.0f || shearX != 0.0f || shearY != 0.0f) {
            val tempMatrix1 = popMatrix()
            val tempMatrix2 = popMatrix()
            val tempMatrix3 = popMatrix()

            tempMatrix1.identity().translate(drawPosition.x + drawOrigin.x, drawPosition.y + drawOrigin.y, 0.0f)
            tempMatrix3.identity().translate(-(drawPosition.x + drawOrigin.x), -(drawPosition.y + drawOrigin.y), 0.0f)

            tempMatrix2.identity()
            tempMatrix2.m10(1 / tan(PI.toFloat() / 2 * (1 - shearX)) / 2)
            tempMatrix2.m01(1 / tan(PI.toFloat() / 2 * (1 - shearY)) / 2)
            tempMatrix2.rotateZ(-rotation)

            transformInfo.set(tempMatrix1.mul(tempMatrix2).mul(tempMatrix3))

            pushMatrix(tempMatrix1)
            pushMatrix(tempMatrix2)
            pushMatrix(tempMatrix3)
        } else transformInfo.identity()
    }

    abstract fun draw(batch: SpriteBatch)

    fun canBeDeleted(): Boolean {
        return !drawForever && clock.currentTime >= endTime
    }

    private val tempVector4f = Vector4f()
    private fun toLocalSpace(position: Vector2i): Vector2f {
        tempVector4f.set(position.x.toFloat(), position.y.toFloat(), 0f, 1f)
        tempVector4f.mul(transformInfo)
        return Vector2f(tempVector4f.x, tempVector4f.y)
    }

    override fun isCursorIn(cursorPosition: Vector2i): Boolean {
        val cursorPosition1 = toLocalSpace(cursorPosition)
        return cursorPosition1.x >= drawPosition.x && cursorPosition1.x < drawPosition.x + drawSize.x &&
                cursorPosition1.y >= drawPosition.y && cursorPosition1.y < drawPosition.y + drawSize.y
    }

    fun update(time: Double) {
        transforms.synchronized {
            var i = 0
            while (i < transforms.size) {
                val transform = transforms[i]
                if (time < transform.startTime) {
                    break
                }

                invalidate()

                when (transform.getType()) {
                    TransformType.Fade, TransformType.Scale, TransformType.Rotate, TransformType.MoveX, TransformType.MoveY -> {
                        val value = transform.getSingle(time)
                        when (transform.transformType) {
                            TransformType.Fade -> color.w = value
                            TransformType.Scale -> scale.set(value)
                            TransformType.Rotate -> rotation = value
                            TransformType.MoveX -> position.x = value
                            TransformType.MoveY -> position.y = value
                            TransformType.OriginX -> customOrigin.x = value
                            TransformType.OriginY -> customOrigin.y = value
                            else -> {}
                        }
                    }
                    TransformType.Origin -> transform.getVector2f(time, customOrigin)
                    TransformType.Move -> transform.getVector2f(time, position)
                    TransformType.ScaleVector -> transform.getVector2f(time, scale)
                    TransformType.Additive, TransformType.HorizontalFlip, TransformType.VerticalFlip -> {
                        val value = transform.getBoolean(time)
                        when (transform.transformType) {
                            TransformType.Additive -> additive = value
                            TransformType.HorizontalFlip -> flipX = value
                            TransformType.VerticalFlip -> flipY = value
                            else -> {}
                        }
                    }
                    TransformType.Color3 -> transform.getColor3(time, color)
                    TransformType.Color4 -> transform.getColor4(time, color)
                    else -> {}
                }

                if (time >= transform.endTime) {
                    transforms.removeAt(i)
                    i--
                }

                i++
            }
        }
    }

    fun addTransform(transform: Transform, sortAfter: Boolean = true) {
        transforms.synchronized {
            add(transform)

            if (sortAfter) sortTransformations()
        }
        adjustTimesToTransformations()
    }

    private fun sortTransformations() {
        transforms.sortBy { transform -> transform.startTime }
    }

    fun adjustTimesToTransformations() {
        var startTime = Double.MAX_VALUE
        var endTime = -Double.MAX_VALUE
        transforms.synchronized {
            forEach {
                startTime = min(startTime, it.startTime)
                endTime = max(endTime, it.endTime)
            }
        }
        this.startTime = startTime
        this.endTime = endTime
    }

    protected companion object {
        private val matrixStack = ArrayDeque<Matrix4f>()

        fun popMatrix() = if(matrixStack.size > 0) matrixStack.pop().identity() else Matrix4f()
        fun pushMatrix(matrix: Matrix4f) = matrixStack.push(matrix)
    }
}