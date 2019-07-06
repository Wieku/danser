package me.wieku.framework.math.curves

import me.wieku.framework.math.clamp
import me.wieku.framework.math.vector2fRad
import org.joml.Vector2f
import java.util.*

class MultiCurve : Curve2d {

    private var curves = ArrayList<Curve2d>()
    private var combinedWidth = 0f
    private var desiredWidth = 0f

    private var temp1 = Vector2f()
    private var temp2 = Vector2f()

    constructor(vararg curves: Curve2d) {
        this.curves.addAll(curves)
        this.curves.forEach { combinedWidth += it.getLength() }
        desiredWidth = combinedWidth
    }

    constructor(curveType: MultiCurveType, points: List<Vector2f>) {
        var type = curveType

        var points1 = LinkedList<Vector2f>()
        points1.addAll(points)

        if (points1.size < 3) {
            type = MultiCurveType.Linear
        }

        if (type == MultiCurveType.Perfect) {
            val c = CircularArc(points1[0], points1[1], points1[2])
            if (c.unstable) {
                type = MultiCurveType.Linear
            } else {
                curves.add(c)
                combinedWidth += c.getLength()
            }
        }

        if (type == MultiCurveType.Linear) {
            for (i in 1 until points1.size) {
                val c = Line(points1[i-1], points1[i])
                curves.add(c)
                combinedWidth += c.getLength()
            }
        }

        if (type == MultiCurveType.Bezier) {
            var lastIndex = 0
            for ((i, p) in points1.withIndex()) {
                if ((i == points1.size - 1 && p != points1[i - 1]) || (i < points1.size - 1 && points1[i + 1] == p)) {
                    var pts = points1.slice(lastIndex until i+1)
                    var c : Curve2d
                    c = when {
                        pts.size > 2 -> Bezier(*pts.toTypedArray())
                        pts.size == 1 -> Line(pts[0], pts[0])
                        else -> Line(pts[0], pts[1])
                    }

                    curves.add(c)
                    combinedWidth += c.getLength()
                    lastIndex = i + 1
                }
            }
        }

        if (type == MultiCurveType.Catmull) {
            if (points1[0] != points1[1]) {
                points1.addFirst(Vector2f(points1.first))
            }

            if (points1[points1.size - 1] != points1[points1.size - 2]) {
                points1.addLast(Vector2f(points1.last))
            }

            for (i in 0 until points1.size - 3) {
                val c = CentripetalCatmullRom(points1.slice(i until (i+4)).toTypedArray())
                curves.add(c)
                combinedWidth += c.getLength()
            }
        }

        desiredWidth = combinedWidth
    }

    constructor(curveType: MultiCurveType, points: List<Vector2f>, desiredWidth: Float): this(curveType, points) {

        this.desiredWidth = desiredWidth

        if (desiredWidth > combinedWidth) {
            val last = curves.last()
            val p2 = last.pointAt(1f)
            val p3 = vector2fRad(last.getEndAngle(), desiredWidth-combinedWidth).add(p2)
            val c = Line(p2, p3)
            curves.add(c)
            combinedWidth += c.getLength()
        }
    }

    override fun getStartAngle(): Float {
        return pointAt(0f, temp1).angle(pointAt(1.0f / combinedWidth, temp2))
    }

    override fun getEndAngle(): Float {
        return pointAt(1f, temp1).angle(pointAt((combinedWidth - 1.0f) / combinedWidth, temp2))
    }

    override fun pointAt(t: Float): Vector2f = pointAt(t, Vector2f())

    override fun pointAt(t: Float, dest: Vector2f): Vector2f {
        val currentWidth = clamp(t * desiredWidth, 0f, desiredWidth)
        var f0 = 0f
        var f1 = curves[1].getLength()

        var i = 0
        while (f1 < currentWidth && i < curves.size - 1) {
            i++
            f0 = f1
            f1 += curves[i].getLength()
        }

        return curves[i].pointAt((currentWidth - f0) / (f1 - f0), dest)
    }

    override fun getLength() = desiredWidth

}