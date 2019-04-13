package me.wieku.framework.math

import org.joml.Vector2f

fun Vector2f.rot(rad: Float): Vector2f {
    val cs = Math.cos(rad.toDouble())
    val sn = Math.sin(rad.toDouble())

    val px = this.x * cs - this.y * sn
    val py = this.x * sn + this.y * cs
    return set(px.toFloat(), py.toFloat())
}