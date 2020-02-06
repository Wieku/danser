package me.wieku.danser

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.wieku.danser.beatmap.Beatmap
import me.wieku.danser.graphics.drawables.CursorWithTrail
import me.wieku.danser.graphics.drawables.triangles.Triangles
import me.wieku.danser.ui.common.FPSStatistics
import me.wieku.danser.ui.screens.LoadingScreen
import me.wieku.framework.animation.Glider
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
import me.wieku.framework.math.Easing
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import me.wieku.framework.math.color.Color
import me.wieku.framework.math.view.Camera
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import org.joml.Vector2f
import org.joml.Vector2i
import org.joml.Vector4f
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
    lateinit var screenChangeTriangles: Triangles
    private val trianglesSpeed = Glider(4f)
    private val trianglesSpawnRate = Glider(1f)

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

        mainContainer.addChild(
            Triangles {
                fillMode = Scaling.Stretch
                startOnScreen = false
                spawnEnabled = false
                colorDark = Color(0.054f, 1f)
                colorLight = Color(0.2f, 1f)
                color.w = 0.5f
                maxSize = 0.4f
            }.also { screenChangeTriangles = it }
        )

        screenCache += { previous, next ->
            if (previous != null && previous !is LoadingScreen) {
                trianglesSpeed.addEvent(updateClock.currentTime, updateClock.currentTime + 200, 2f, 4f, Easing.OutQuad)
                trianglesSpawnRate.addEvent(updateClock.currentTime, updateClock.currentTime + 100, 0.1f, 2f, Easing.OutQuad)
                trianglesSpawnRate.addEvent(updateClock.currentTime + 100, updateClock.currentTime + 200, 2f, 0.1f, Easing.OutQuad)
                screenChangeTriangles.spawnEnabled = true
                GlobalScope.launch {
                    delay(120L)
                    screenChangeTriangles.spawnEnabled = false
                }

            }

        }

        screenCache.push(LoadingScreen())
    }

    override fun update() {
        bindable.value?.getTrack()?.update()

        trianglesSpeed.update(updateClock.currentTime)
        trianglesSpawnRate.update(updateClock.currentTime)

        screenChangeTriangles.baseVelocity = trianglesSpeed.value
        screenChangeTriangles.spawnRate = trianglesSpawnRate.value

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