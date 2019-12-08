package me.wieku.framework.gui.screen

import me.wieku.framework.graphics.containers.Container
import me.wieku.framework.math.Scaling
import java.util.*

class ScreenCache : Container() {

    init {
        fillMode = Scaling.Stretch
    }

    private val stack = ArrayDeque<Screen>()

    fun push(screen: Screen) {

        val current = if (stack.isNotEmpty()) stack.peek() else null

        current?.let {
            current.onExit(screen)
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
        next.onEnter(current)
        stack.push(next)
        addChild(next)
    }

}