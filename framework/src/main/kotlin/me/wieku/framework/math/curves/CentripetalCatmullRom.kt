package me.wieku.framework.math.curves

import org.joml.Vector2f
import kotlin.math.ceil

class CentripetalCatmullRom(points: Array<Vector2f>) : NCCurve() {
    private val points: Array<Vector2f>

    init {
        check(points.size == 4) { "4 points are needed to create centripetal catmull rom" }

        this.points = Array(points.size) { i -> Vector2f(points[i]) }

        val pointLength = ceil(this.points[1].distance(this.points[2]))

        for (i in 1..pointLength.toInt()) {
            approxLength += ncPointAt(i / pointLength, temp1).distance(ncPointAt((i - 1) / pointLength, temp2))
        }
    }

    override fun ncPointAt(t: Float, vec: Vector2f): Vector2f {
        return findPoint(points[0], points[1], points[2], points[3], t, vec)
    }

    private fun findPoint(
        vec1: Vector2f,
        vec2: Vector2f,
        vec3: Vector2f,
        vec4: Vector2f,
        t: Float,
        vec: Vector2f
    ): Vector2f {
        val t2 = t * t
        val t3 = t * t2

        return vec.set(
            0.5f * (2 * vec2.x + (-vec1.x + vec3.x) * t + (2 * vec1.x - 5 * vec2.x + 4 * vec3.x - vec4.x) * t2 + (-vec1.x + 3 * vec2.x - 3 * vec3.x + vec4.x) * t3),
            0.5f * (2 * vec2.y + (-vec1.y + vec3.y) * t + (2 * vec1.y - 5 * vec2.y + 4 * vec3.y - vec4.y) * t2 + (-vec1.y + 3 * vec2.y - 3 * vec3.y + vec4.y) * t3)
        )
    }

}