package me.wieku.framework.game

import me.wieku.framework.audio.BassSystem
import me.wieku.framework.backend.WindowMode
import me.wieku.framework.configuration.FrameworkConfig
import me.wieku.framework.utils.FpsLimiter
import org.joml.Vector2i
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.awt.Frame
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

    protected abstract fun startContext()

    protected abstract fun closeContext()

    protected abstract fun handleGameCycle(): Boolean

    protected var game: Game? = null

    fun start(game: Game) {
        val frameworkModule = module {
            single { this@GameContext }
        }

        FrameworkConfig.openConfig()

        loadKoinModules(frameworkModule)
        startContext()
        BassSystem.initSystem()

        this.game = game

        game.setup()

        var keepRunning = true

        var thread = Thread {
            while (keepRunning) {
                game.update()
                updateLimiter.fps = (if (focused) FrameworkConfig.updateRate else FrameworkConfig.updateRateBackground).value
                updateLimiter.sync()
                game.updateClock.updateClock()
            }
        }

        thread.start()

        while (!handleGameCycle()) {
            fpsLimiter.fps = (if (focused) FrameworkConfig.foregroundFPS else FrameworkConfig.backgroundFPS).value

            if(!FrameworkConfig.vSync.value)
                fpsLimiter.sync()
            game.graphicsClock.updateClock()
        }

        keepRunning = false
        game.dispose()
        closeContext()

        FrameworkConfig.saveConfig()

        exitProcess(0)
    }

}