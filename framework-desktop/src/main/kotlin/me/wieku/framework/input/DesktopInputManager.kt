package me.wieku.framework.input

import me.wieku.framework.backend.DesktopContext
import org.joml.Vector2i
import org.lwjgl.glfw.GLFW.*

class DesktopInputManager(private val glfwContext: DesktopContext): InputManager() {
    private val tempVector2i = Vector2i()

    private val xBuff = doubleArrayOf(0.0)
    private val yBuff = doubleArrayOf(0.0)

    init {
        /*glfwSetCursorPosCallback(glfwContext.windowHandle) { _, x, y ->
            updatePosition(x.toInt(), y.toInt())
        }*/

        glfwSetMouseButtonCallback(glfwContext.windowHandle) { _, button, action, _ ->
            updateCursorAction(MouseButton.values()[button], InputAction.values()[action])
        }
    }

    override fun getPosition(): Vector2i {
        glfwGetCursorPos(glfwContext.windowHandle, xBuff, yBuff)
        return tempVector2i.set(xBuff[0].toInt(), yBuff[0].toInt())
    }

    override fun update() {
        glfwPollEvents()
        getPosition()
        updatePosition(tempVector2i.x, tempVector2i.y)
    }

}