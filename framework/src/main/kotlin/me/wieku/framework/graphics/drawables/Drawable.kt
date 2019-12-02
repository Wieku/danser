package me.wieku.framework.graphics.drawables

import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import me.wieku.framework.time.IFramedClock
import me.wieku.framework.utils.Disposable
import org.joml.Vector2f
import org.joml.Vector4f
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.math.max
import kotlin.math.min

abstract class Drawable() : Disposable, KoinComponent {

    val clock: IFramedClock by inject()

    constructor(inContext: Drawable.() -> Unit) : this() {
        inContext()
    }

    /**
     * Relative position of drawable
     */
    open var position = Vector2f()
    var fixedPosition = Vector2f()
    var drawPosition = Vector2f()

    var childOffset = Vector2f()

    var anchor = Origin.Centre
    var customAnchor = Vector2f()
    private val drawAnchor = Vector2f()

    var origin = Origin.Centre
    var customOrigin = Vector2f()
    var drawOrigin = Vector2f()

    var inheritScale = true
    var scale = Vector2f(1f)
    var drawScale = Vector2f(1f)

    var fillMode = Scaling.None
    var size = Vector2f(1f)
    var drawSize = Vector2f()

    var inheritColor = false
    var color = Vector4f(1f, 1f, 1f, 1f)
    var drawColor = Vector4f(1f, 1f, 1f, 1f)

    var flipX = false
    var flipY = false
    
    var rotation = 0f
    
    var additive = false

    var shearX = 0f
    var shearY = 0f

    var startTime = 0f
    var endTime = 0f
    var drawForever = true

    var transforms = ArrayList<Transform>()

    /**
     * parent of this drawable
     */
    var parent: Drawable? = null

    var isValid = false
        private set

    open fun invalidate() {
        isValid = false
    }

    open fun update() {
        update(clock.currentTime)
        if (!isValid) {
            updateDrawable()
        }
    }

    private fun updateDrawable() {
        drawScale.set(scale)
        drawColor.set(color)

        parent?.let { parent ->

            drawSize.set(fillMode.apply(size.x, size.y, parent.drawSize.x, parent.drawSize.y)).mul(drawScale)
            drawOrigin.set(if (origin == Origin.Custom) customOrigin else origin.offset).mul(drawSize)

            drawAnchor.set(0f)

            if (anchor != Origin.None) {
                drawAnchor.set(if (anchor == Origin.Custom) customAnchor else anchor.offset)
                drawAnchor.mul(parent.drawSize).add(parent.drawPosition).add(parent.childOffset)
            }

            drawPosition.set(position).sub(drawOrigin).add(drawAnchor)
            drawColor.mul(parent.drawColor)
            return
        }

        if (parent == null) {
            drawSize.set(size).mul(drawScale)
            drawOrigin.set(if (origin == Origin.Custom) customOrigin else origin.offset).mul(drawSize)
            drawPosition.set(position).sub(drawOrigin)
        }
    }

    abstract fun draw(batch: SpriteBatch)

    fun canBeDeleted(): Boolean {
        return !drawForever && clock.currentTime >= endTime
    }

    fun update(time: Float) {

        var i = 0
        while (i < transforms.size) {
            val transform = transforms[i]
            if (time < transform.startTime) {
                break
            }

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

    fun addTransform(transform: Transform, sortAfter: Boolean = false) {
        transforms.add(transform)

        if (sortAfter)
            sortTransformations()
    }

    private fun sortTransformations() {
        transforms.sortBy { tranform -> tranform.startTime }
    }

    fun adjustTimesToTransformations() {
        var startTime = Float.MAX_VALUE
        var endTime = -Float.MAX_VALUE
        transforms.forEach {
            startTime = min(startTime, it.startTime)
            endTime = max(endTime, it.endTime)
        }
        this.startTime = startTime
        this.endTime = endTime
    }
}