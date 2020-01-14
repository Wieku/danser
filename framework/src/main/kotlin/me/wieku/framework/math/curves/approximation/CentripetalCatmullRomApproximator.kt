package me.wieku.framework.math.curves.approximation

import me.wieku.framework.math.curves.CentripetalCatmullRom
import me.wieku.framework.math.curves.Line

class CentripetalCatmullRomApproximator(private val detail: Int, private val curve: CentripetalCatmullRom) : CurveApproximator {
    override fun approximate(): Array<Line> {

        return Array(detail) { p ->
            Line(
                curve.pointAt(p.toFloat() / detail),
                curve.pointAt((p + 1).toFloat() / detail)
            )
        }
    }
}