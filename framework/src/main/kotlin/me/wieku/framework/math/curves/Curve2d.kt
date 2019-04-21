package me.wieku.framework.math.curves

import org.joml.Vector2f

interface Curve2d: Curve<Vector2f> {
    fun getStartAngle(): Float
    fun getEndAngle(): Float
}