package me.wieku.framework.gui.screen

import me.wieku.framework.graphics.drawables.containers.Container
import me.wieku.framework.math.Scaling
import me.wieku.framework.utils.synchronized
import java.util.*

class ScreenCache : Container() {

    init {
        fillMode = Scaling.Stretch
    }

    private val stack = ArrayDeque<Screen>()

    private val screenChangeListeners = ArrayDeque<(previous: Screen?, next: Screen?) -> Unit>()

    fun push(screen: Screen) {

        val current = if (stack.isNotEmpty()) stack.peek() else null

        current?.let {
            current.onSuspend(screen)
            current.drawForever = false
        }

        screen.drawForever = true
        screen.onEnter(current)
        stack.push(screen)
        insertChild(screen, 0)
    }

    fun back() {

        val current = if (stack.isNotEmpty()) stack.pop() else null
        val next = stack.peek()

        current?.let {
            current.onExit(next)
            current.drawForever = false
        }

        next.drawForever = true
        next.onResume(current)
        stack.push(next)
        addChild(next)

        screenChangeListeners.synchronized {
            forEach { it(current, next) }
        }
    }

    operator fun plusAssign(listener: (previous: Screen?, next: Screen?) -> Unit) {
        screenChangeListeners.synchronized {
            add(listener)
        }
    }

}