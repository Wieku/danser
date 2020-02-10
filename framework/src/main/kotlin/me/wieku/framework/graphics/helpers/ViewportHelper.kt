package me.wieku.framework.graphics.helpers

import org.joml.Vector4i
import org.lwjgl.opengl.GL33.*
import java.util.*

object ViewportHelper {

    private val temp = IntArray(4)
    private val viewportStack = ArrayDeque<Vector4i>()

    fun pushViewport(x: Int, y: Int, width: Int, height: Int) {
        glGetIntegerv(GL_VIEWPORT, temp)
        viewportStack.push(Vector4i(temp[0], temp[1], temp[2], temp[3]))

        glViewport(x, y, width, height)
    }

    fun pushViewport(width: Int, height: Int) = pushViewport(0, 0, width, height)

    fun popViewport() {
        val viewport = viewportStack.pop() ?: Vector4i(0, 0, 1, 1)
        glViewport(viewport.x, viewport.y, viewport.z, viewport.w)
    }

    fun clearViewportStack() {
        viewportStack.clear()
    }
}