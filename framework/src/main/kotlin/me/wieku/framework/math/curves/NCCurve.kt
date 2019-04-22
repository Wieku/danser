package me.wieku.framework.math.curves

import org.joml.Vector2f

/**
 * Non-constant velocity curve
 */
abstract class NCCurve : Curve2d {

    protected var approxLength = 0f

    protected var temp1 = Vector2f()
    protected var temp2 = Vector2f()

    override fun pointAt(t: Float): Vector2f = pointAt(t, Vector2f())

    override fun pointAt(t: Float, dest: Vector2f): Vector2f {
        val desiredWidth = (approxLength * t) * (approxLength * t)
        var width = 0.0f
        dest.set(ncPointAt(0f, temp1))
        var c = 0.0f
        while (width < desiredWidth) {
            val pt = ncPointAt(c, temp1)
            width += pt.distanceSquared(dest)
            if (width > desiredWidth) {
                return dest
            }
            dest.set(pt)
            c += 1.0f / (approxLength * 2 - 1)
        }

        return dest
    }

    override fun getStartAngle(): Float {
        return ncPointAt(0f, temp1).angle(ncPointAt(1.0f / approxLength, temp2))
    }

    override fun getEndAngle(): Float {
        return ncPointAt(1f, temp1).angle(ncPointAt((approxLength - 1.0f) / approxLength, temp2))
    }

    override fun getLength() = approxLength

    abstract fun ncPointAt(t: Float, vec: Vector2f): Vector2f
}