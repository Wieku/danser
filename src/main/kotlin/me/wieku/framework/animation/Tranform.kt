package me.wieku.framework.animation

import me.wieku.framework.math.Easing
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f

class Tranform {

    var transformType: TransformType
        private set

    private var startValues = FloatArray(4)
    private var endValues = FloatArray(4)
    private var easing = Easing.Linear

    var startTime: Float
        private set
    var endTime: Float
        private set

    constructor(transformType: TransformType, startTime: Float, endTime: Float) {
        if (transformType != TransformType.HorizontalFlip || transformType != TransformType.VerticalFlip || transformType != TransformType.Additive) {
            throw IllegalStateException("Wrong TransformationType used!")
        }

        this.transformType = transformType
        this.startTime = startTime
        this.endTime = endTime
    }

    constructor(
        transformType: TransformType,
        startTime: Float,
        endTime: Float,
        startValue: Float,
        endValue: Float,
        easing: Easing = Easing.Out
    ) {
        if (transformType != TransformType.Fade || transformType != TransformType.Rotate || transformType != TransformType.Scale || transformType != TransformType.MoveX || transformType != TransformType.MoveY) {
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
        startTime: Float,
        endTime: Float,
        startValueX: Float,
        startValueY: Float,
        endValueX: Float,
        endValueY: Float,
        easing: Easing = Easing.Out
    ) {
        if (transformType != TransformType.ScaleVector || transformType != TransformType.Move) {
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
        startTime: Float,
        endTime: Float,
        start: Vector2f,
        end: Vector2f,
        easing: Easing = Easing.Out
    ) {
        if (transformType != TransformType.ScaleVector || transformType != TransformType.Move) {
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
        startTime: Float,
        endTime: Float,
        start: Vector4f,
        end: Vector4f,
        easing: Easing = Easing.Out
    ) {
        if (transformType != TransformType.Color3 || transformType != TransformType.Color4) {
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

    fun getStatus(time: Float): TransformStatus {
        if (time < startTime) {
            return TransformStatus.NotStarted
        } else if (time >= endTime) {
            return TransformStatus.Ended
        }
        return TransformStatus.Going
    }

    private fun timeClamp(start: Float, end: Float, time: Float) =
        Math.max(0f, Math.min(1f, (time - start) / (end - start)))

    fun getProgress(time: Float): Float {
        return easing.func(timeClamp(startTime, endTime, time))
    }

    fun getSingle(time: Float): Float {
        return startValues[0] + getProgress(time) * (endValues[0] - startValues[0])
    }

    fun getVector2f(time: Float, to: Vector2f = Vector2f()): Vector2f {
        val progress = getProgress(time)
        return to.set(
            startValues[0] + progress * (endValues[0] - startValues[0]),
            startValues[1] + progress * (endValues[1] - startValues[1])
        )
    }

    fun getBoolean(time: Float): Boolean {
        return time >= startTime && time < endTime
    }

    fun getColor3(time: Float, to: Vector3f = Vector3f()): Vector3f {
        val progress = getProgress(time)
        return to.set(
            startValues[0] + progress * (endValues[0] - startValues[0]),
            startValues[1] + progress * (endValues[1] - startValues[1]),
            startValues[2] + progress * (endValues[2] - startValues[2])
        )
    }

    fun getColor4(time: Float, to: Vector4f = Vector4f()): Vector4f {
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