package me.wieku.framework.math.curves.approximation

import me.wieku.framework.math.curves.Line

interface CurveApproximator {
    fun approximate(): Array<Line>
}