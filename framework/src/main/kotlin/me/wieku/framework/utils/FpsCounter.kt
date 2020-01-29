package me.wieku.framework.utils

import kotlin.math.max

class FpsCounter(samples: Int = 60) {

    private val samples = FloatArray(samples) { 16.6667f }
    private var index = -1

    val fps: Float
        get() = if (frameTime > 0f) 1000f / frameTime else 0f

    val frameTime: Float
        get() {
            return samples.sum() / samples.size
        }

    fun putSample(delta: Float) {
        index = ++index % samples.size
        samples[index] = delta
    }

}