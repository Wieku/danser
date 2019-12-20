package me.wieku.framework.animation

import me.wieku.framework.math.Easing
import java.util.*

class Glider(var value: Float) {
    private val eventQueue = ArrayDeque<GliderEvent>()

    var easing: Easing = Easing.Linear

    private var lastTime = 0f

    fun update(time: Float) {
        lastTime = time

        while (eventQueue.isNotEmpty()) {
            val event = eventQueue.peekFirst()
            if (time < event.startTime) break

            value = event.startValue + easing.func((time-event.startTime)/(event.endTime-event.startTime)) * (event.endValue - event.startValue)

            if (time >= event.endTime) {
                eventQueue.pop()
            } else break
        }

    }

    fun addEvent(startTime: Float, endTime: Float, startValue: Float, endValue: Float) {
        eventQueue.push(GliderEvent(startTime, endTime, startValue, endValue))
    }

    fun addEvent(endTime: Float, endValue: Float) {
        eventQueue.clear()
        eventQueue.push(GliderEvent(lastTime, endTime, value, endValue))
    }

}