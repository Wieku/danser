package me.wieku.framework.utils

import kotlin.math.max

class FpsCounter(samples: Int = 60) {

    private val samples = FloatArray(samples)
    private var sampleBase = 1
    private var index = 0

    val fps: Float
        get() = if (frameTime > 0f) 1000f / frameTime else 0f

    val frameTime: Float
        get() {
            var sum = 0f
            for (time in samples) {
                sum += time
            }
            return sum / sampleBase
        }

    fun putSample(delta: Float) {
        if (index >= samples.size) {
            index = 0
        }

        sampleBase = max(sampleBase, index + 1)

        samples[index] = delta
        index++
    }

}