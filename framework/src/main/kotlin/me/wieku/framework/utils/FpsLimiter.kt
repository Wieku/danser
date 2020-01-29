package me.wieku.framework.utils

import kotlin.math.*

class FpsLimiter(var fps: Int = 60) {

    private var variableYieldTime: Long = 0
    private var lastTime: Long = 0

    private var lastRealTime: Long = 0

    var delta: Float = 0f
        private set

    /**
     * An accurate sync method that adapts automatically
     * to the system it runs on to provide reliable results.
     *
     * @author kappa (On the LWJGL Forums)
     */
    fun sync() {
        if (fps > 0) {
            val sleepTime = (1000000000 / fps).toLong() // nanoseconds to sleep this frame
            // yieldTime + remainder micro & nano seconds if smaller than sleepTime
            val yieldTime = min(sleepTime, variableYieldTime + sleepTime % (1000 * 1000))
            var overSleep: Long = 0 // time the sync goes over by

            try {
                while (true) {
                    val t = System.nanoTime() - lastTime

                    if (t < sleepTime - yieldTime) {
                        Thread.sleep(1)
                    } else if (t < sleepTime) {
                        // burn the last few CPU cycles to ensure accuracy
                        Thread.yield()
                    } else {
                        overSleep = t - sleepTime
                        break // exit while loop
                    }
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } finally {
                lastTime = System.nanoTime() - min(overSleep, sleepTime)

                // auto tune the time sync should yield
                if (overSleep > variableYieldTime) {
                    // increase by 200 microseconds (1/5 a ms)
                    variableYieldTime = min(variableYieldTime + 200 * 1000, sleepTime)
                } else if (overSleep < variableYieldTime - 200 * 1000) {
                    // decrease by 2 microseconds
                    variableYieldTime = max(variableYieldTime - 2 * 1000, 0)
                }
            }
        }

        val time = System.nanoTime()

        if (lastRealTime == 0L) {
            lastRealTime = time
        }

        delta = (time - lastRealTime) / 1000000f
        lastRealTime = time
    }

}