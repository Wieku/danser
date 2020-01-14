package me.wieku.framework.math.curves.approximation

import me.wieku.framework.math.cpy
import me.wieku.framework.math.curves.CircularArc
import me.wieku.framework.math.curves.Line

class CircularArcApproximator(private val detail: Float, private val arc: CircularArc) : CurveApproximator {
    override fun approximate(): Array<Line> {
        if (arc.unstable) {
            return arrayOf(
                Line(arc.pt1.cpy(), arc.pt2.cpy()),
                Line(arc.pt2.cpy(), arc.pt3.cpy())
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