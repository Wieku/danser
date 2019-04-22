package me.wieku.framework.math.curves

import org.joml.Vector2f
import kotlin.math.ceil

class Bezier(vararg points: Vector2f) : NCCurve() {

    private val points = Array(points.size) { i -> Vector2f(points[i]) }

    init {
        var pointLength = 0.0f
        for (i in 1 until points.size) {
            pointLength += points[i].distance(points[i - 1])
        }

        pointLength = ceil(pointLength)

        for (i in 1..pointLength.toInt()) {
            approxLength += ncPointAt(i / pointLength, temp1).distance(ncPointAt((i - 1) / pointLength, temp2))
        }
    }

    override fun ncPointAt(t: Float, vec: Vector2f): Vector2f {
        vec.set(0f)
        val n = points.size - 1
        for (i in 0..n) {
            val b = bernstein(i, n, t)
            vec.add(points[i].x * b, points[i].y * b)
        }
        return vec
    }

    private fun binomialCoefficient(n: Int, k: Int): Int {
        if (k < 0 || k > n) {
            return 0
        }
        if (k == 0 || k == n) {
            return 1
        }

        val k1 = Math.min(k, n - k)
        var c = 1
        for (i in 0 until k1) {
            c = c * (n - i) / (i + 1)
        }

        return c
    }

    private fun bernstein(i: Int, n: Int, t: Float): Float {
        return (binomialCoefficient(n, i).toFloat() * Math.pow(t.toDouble(), i.toDouble()) * Math.pow(
            1.0 - t,
            (n - i).toDouble()
        )).toFloat()
    }

}