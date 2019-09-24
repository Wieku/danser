package me.wieku.framework.graphics.drawables.sprite

import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.graphics.textures.TextureRegion
import org.joml.Vector2f
import org.joml.Vector4f
import java.util.*

open class Sprite() {

    var texture: TextureRegion? = null
    var width = 0f
    var height = 0f

    var transforms = ArrayList<Transform>()

    var startTime = 0f
    var endTime = 0f
    var depth = 0f

    var position         = Vector2f(0f, 0f)
    var origin           = Vector2f(0f, 0f)
    var scale            = Vector2f(1f, 1f)
    var flipX = false
    var flipY = false
    var rotation         = 0f
    var color            = Vector4f(1f, 1f, 1f, 1f)
    var additive         = false
    var showForever      = true

    constructor(texture: TextureRegion, width: Float = texture.getWidth(), height: Float = texture.getHeight(), origin: Vector2f = Vector2f(0.5f, 0.5f)): this() {
        this.texture = texture
        this.width = width
        this.height = height
        this.origin.set(origin)
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
                        TransformType.OriginX -> position.x = value
                        TransformType.OriginY -> position.y = value
                    }
                }
                TransformType.Origin -> transform.getVector2f(time, origin)
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

    fun sortTransformations() {
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