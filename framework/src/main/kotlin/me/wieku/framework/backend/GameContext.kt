package me.wieku.framework.backend

import me.wieku.framework.audio.BassSystem
import me.wieku.framework.configuration.FrameworkConfig
import me.wieku.framework.input.InputManager
import me.wieku.framework.utils.FpsLimiter
import org.joml.Vector2i
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.util.concurrent.locks.ReentrantLock
import kotlin.Exception
import kotlin.system.exitProcess

abstract class GameContext {

    init {
        startKoin {}
    }

    /*var contextRunning = false
        private set*/

    var focused = true
        protected set

    val contextSize = Vector2i(0, 0)

    private val updateLimiter = FpsLimiter()

    protected val fpsLimiter = FpsLimiter()

    protected val inputLimiter = FpsLimiter(1000)

    protected abstract fun startContext()

    protected abstract fun startGraphicsContext()

    protected abstract fun closeContext()

    protected abstract fun handleGameCycle(): Boolean

    protected abstract fun createInputManager(): InputManager

    private lateinit var inputManager: InputManager

    protected var game: Game? = null

    private val lock = ReentrantLock()

    fun start(game: Game) {
        val frameworkModule = module {
            single { this@GameContext }
        }

        FrameworkConfig.openConfig()

        loadKoinModules(frameworkModule)
        startContext()

        inputManager = createInputManager()

        loadKoinModules(
            module {
                single {
                    inputManager
                }
            }
        )

        BassSystem.initSystem()

        this.game = game

        var keepRunning = true

        val renderingThread = Thread {
            lock.lock()
            startGraphicsContext()
            game.setup()
            lock.unlock()
            while (!handleGameCycle() && keepRunning) {
                fpsLimiter.fps = (if (focused) FrameworkConfig.foregroundFPS else FrameworkConfig.backgroundFPS).value

                if(!FrameworkConfig.vSync.value)
                    fpsLimiter.sync()
                game.graphicsClock.updateClock()
            }
            keepRunning = false
        }
        renderingThread.setUncaughtExceptionHandler { _, e ->
            println("*** Uncaught exception in rendering thread! ***")
            e.printStackTrace()
            keepRunning = false
        }
        renderingThread.start()

        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            println("*** Uncaught exception in update thread! ***")
            e.printStackTrace()
            keepRunning = false
        }

        Thread {
            lock.lock()
            while (keepRunning) {
                game.update()
                updateLimiter.fps = (if (focused) FrameworkConfig.updateRate else FrameworkConfig.updateRateBackground).value
                updateLimiter.sync()
                game.updateClock.updateClock()
            }
        }.start()

        try {
            while (keepRunning) {
                inputManager.update()
                inputLimiter.sync()
            }
        } catch (e: Throwable) {
            println("*** Uncaught exception in input thread! ***")
            e.printStackTrace()
        }

        keepRunning = false
        game.dispose()
        closeContext()

        FrameworkConfig.saveConfig()

        exitProcess(0)
    }

}