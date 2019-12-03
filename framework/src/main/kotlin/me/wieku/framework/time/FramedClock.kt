package me.wieku.framework.time

class FramedClock: IFramedClock {

    override var time: FrameInfo = FrameInfo(0f, 0f, 0f)

    override var currentTime: Float = 0f
        get() = time.currentTime

    override var clockRate: Float = 1f
    override var isRunning: Boolean = true


    private var cTime = 0L
    private var lTime = 0L

    override fun updateClock() {
        time.lastTime = time.currentTime
        cTime = System.nanoTime()

        if (lTime == 0L) {
            lTime = cTime
         }

        time.frameTime = (cTime-lTime)/1000000f
        time.currentTime += time.frameTime

        lTime = cTime
    }
}