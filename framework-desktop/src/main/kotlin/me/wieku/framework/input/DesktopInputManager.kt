package me.wieku.framework.input

import me.wieku.framework.backend.DesktopContext
import org.lwjgl.glfw.GLFW.*

class DesktopInputManager(private val glfwContext: DesktopContext) : InputManager() {

    private val xBuff = doubleArrayOf(0.0)
    private val yBuff = doubleArrayOf(0.0)

    init {
        glfwSetMouseButtonCallback(glfwContext.windowHandle) { _, button, action, _ ->
            updateCursorAction(MouseButton.values()[button], InputAction.values()[action])
        }
    }

    override fun update() {
        glfwPollEvents()
        glfwGetCursorPos(glfwContext.windowHandle, xBuff, yBuff)
        updatePosition(xBuff[0].toFloat(), yBuff[0].toFloat())
    }

}