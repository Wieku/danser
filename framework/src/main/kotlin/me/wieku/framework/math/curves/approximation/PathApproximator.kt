package me.wieku.framework.math.curves.approximation

import me.wieku.framework.math.curves.Line

interface PathApproximator {
    fun approximate(): Array<Line>
}