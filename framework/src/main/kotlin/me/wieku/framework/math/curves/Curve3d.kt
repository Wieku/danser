package me.wieku.framework.math.curves

import org.joml.Vector2f
import org.joml.Vector3f

interface Curve3d: Curve<Vector3f> {
    fun getStartAngle(): Vector2f
    fun getEndAngle(): Vector2f
}