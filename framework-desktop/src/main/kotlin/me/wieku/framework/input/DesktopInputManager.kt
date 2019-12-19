package me.wieku.framework.input

import me.wieku.framework.backend.DesktopContext
import org.joml.Vector2i
import org.lwjgl.glfw.GLFW.*

class DesktopInputManager(private val glfwContext: DesktopContext): InputManager() {
    private val tempVector2i = Vector2i()

    private val xBuff = doubleArrayOf(0.0)
    private val yBuff = doubleArrayOf(0.0)

    override fun getPosition(): Vector2i {
        glfwGetCursorPos(glfwContext.windowHandle, xBuff, yBuff)
        return tempVector2i.set(xBuff[0].toInt(), yBuff[0].toInt())
    }

    override fun update() {
        glfwPollEvents()
    }

}