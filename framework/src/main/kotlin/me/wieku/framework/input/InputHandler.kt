package me.wieku.framework.input

import me.wieku.framework.input.event.*
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
            is HoverEvent -> return onHover(e)
            is HoverLostEvent -> return onHoverLost(e)
            is MouseDownEvent -> return onMouseDown(e)
            is MouseUpEvent -> return onMouseUp(e)
            is ClickEvent -> return onClick(e)
        }
        return false
    }

    var isHovered = false

    var action: (() -> Unit)? = null

    open fun onHover(e: HoverEvent) = false

    open fun onHoverLost(e: HoverLostEvent) = false

    open fun onMouseDown(e: MouseDownEvent) = false

    open fun onMouseUp(e: MouseUpEvent) = false

    open fun onClick(e: ClickEvent): Boolean {
        action?.let { it() }
        return false
    }

}