package me.wieku.framework.math.curves

import org.joml.Vector2f

class Line(point1: Vector2f, point2: Vector2f) : Curve2d {
    private var pt1 = Vector2f(point1)
    private var pt2 = Vector2f(point2)

    override fun getStartAngle(): Float = pt1.angle(pt2)

    override fun getEndAngle(): Float = pt2.angle(pt1)

    override fun pointAt(t: Float, dest: Vector2f): Vector2f = dest.set(pt2).sub(pt1).mul(t).add(pt1)

    override fun getLength() = pt1.distance(pt2)
}