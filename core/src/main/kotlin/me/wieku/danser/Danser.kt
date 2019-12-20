package me.wieku.danser

import me.wieku.danser.beatmap.Beatmap
import me.wieku.danser.ui.screens.LoadingScreen
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.font.FontStore
import me.wieku.framework.backend.Game
import me.wieku.framework.backend.GameContext
import me.wieku.framework.graphics.drawables.containers.Container
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.graphics.drawables.sprite.TextSprite
import me.wieku.framework.graphics.textures.store.TextureStore
import me.wieku.framework.gui.screen.ScreenCache
import me.wieku.framework.input.InputManager
import me.wieku.framework.math.Origin
import me.wieku.framework.math.view.Camera
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import me.wieku.framework.utils.FpsCounter
import org.joml.Vector2f
import org.joml.Vector2i
import org.koin.core.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.inject
import org.koin.dsl.module

class Danser : Game(), KoinComponent {

    private val inputManager: InputManager by inject()

    lateinit var batch: SpriteBatch
    val bindable = Bindable<Beatmap?>(null)
    val camera = Camera()
    lateinit var mainContainer: Container
    lateinit var screenCahe: ScreenCache
    lateinit var fontStore: FontStore
    lateinit var textureStore: TextureStore

    val gameContext: GameContext by inject()
    val lastContextSize = Vector2i()

    lateinit var fpsSprite: TextSprite

    private val counter = FpsCounter()

    private var deltaSum = 0f

    override fun setup() {
        batch = SpriteBatch()
        screenCahe = ScreenCache()
        fontStore = FontStore()
        fontStore.addResource("Exo2", FileHandle("assets/fonts/Exo2/Exo2.fnt", FileType.Classpath))
        fontStore.addResource("FontAwesome-Regular", FileHandle("assets/fonts/FontAwesome/FontAwesome-Regular.fnt", FileType.Classpath))
        fontStore.addResource("FontAwesome-Solid", FileHandle("assets/fonts/FontAwesome/FontAwesome-Solid.fnt", FileType.Classpath))
        fontStore.addResource("FontAwesome-Brands", FileHandle("assets/fonts/FontAwesome/FontAwesome-Brands.fnt", FileType.Classpath))
        textureStore = TextureStore()

        val danserModule = module {
            single { bindable }
            single { updateClock }
            single { screenCahe }
            single { textureStore }
            single { fontStore }
        }

        loadKoinModules(danserModule)

        mainContainer = Container {
            size = Vector2f(1920f, 1080f)
            origin = Origin.TopLeft
        }

        inputManager.inputHandler = mainContainer

        mainContainer.addChild(screenCahe)

        fpsSprite = TextSprite("Exo2") {
            text = "0.00 ms"
            fontSize = 16f
            drawShadow = true
            shadowOffset = Vector2f(0f, 0.15f)
            drawDigitsMonospace = true
            anchor = Origin.BottomLeft
            origin = Origin.BottomLeft
        }

        mainContainer.addChild(fpsSprite)

        camera.setViewportF(0, 0, 1920, 1080, true)
        camera.update()

        screenCahe.push(LoadingScreen())
    }

    override fun update() {

        deltaSum += updateClock.time.frameTime

        if (deltaSum >= 16.666667f) {
            fpsSprite.text = String.format("%.2f ms, %d FPS", counter.frameTime, counter.fps.toInt())
            deltaSum -= 16.666667f
        }

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
        counter.putSample(graphicsClock.time.frameTime)
        batch.camera = camera
        batch.begin()

        mainContainer.draw(batch)

        batch.end()
    }

    override fun dispose() {

    }
}