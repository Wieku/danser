package me.wieku.framework.time

class FramedClock: IFramedClock {

    override var time: FrameInfo = FrameInfo(0f, 0f, 0f)

    override var currentTime: Float = 0f
        get() = time.currentTime

    override var clockRate: Float = 1f
    override var isRunning: Boolean = true


    private var rawTime = 0L
    private var cTime = 0L
    private var lTime = 0L

    override fun updateClock() {
        time.lastTime = time.currentTime
        cTime = System.nanoTime()

        if (lTime == 0L) {
            lTime = cTime
         }

        rawTime += ((cTime-lTime).toDouble()*clockRate.toDouble()).toLong()

        time.currentTime = rawTime.toFloat() / 1000000f
        time.frameTime = time.currentTime - time.lastTime//((cTime-lTime).toDouble()/1000000.0).toFloat()

        lTime = cTime
    }
}