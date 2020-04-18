package me.wieku.framework.utils

class FpsCounter(samples: Int = 60) {

    private val samples = DoubleArray(samples) { 1000.0 / 60 }
    private var index = -1

    val fps: Double
        get() = if (frameTime > 0.0) 1000.0 / frameTime else 0.0

    val frameTime: Double
        get() {
            return samples.sum() / samples.size
        }

    fun putSample(delta: Double) {
        index = ++index % samples.size
        samples[index] = delta
    }

}