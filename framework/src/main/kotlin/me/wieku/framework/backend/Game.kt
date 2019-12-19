package me.wieku.framework.backend

import me.wieku.framework.time.FramedClock
import me.wieku.framework.time.IFramedClock
import me.wieku.framework.utils.Disposable

abstract class Game: Disposable {

    var updateClock: IFramedClock = FramedClock()
    var graphicsClock: IFramedClock = FramedClock()

    abstract fun setup()
    abstract fun update()
    abstract fun draw()

}