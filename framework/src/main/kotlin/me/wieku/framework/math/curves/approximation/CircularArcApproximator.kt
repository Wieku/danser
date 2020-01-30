package me.wieku.framework.math.curves.approximation

import me.wieku.framework.math.curves.CircularArc
import me.wieku.framework.math.curves.Line

class CircularArcApproximator(private val detail: Float, private val arc: CircularArc) : CurveApproximator {
    override fun approximate(): Array<Line> {
        if (arc.unstable) {
            return arrayOf(
                Line(arc.pt1, arc.pt2),
                Line(arc.pt2, arc.pt3)
            )
        }

        val segments = (arc.radius * arc.totalAngle * detail).toInt()

        return Array(segments) { p ->
            Line(
                arc.pointAt(p.toFloat() / segments),
                arc.pointAt((p + 1).toFloat() / segments)
            )
        }
    }
}