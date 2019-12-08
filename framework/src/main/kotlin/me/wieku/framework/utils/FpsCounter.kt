package me.wieku.framework.utils

class FpsCounter(samples: Int = 60) {

    private val samples = FloatArray(samples)
    private var index = 0

    val fps: Float
        get() = 1000f / frameTime

    val frameTime: Float
        get() {
            var sum = 0f
            for (time in samples) {
                sum += time
            }
            return sum / samples.size
        }

    fun putSample(delta: Float) {
        if (index >= samples.size) {
            index = 0
        }
        samples[index] = delta
        index++
    }

}