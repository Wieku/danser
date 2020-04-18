package me.wieku.framework.time

class FramedClock: IFramedClock {

    override var time: FrameInfo = FrameInfo(0.0, 0.0, 0.0)

    override var currentTime: Double = 0.0
        get() = time.currentTime

    override var clockRate: Double = 1.0
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

        rawTime += ((cTime-lTime).toDouble()*clockRate).toLong()

        time.currentTime = rawTime.toDouble() / 1000000.0
        time.frameTime = time.currentTime - time.lastTime

        lTime = cTime
    }
}