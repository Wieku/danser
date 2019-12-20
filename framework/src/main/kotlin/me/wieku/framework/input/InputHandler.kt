package me.wieku.framework.input

import me.wieku.framework.input.event.HoverEvent
import me.wieku.framework.input.event.HoverLostEvent
import me.wieku.framework.input.event.InputEvent
import org.joml.Vector2i
import java.util.*

abstract class InputHandler {

    abstract fun isCursorIn(cursorPosition: Vector2i): Boolean

    open fun buildInputQueue(cursorPosition: Vector2i, queue: ArrayDeque<InputHandler>) {
        if (isCursorIn(cursorPosition)) {
            queue.push(this)
        }
    }

    open fun trigger(e: InputEvent): Boolean {
        when(e) {
            is HoverEvent -> return OnHover(e)
            is HoverLostEvent -> return OnHoverLost(e)
        }
        return false
    }

    var isHovered = false

    open fun OnHover(e: HoverEvent) = false

    open fun OnHoverLost(e: HoverLostEvent) = false

}