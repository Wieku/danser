package me.wieku.framework.backend

import me.wieku.framework.configuration.FrameworkConfig
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.graphics.helpers.ViewportHelper
import me.wieku.framework.input.DesktopInputManager
import me.wieku.framework.input.InputManager
import org.joml.Vector2i
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL33.*


class DesktopContext: GameContext() {
    internal var windowHandle: Long = 0

    private var minimized = false

    private val pos1 = Vector2i()
    private val pos2 = Vector2i()

    private fun setWindowTitle(title: String) {
        glfwSetWindowTitle(windowHandle, title)
    }

    private fun setPosition(position: Vector2i) {
        glfwSetWindowPos(windowHandle, position.x, position.y)
    }

    private fun setWindowSize(width: Int, height: Int) {
        if (FrameworkConfig.windowMode.value == WindowMode.Maximized) return
        glfwSetWindowSize(windowHandle, width, height)
        contextSize.set(width, height)
    }

    private fun setWindowMode(windowMode: WindowMode) {
        logger.info("Window mode changed: ${FrameworkConfig.windowMode.value} -> $windowMode")
        glfwSetWindowAttrib(windowHandle, GLFW_DECORATED, if(windowMode == WindowMode.Windowed || windowMode == WindowMode.Maximized) GLFW_TRUE else GLFW_FALSE)

        when(windowMode) {
            WindowMode.Windowed -> {
                val windowPos = FrameworkConfig.windowPosition.value
                val windowSize = FrameworkConfig.windowSize.value
                contextSize.set(windowSize)
                glfwSetWindowMonitor(windowHandle, 0, windowPos.x, windowPos.y, windowSize.x, windowSize.y, GLFW_DONT_CARE)
            }
            WindowMode.Borderless -> {
                val monitorHandle = glfwGetPrimaryMonitor()
                val monitorMode = glfwGetVideoMode(monitorHandle)!!
                contextSize.set(monitorMode.width(), monitorMode.height())
                glfwSetWindowMonitor(windowHandle, 0, 0, 0, monitorMode.width(), monitorMode.height(), monitorMode.refreshRate())
            }
            WindowMode.Fullscreen -> {
                val monitorHandle = glfwGetPrimaryMonitor()
                val monitorMode = glfwGetVideoMode(monitorHandle)!!
                val fullScreenSize = FrameworkConfig.fullScreenResolution.value
                contextSize.set(fullScreenSize)
                glfwSetWindowMonitor(windowHandle, monitorHandle, 0, 0, fullScreenSize.x, fullScreenSize.y, monitorMode.refreshRate())
            }
            WindowMode.Maximized -> {
                glfwMaximizeWindow(windowHandle)
                val x = intArrayOf(0)
                val y = intArrayOf(0)
                glfwGetWindowSize(windowHandle, x, y)
                contextSize.set(x[0], y[0])
            }
        }
    }

    private fun setVSync(vSync: Boolean) {
        glfwSwapInterval(if (vSync) 1 else 0)
    }

    private fun generateEventBindings() {

        val vectorListeners = { _: Vector2i, newValue: Vector2i, bindable: Bindable<Vector2i> ->
                when (bindable) {
                    FrameworkConfig.fullScreenResolution -> {
                        if (FrameworkConfig.windowMode.value == WindowMode.Fullscreen)
                            setWindowSize(newValue.x, newValue.y)
                    }
                    FrameworkConfig.windowSize -> {
                        if (FrameworkConfig.windowMode.value == WindowMode.Windowed)
                            setWindowSize(newValue.x, newValue.y)
                    }
                }
            }

        FrameworkConfig.fullScreenResolution.addListener(vectorListeners)
        FrameworkConfig.windowSize.addListener(vectorListeners)

        FrameworkConfig.windowTitle.addListener { _, newValue, _ ->
                setWindowTitle(newValue)
            }

        FrameworkConfig.windowMode.addListener { _, newValue, _ ->
                setWindowMode(newValue)
            }

        FrameworkConfig.vSync.addListener { _, newValue, _ ->
                setVSync(newValue)
            }
    }

    private fun generateGLFWCallbacks() {
        glfwSetWindowFocusCallback(windowHandle) { _, focused ->
            this.focused = focused
        }

        glfwSetWindowIconifyCallback(windowHandle) { _, iconified ->
            minimized = iconified

            if (minimized)
                FrameworkConfig.windowPosition.value = Vector2i(pos2)
        }

        glfwSetWindowSizeCallback(windowHandle) { _, width, height ->
            if (FrameworkConfig.windowMode.value != WindowMode.Maximized && !minimized)
                FrameworkConfig.windowSize.value = Vector2i(width, height)
        }

        glfwSetWindowPosCallback(windowHandle) { _, x, y ->
            pos2.set(pos1)
            pos1.set(x, y)
            if (FrameworkConfig.windowMode.value != WindowMode.Maximized)
                FrameworkConfig.windowPosition.value = Vector2i(x, y)
        }

        glfwSetWindowMaximizeCallback(windowHandle) { _, maximized ->
            if(maximized) FrameworkConfig.windowPosition.value = Vector2i(pos2)
            FrameworkConfig.windowMode.value = if (maximized) WindowMode.Maximized else WindowMode.Windowed }

    }

    override fun startContext() {
        pos1.set(FrameworkConfig.windowPosition.value)
        GLFWErrorCallback.createPrint(System.err).set()

        glfwInit()
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, 1)
        glfwWindowHint(GLFW_SAMPLES, FrameworkConfig.msaa.value)
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)

        windowHandle = glfwCreateWindow(100, 100, FrameworkConfig.windowTitle.value, 0, 0)
        setWindowMode(WindowMode.Windowed)
        setWindowMode(FrameworkConfig.windowMode.value)
    }

    override fun startGraphicsContext() {
        glfwMakeContextCurrent(windowHandle)
        setVSync(FrameworkConfig.vSync.value)
        GL.createCapabilities()

        val glvendor = GL11.glGetString(GL11.GL_VENDOR)
        val glrenderer = GL11.glGetString(GL11.GL_RENDERER)
        val glversion = GL11.glGetString(GL11.GL_VERSION)
        val glslversion = GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION)

        val extensions = StringBuilder()

        val numExtensions = GL11.glGetInteger(GL30.GL_NUM_EXTENSIONS)
        for (i in 0 until numExtensions) {
            extensions.append(GL30.glGetStringi(GL11.GL_EXTENSIONS, i))
            extensions.append(" ")
        }

        logger.info("GL Vendor:     $glvendor")
        logger.info("GL Renderer:   $glrenderer")
        logger.info("GL Version:    $glversion")
        logger.info("GLSL Version:  $glslversion")
        logger.info("GL Extensions: $extensions")

        glEnable(GL_MULTISAMPLE)
        glfwShowWindow(windowHandle)
        glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_HIDDEN)

        generateEventBindings()
        generateGLFWCallbacks()
    }

    override fun closeContext() {
        glfwDestroyWindow(windowHandle)
    }

    override fun createInputManager(): InputManager = DesktopInputManager(this)

    override fun handleGameCycle(): Boolean {
        val shouldGetClosed = glfwWindowShouldClose(windowHandle)
        if (!shouldGetClosed) {
            ViewportHelper.pushViewport(contextSize.x, contextSize.y)
            glClearColor(0f, 0f, 0f, 1.0f)
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            game!!.draw()

            glfwSwapBuffers(windowHandle)
            ViewportHelper.clearViewportStack()
        }

        return shouldGetClosed
    }

}