package me.wieku.framework.game

import me.wieku.framework.backend.WindowMode
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL33.*

class DesktopContext: GameContext() {

    private var windowHandle: Long = 0

    override fun setWindowTitleC(title: String) {
        glfwSetWindowTitle(windowHandle, title)
    }

    override fun setPositionC(x: Int, y: Int) {
        glfwSetWindowPos(windowHandle, x, y)
    }

    override fun setWindowSizeC(width: Int, height: Int) {
        glfwSetWindowSize(windowHandle, width, height)
    }

    override fun setWindowModeC(windowMode: WindowMode) {
        glfwSetWindowAttrib(windowHandle, GLFW_DECORATED, if(windowMode == WindowMode.Windowed) GLFW_TRUE else GLFW_FALSE)
        when(windowMode) {
            WindowMode.Windowed -> {
                glfwSetWindowMonitor(windowHandle, 0, windowPositionX, windowPositionY, windowWidth, windowHeight, GLFW_DONT_CARE)
            }
            WindowMode.Borderless -> {
                val monitorHandle = glfwGetPrimaryMonitor()
                val monitorMode = glfwGetVideoMode(monitorHandle)!!
                glfwSetWindowMonitor(windowHandle, 0, 0, 0, monitorMode.width(), monitorMode.height(), monitorMode.refreshRate())
            }
            WindowMode.Fullscreen -> {
                val monitorHandle = glfwGetPrimaryMonitor()
                val monitorMode = glfwGetVideoMode(monitorHandle)!!
                glfwSetWindowMonitor(windowHandle, monitorHandle, 0, 0, windowWidth, windowHeight, monitorMode.refreshRate())
            }
        }
    }

    override fun setVSyncC(vSync: Boolean) {
        glfwSwapInterval(if (vSync) 1 else 0)
    }

    override fun startContext() {
        GLFWErrorCallback.createPrint(System.err).set()
        glfwInit()
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, 1)
        glfwWindowHint(GLFW_SAMPLES, 4)
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)

        windowHandle = glfwCreateWindow(windowWidth, windowHeight, windowTitle, 0, 0)
        setWindowModeC(windowMode)
        glfwMakeContextCurrent(windowHandle)
        glfwSwapInterval(0)

        GL.createCapabilities()
        glEnable(GL_MULTISAMPLE)

        glfwShowWindow(windowHandle)
    }

    override fun closeContext() {
        glfwDestroyWindow(windowHandle)
    }

    override fun handleGameCycle(): Boolean {
        val shouldGetClosed = glfwWindowShouldClose(windowHandle)
        if (!shouldGetClosed) {
            glViewport(0, 0, windowWidth, windowHeight)
            glClearColor(0f, 0f, 0f, 1.0f)
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            game!!.draw()

            glfwPollEvents()
            glfwSwapBuffers(windowHandle)
        }

        return shouldGetClosed
    }

}