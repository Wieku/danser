package me.wieku.framework.math

import org.joml.Vector2f
import kotlin.math.*

fun Vector2f.rot(rad: Float): Vector2f {
    val cs = cos(rad)
    val sn = sin(rad)

    val px = this.x * cs - this.y * sn
    val py = this.x * sn + this.y * cs
    return set(px, py)
}

fun vector2fRad(rad: Float, length: Float): Vector2f {
    return Vector2f(cos(rad) * length, sin(rad) * length)
}

fun clamp(value: Float, minVal: Float, maxVal: Float) = min(maxVal, max(value, minVal))

fun Float.clamped(minVal: Float, maxVal: Float): Float = clamp(this, minVal, maxVal)

fun Float.equalsEpsilon(b: Float, epsilon: Float = 0.01f) = abs(this-b) <= epsilon