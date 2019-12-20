package me.wieku.framework.animation

import me.wieku.framework.math.Easing

data class GliderEvent(
    val startTime: Float,
    val endTime: Float,
    val startValue: Float,
    val endValue: Float,
    val easing: Easing
)