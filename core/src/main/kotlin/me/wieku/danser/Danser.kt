package me.wieku.danser

import me.wieku.danser.beatmap.Beatmap
import me.wieku.danser.graphics.drawables.CursorWithTrail
import me.wieku.danser.ui.common.FPSStatistics
import me.wieku.danser.ui.screens.LoadingScreen
import me.wieku.framework.audio.SampleStore
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.font.FontStore
import me.wieku.framework.backend.Game
import me.wieku.framework.backend.GameContext
import me.wieku.framework.graphics.drawables.containers.Container
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.graphics.textures.store.TextureStore
import me.wieku.framework.gui.screen.ScreenCache
import me.wieku.framework.input.InputManager
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import me.wieku.framework.math.view.Camera
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import org.joml.Vector2f
import org.joml.Vector2i
import org.koin.core.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.inject
import org.koin.dsl.module

class Danser : Game(), KoinComponent {

    private val inputManager: InputManager by inject()

    private val gameContext: GameContext by inject()
    private val lastContextSize = Vector2i()

    val bindable = Bindable<Beatmap?>(null)

    lateinit var batch: SpriteBatch

    private val camera = Camera()
    lateinit var mainContainer: Container
    lateinit var screenCache: ScreenCache

    lateinit var fontStore: FontStore
    lateinit var textureStore: TextureStore

    override fun setup() {
        batch = SpriteBatch()

        screenCache = ScreenCache()

        fontStore = FontStore()
        fontStore.addResource("Exo2", FileHandle("assets/fonts/Exo2/Exo2.fnt", FileType.Classpath))
        fontStore.addResource("FontAwesome-Regular", FileHandle("assets/fonts/FontAwesome/FontAwesome-Regular.fnt", FileType.Classpath))
        fontStore.addResource("FontAwesome-Solid", FileHandle("assets/fonts/FontAwesome/FontAwesome-Solid.fnt", FileType.Classpath))
        fontStore.addResource("FontAwesome-Brands", FileHandle("assets/fonts/FontAwesome/FontAwesome-Brands.fnt", FileType.Classpath))

        textureStore = TextureStore()

        val danserModule = module {
            single { bindable }
            single { updateClock }
            single { screenCache }
            single { textureStore }
            single { fontStore }
            single { SampleStore() }
        }

        loadKoinModules(danserModule)

        mainContainer = Container {
            size = Vector2f(1920f, 1080f)
            origin = Origin.TopLeft
        }

        inputManager.inputHandler = mainContainer

        mainContainer.addChild(screenCache)

        mainContainer.addChild(
            FPSStatistics(this) {
                fillMode = Scaling.Stretch
                scale = Vector2f(1f, 0.08f)
                anchor = Origin.BottomRight
                origin = Origin.BottomRight
            }
        )

        mainContainer.addChild(CursorWithTrail())

        camera.setViewportF(0, 0, 1920, 1080, true)
        camera.update()

        screenCache.push(LoadingScreen())
    }

    override fun update() {
        bindable.value?.getTrack()?.update()

        if (lastContextSize != gameContext.contextSize) {
            lastContextSize.set(gameContext.contextSize)
            mainContainer.size.set(lastContextSize)
            mainContainer.invalidate()
            camera.setViewportF(0, 0, lastContextSize.x, lastContextSize.y)
            camera.update()
        }

        mainContainer.update()
    }

    override fun draw() {
        batch.camera = camera
        batch.begin()

        mainContainer.draw(batch)

        batch.end()
    }

    override fun dispose() {

    }
}