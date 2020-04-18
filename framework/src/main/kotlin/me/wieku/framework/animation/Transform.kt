package me.wieku.framework.animation

import me.wieku.framework.math.Easing
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f
import kotlin.math.max
import kotlin.math.min

class Transform {

    var transformType: TransformType
        private set

    private var startValues = FloatArray(4)
    private var endValues = FloatArray(4)
    private var easing = Easing.Linear

    var startTime: Double
        private set
    var endTime: Double
        private set

    constructor(transformType: TransformType, startTime: Double, endTime: Double) {
        if (transformType != TransformType.HorizontalFlip || transformType != TransformType.VerticalFlip || transformType != TransformType.Additive) {
            throw IllegalStateException("Wrong TransformationType used!")
        }

        this.transformType = transformType
        this.startTime = startTime
        this.endTime = endTime
    }

    constructor(
        transformType: TransformType,
        startTime: Double,
        endTime: Double,
        startValue: Float,
        endValue: Float,
        easing: Easing = Easing.Out
    ) {
        if (!(transformType == TransformType.Fade || transformType == TransformType.Rotate || transformType == TransformType.Scale || transformType == TransformType.MoveX || transformType == TransformType.MoveY || transformType == TransformType.OriginX || transformType == TransformType.OriginY)) {
            throw IllegalStateException("Wrong TransformationType used!")
        }

        this.transformType = transformType
        this.startTime = startTime
        this.endTime = endTime
        this.easing = easing
        startValues[0] = startValue
        endValues[0] = endValue
    }

    constructor(
        transformType: TransformType,
        startTime: Double,
        endTime: Double,
        startValueX: Float,
        startValueY: Float,
        endValueX: Float,
        endValueY: Float,
        easing: Easing = Easing.Out
    ) {
        if (!(transformType == TransformType.ScaleVector || transformType == TransformType.Move || transformType == TransformType.Origin)) {
            throw IllegalStateException("Wrong TransformationType used!")
        }

        this.transformType = transformType
        this.startTime = startTime
        this.endTime = endTime
        this.easing = easing
        startValues[0] = startValueX
        startValues[1] = startValueY
        endValues[0] = endValueX
        endValues[1] = endValueY
    }

    constructor(
        transformType: TransformType,
        startTime: Double,
        endTime: Double,
        start: Vector2f,
        end: Vector2f,
        easing: Easing = Easing.Out
    ) {
        if (!(transformType == TransformType.ScaleVector || transformType == TransformType.Move)) {
            throw IllegalStateException("Wrong TransformationType used!")
        }

        this.transformType = transformType
        this.startTime = startTime
        this.endTime = endTime
        this.easing = easing
        startValues[0] = start.x
        startValues[1] = start.y
        endValues[0] = end.x
        endValues[1] = end.y
    }

    constructor(
        transformType: TransformType,
        startTime: Double,
        endTime: Double,
        start: Vector4f,
        end: Vector4f,
        easing: Easing = Easing.Out
    ) {
        if (!(transformType == TransformType.Color3 || transformType == TransformType.Color4)) {
            throw IllegalStateException("Wrong TransformationType used!")
        }

        this.transformType = transformType
        this.startTime = startTime
        this.endTime = endTime
        this.easing = easing
        startValues[0] = start.x
        startValues[1] = start.y
        startValues[2] = start.z
        startValues[3] = start.w

        endValues[0] = end.x
        endValues[1] = end.y
        endValues[2] = end.z
        endValues[3] = end.w
    }

//Missing color

    fun getStatus(time: Double): TransformStatus {
        if (time < startTime) {
            return TransformStatus.NotStarted
        } else if (time >= endTime) {
            return TransformStatus.Ended
        }
        return TransformStatus.Going
    }

    private fun timeClamp(start: Double, end: Double, time: Double) =
        max(0.0, min(1.0, (time - start) / (end - start)))

    fun getProgress(time: Double): Float {
        return easing.func(timeClamp(startTime, endTime, time).toFloat())
    }

    fun getSingle(time: Double): Float {
        return startValues[0] + getProgress(time) * (endValues[0] - startValues[0])
    }

    fun getVector2f(time: Double, to: Vector2f = Vector2f()): Vector2f {
        val progress = getProgress(time)
        return to.set(
            startValues[0] + progress * (endValues[0] - startValues[0]),
            startValues[1] + progress * (endValues[1] - startValues[1])
        )
    }

    fun getBoolean(time: Double): Boolean {
        return time >= startTime && time < endTime
    }

    fun getColor3(time: Double, to: Vector3f = Vector3f()): Vector3f {
        val progress = getProgress(time)
        return to.set(
            startValues[0] + progress * (endValues[0] - startValues[0]),
            startValues[1] + progress * (endValues[1] - startValues[1]),
            startValues[2] + progress * (endValues[2] - startValues[2])
        )
    }

    fun getColor3(time: Double, to: Vector4f = Vector4f()): Vector4f {
        val progress = getProgress(time)
        return to.set(
            startValues[0] + progress * (endValues[0] - startValues[0]),
            startValues[1] + progress * (endValues[1] - startValues[1]),
            startValues[2] + progress * (endValues[2] - startValues[2]),
            to.w
        )
    }

    fun getColor4(time: Double, to: Vector4f = Vector4f()): Vector4f {
        val progress = getProgress(time)
        return to.set(
            startValues[0] + progress * (endValues[0] - startValues[0]),
            startValues[1] + progress * (endValues[1] - startValues[1]),
            startValues[2] + progress * (endValues[2] - startValues[2]),
            startValues[3] + progress * (endValues[3] - startValues[3])
        )
    }

    fun getType(): TransformType {
        return transformType
    }

}