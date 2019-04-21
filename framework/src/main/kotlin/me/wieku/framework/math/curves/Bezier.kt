package me.wieku.framework.math.curves

import org.joml.Vector2f
import kotlin.math.ceil

class Bezier(vararg points: Vector2f): Curve2d {

    private val points = Array(points.size) { i -> Vector2f(points[i])}
    private var approxLength = 0f

    init {
        var pointLength = 0.0f
        for (i in 1 until points.size) {
            pointLength += points[i].distance(points[i-1])
        }

        pointLength = ceil(pointLength)

        val roundedLength = pointLength.toInt()

        val p1 = Vector2f()
        val p2 = Vector2f()

        for (i in 1..roundedLength) {
            approxLength += npointAt(i / pointLength, p1).distance(npointAt((i-1) / pointLength, p2))
        }
    }

    override fun getStartAngle(): Float {
        return points.first().angle(npointAt(1.0f / approxLength, temp))
    }

    override fun getEndAngle(): Float {
        return points.last().angle(npointAt((approxLength-1.0f) / approxLength, temp))
    }

    override fun pointAt(t: Float): Vector2f = pointAt(t, Vector2f())

    private var temp = Vector2f()
    override fun pointAt(t: Float, dest: Vector2f): Vector2f {
        val desiredWidth = (approxLength * t) * (approxLength * t)
        var width = 0.0f
        dest.set(points[0])
        var c = 0.0f
        while (width < desiredWidth) {
            val pt = npointAt(c, temp)
            width += pt.distanceSquared(dest)
            if (width > desiredWidth) {
                return dest
            }
            dest.set(pt)
            c += 1.0f / (approxLength*2-1)
        }

        return dest
    }

    private fun npointAt(t: Float, vec: Vector2f): Vector2f {
        vec.set(0f)
        val n = points.size - 1
        for (i in 0..n) {
            val b = bernstein(i, n, t)
            vec.add(points[i].x*b, points[i].y*b)
        }
        return vec
    }

    override fun getLength() = approxLength

    private fun binomialCoefficient(n: Int, k: Int): Int {
        if (k < 0 || k > n) {
            return 0
        }
        if (k == 0 || k == n) {
            return 1
        }

        val k1 = Math.min(k, n-k)
        var c = 1
        for (i in 0 until k1) {
            c = c * (n - i) / (i + 1)
        }

        return c
    }

    private fun bernstein(i: Int, n: Int, t: Float): Float {
        return (binomialCoefficient(n, i).toFloat() * Math.pow(t.toDouble(), i.toDouble()) * Math.pow(1.0-t, (n-i).toDouble())).toFloat()
    }

}