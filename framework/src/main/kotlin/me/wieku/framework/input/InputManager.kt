package me.wieku.framework.input

import me.wieku.framework.input.event.HoverEvent
import me.wieku.framework.input.event.HoverLostEvent
import org.joml.Vector2i
import java.util.*
import kotlin.collections.ArrayList

abstract class InputManager {

    private val pos = Vector2i()

    var inputHandler: InputHandler? = null

    var lastHovered = ArrayList<InputHandler>()

    var hovered = ArrayList<InputHandler>()

    var inputQueue = ArrayDeque<InputHandler>()

    abstract fun getPosition(): Vector2i

    fun updatePosition(x: Int, y: Int) {
        pos.set(x, y)
        updateHover()
    }

    private fun updateHover() {
        //println("hover update")
        inputHandler?.let { inputHandler ->
            inputQueue.clear()
            inputHandler.buildInputQueue(pos, inputQueue)

            lastHovered.clear()
            lastHovered.addAll(hovered)
            hovered.clear()

            inputQueue.forEach { handler ->
                hovered.add(handler)
                lastHovered.remove(handler)

                if (!handler.isHovered) {
                    handler.isHovered = true
                    if (handler.trigger(HoverEvent(pos))) {
                        return@forEach
                    }

                }
            }

            lastHovered.forEach { handler ->
                handler.isHovered = false
                handler.trigger(HoverLostEvent(pos))
            }

        }
    }

    abstract fun update()

}