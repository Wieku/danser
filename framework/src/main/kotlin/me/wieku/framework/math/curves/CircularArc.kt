package me.wieku.framework.math.curves

import org.joml.Vector2f
import kotlin.math.*

class CircularArc(point1: Vector2f, point2: Vector2f, point3: Vector2f) : Curve2d {

    private var pt1 = Vector2f(point1)
    private var pt2 = Vector2f(point2)
    private var pt3 = Vector2f(point3)

    var centre = Vector2f()

    var angleStart = 0f
        private set

    var totalAngle = 0f
        private set

    var radius = 0f
        private set

    var direction = 0f
        private set

    var unstable = false
        private set

    init {
        val aSq = pt2.distanceSquared(pt3)
        val bSq = pt1.distanceSquared(pt3)
        val cSq = pt1.distanceSquared(pt2)

        if (abs(aSq) < 0.001f || abs(bSq) < 0.001f || abs(cSq) < 0.001f) {
            unstable = true
        }

        val s = aSq * (bSq + cSq - aSq)
        val t = bSq * (aSq + cSq - bSq)
        val u = cSq * (aSq + bSq - cSq)

        val sum = s + t + u

        if (abs(sum) < 0.001f) {
            unstable = true
        }

        centre.set(pt1).mul(s).add(Vector2f(pt2).mul(t)).add(Vector2f(pt3).mul(u)).mul(1 / sum)

        val dA = Vector2f(pt1).sub(centre)
        val dC = Vector2f(pt3).sub(centre)

        radius = dA.length()

        angleStart = atan2(dA.y, dA.x)
        var end = atan2(dC.y, dC.x)

        while (end < angleStart) {
            end += 2 * Math.PI.toFloat()
        }

        direction = 1f
        totalAngle = end - angleStart

        val aToC = Vector2f(pt3).sub(pt1)
        aToC.set(aToC.y, -aToC.x)

        if (aToC.dot(Vector2f(pt2).sub(pt1)) < 0) {
            direction = -direction
            totalAngle = 2 * Math.PI.toFloat() - totalAngle
        }
    }

    private var temp = Vector2f()

    override fun getStartAngle(): Float {
        return pt1.angle(pointAt(1f / getLength(), temp))
    }

    override fun getEndAngle(): Float {
        return pt3.angle(pointAt(1f - 1f / getLength(), temp))
    }

    override fun pointAt(t: Float): Vector2f = pointAt(t, Vector2f())

    override fun pointAt(t: Float, dest: Vector2f): Vector2f {
        val rad = angleStart + direction * t * totalAngle
        return dest.set(cos(rad) * radius, sin(rad) * radius)
    }

    override fun getLength() = radius * totalAngle

}