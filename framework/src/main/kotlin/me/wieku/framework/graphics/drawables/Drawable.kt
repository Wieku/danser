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

abstract class Drawable(): Disposable, KoinComponent {

    val clock: IFramedClock by inject()

    constructor(inContext: Drawable.() -> Unit):this(){
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

    var origin = Origin.Centre
    var customOrigin = Vector2f()
    var drawOrigin = Vector2f()

    var inheritScale = true
    var scale = Vector2f(1f)
    var drawScale = Vector2f(1f)

    var fillMode = Scaling.None
    var size = Vector2f(1f)
    var drawSize = Vector2f()

    var color = Vector4f(1f, 1f, 1f, 1f)

    var flipX = false
    var flipY = false
    var rotation         = 0f
    var additive         = false

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

        if (parent != null) {
            parent?.let {

                drawSize.set(fillMode.apply(size.x, size.y, it.drawSize.x, it.drawSize.y)).mul(drawScale)
                drawOrigin.set(if (origin == Origin.Custom) customOrigin else origin.offset).mul(drawSize)

                val anchorV = Vector2f()

                if (anchor != Origin.None) {
                    anchorV.set(if (anchor == Origin.Custom) customAnchor else anchor.offset)
                    anchorV.mul(it.drawSize).add(it.drawPosition).add(it.childOffset)
                }

                drawPosition.set(position).sub(drawOrigin).add(anchorV)
                return
            }
        } else {
            drawSize.set(size.x, size.y).mul(drawScale)
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
            var transform = transforms.get(i)
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
                    }
                }
                TransformType.Origin -> transform.getVector2f(time, customOrigin)
                TransformType.Move -> transform.getVector2f(time, position)
                TransformType.ScaleVector -> transform.getVector2f(time, scale)
                TransformType.Additive, TransformType.HorizontalFlip, TransformType.VerticalFlip -> {
                    val value = transform.getBoolean(time)
                    when (transform.transformType) {
                        TransformType.Additive->additive = value
                        TransformType.HorizontalFlip->flipX=value
                        TransformType.VerticalFlip->flipY=value
                    }
                }
                TransformType.Color3-> transform.getColor3(time, color)
                TransformType.Color4-> transform.getColor4(time, color)
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
            startTime = Math.min(startTime, it.startTime)
            endTime = Math.max(endTime, it.endTime)
        }
        this.startTime = startTime
        this.endTime = endTime
    }
}