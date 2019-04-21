package me.wieku.framework.math.curves

import org.joml.Vector2f
import java.util.*
import kotlin.math.ceil

class CentripetalCatmullRom(vararg points: Vector2f) : Curve2d {

    private val points: Array<Vector2f>
    private var approxLength = 0f

    init {
        check(points.size == 4) { "4 points are needed to create centripetal catmull rom" }

        this.points = Array(points.size) { i -> Vector2f(points[i]) }

        val pointLength = ceil(this.points[1].distance(this.points[2]))

        val roundedLength = pointLength.toInt()

        val p1 = Vector2f()
        val p2 = Vector2f()

        for (i in 1..roundedLength) {
            approxLength += npointAt(i / pointLength, p1).distance(npointAt((i - 1) / pointLength, p2))
        }
    }

    override fun getStartAngle(): Float {
        return points[1].angle(npointAt(1.0f / approxLength, temp))
    }

    override fun getEndAngle(): Float {
        return points[2].angle(npointAt((approxLength - 1.0f) / approxLength, temp))
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
            c += 1.0f / (approxLength * 2 - 1)
        }

        return dest
    }

    private fun npointAt(t: Float, vec: Vector2f): Vector2f {
        return findPoint(points[0], points[1], points[2], points[3], t, vec)
    }

    private fun findPoint(vec1: Vector2f, vec2: Vector2f, vec3: Vector2f, vec4: Vector2f, t: Float, vec: Vector2f): Vector2f {
        val t2 = t * t
        val t3 = t * t2

        return vec.set(0.5f*(2*vec2.x+(-vec1.x+vec3.x)*t+(2*vec1.x-5*vec2.x+4*vec3.x-vec4.x)*t2+(-vec1.x+3*vec2.x-3*vec3.x+vec4.x)*t3),
            0.5f*(2*vec2.y+(-vec1.y+vec3.y)*t+(2*vec1.y-5*vec2.y+4*vec3.y-vec4.y)*t2+(-vec1.y+3*vec2.y-3*vec3.y+vec4.y)*t3))
    }
    
    override fun getLength() = approxLength

}