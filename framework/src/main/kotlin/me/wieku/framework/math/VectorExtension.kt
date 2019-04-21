package me.wieku.framework.math

import org.joml.Vector2f
import kotlin.math.cos
import kotlin.math.sin

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