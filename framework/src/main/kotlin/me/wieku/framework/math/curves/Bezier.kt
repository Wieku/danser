package me.wieku.framework.math.curves

import org.joml.Vector2f
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.pow

class Bezier(vararg points: Vector2f) : Curve2d {

    private var curveLength: Float = 0.0f

    private val points = Array(points.size) { i -> Vector2f(points[i]) }

    private val temp1 = Vector2f()
    private val temp2 = Vector2f()

    init {
        var pointLength = 0.0f
        for (i in 1 until points.size) {
            pointLength += points[i].distance(points[i - 1])
        }

        pointLength = ceil(pointLength)

        pointAt(0.0f, temp2)
        for (i in 1..pointLength.toInt()) {
            curveLength += pointAt(i / pointLength, temp1).distance(temp2)
            temp2.set(temp1)
        }
    }

    override fun getStartAngle(): Float {
        return pointAt(0.0f, temp1).angle(pointAt(1.0f / curveLength, temp2))
    }

    override fun getEndAngle(): Float {
        return pointAt(1.0f, temp1).angle(pointAt(1.0f - 1.0f / curveLength, temp2))
    }

    override fun getLength() = curveLength

    override fun pointAt(t: Float, dest: Vector2f): Vector2f {
        dest.set(0.0f)
        val n = points.size - 1
        for (i in 0..n) {
            val b = bernstein(i, n, t)
            dest.add(points[i].x * b, points[i].y * b)
        }
        return dest
    }

    private fun binomialCoefficient(n: Int, k: Int): Int {
        if (k !in 0..n)
            return 0

        if (k == 0 || k == n)
            return 1

        val k1 = min(k, n - k)
        var c = 1
        for (i in 0 until k1) {
            c *= (n - i) / (i + 1)
        }

        return c
    }

    private fun bernstein(i: Int, n: Int, t: Float): Float {
        return binomialCoefficient(n, i) * t.pow(i) * (1.0f - t).pow(n - 1)
    }

}