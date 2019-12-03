package me.wieku.framework.game

import me.wieku.framework.audio.BassSystem
import me.wieku.framework.backend.WindowMode
import me.wieku.framework.utils.FpsLimiter
import org.joml.Vector2i
import org.koin.core.context.startKoin
import org.koin.dsl.module
import kotlin.system.exitProcess

abstract class GameContext {

    init {
        startKoin {
            module {
                single {
                    this@GameContext
                }
            }
        }
    }

    var contextRunning = false
        private set

    var windowTitle: String = "framework basic title"
        set(value) {
            field = value
            if (contextRunning)
                setWindowTitleC(windowTitle)
        }

    protected abstract fun setWindowTitleC(title: String)
    
    private var _windowPositionX = 50
    var windowPositionX: Int
        get() = _windowPositionX
        set(value) {
            _windowPositionX = value
            if (contextRunning)
                setPositionC(_windowPositionX, _windowPositionY)
        }

    private var _windowPositionY = 50
    var windowPositionY: Int
        get() = _windowPositionY
        set(value) {
            _windowPositionY = value
            if (contextRunning)
                setPositionC(_windowPositionX, _windowPositionY)
        }
    
    fun getPosition() = Vector2i(_windowPositionX, _windowPositionY)
    
    protected abstract fun setPositionC(x: Int, y: Int)
    
    fun setPosition(x: Int, y: Int) {
        _windowPositionX = x
        _windowPositionY = y
        if (contextRunning)
            setPositionC(x, y)
    }
    
    protected fun positionChanged(x: Int, y: Int) {
        _windowPositionX = x
        _windowPositionY = y
    }

    private var _windowWidth = 1920
    var windowWidth: Int
        get() = _windowWidth
        set(value) {
            _windowWidth = value
            if (contextRunning)
                setWindowSizeC(_windowWidth, _windowHeight)
        }

    private var _windowHeight = 1080
    var windowHeight: Int
        get() = _windowHeight
        set(value) {
            _windowHeight = value
            if (contextRunning)
                setWindowSizeC(_windowWidth, _windowHeight)
        }
    
    fun getWindowSize() = Vector2i(_windowWidth, _windowHeight)
    
    protected abstract fun setWindowSizeC(width: Int, height: Int)

    fun setWindowSize(width: Int, height: Int) {
        _windowWidth = width
        _windowHeight = height
        if (contextRunning)
            setWindowSizeC(width, height)
    }

    protected fun sizeChanged(width: Int, height: Int) {
        _windowWidth = width
        _windowHeight = height
    }

    private var _windowMode = WindowMode.Fullscreen
    var windowMode: WindowMode
        get() = _windowMode
        set(value) {
            _windowMode = value
            if (contextRunning)
                setWindowModeC(_windowMode)
        }

    protected abstract fun setWindowModeC(windowMode: WindowMode)

    private var _vSync = false
    var vSync: Boolean
        get() = _vSync
        set(value) {
            _vSync = value
            if (contextRunning)
                setVSyncC(_vSync)
        }

    protected abstract fun setVSyncC(vSync: Boolean)

    var updateRate = 60

    var foregroundFPS = 60

    var backgroundFPS = 60


    private val updateLimiter = FpsLimiter()

    private val fpsLimiter = FpsLimiter()

    protected abstract fun startContext()

    protected abstract fun closeContext()

    protected abstract fun handleGameCycle(): Boolean

    protected var game: Game? = null

    fun start(game: Game) {

        startContext()
        BassSystem.initSystem()

        this.game = game

        game.setup()

        var keepRunning = true

        var thread = Thread {
            while (keepRunning) {
                game.update()
                updateLimiter.fps = updateRate
                updateLimiter.sync()
                game.updateClock.updateClock()
            }
        }

        thread.start()

        while (!handleGameCycle());

        keepRunning = false
        game.dispose()
        closeContext()
        exitProcess(0)
    }

}