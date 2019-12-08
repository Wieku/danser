package me.wieku.danser

import me.wieku.danser.beatmap.Beatmap
import me.wieku.danser.beatmap.BeatmapManager
import me.wieku.danser.graphics.drawables.DanserCoin
import me.wieku.danser.graphics.drawables.SideFlashes
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.font.BitmapFont
import me.wieku.framework.game.Game
import me.wieku.framework.game.GameContext
import me.wieku.framework.graphics.containers.BlurredContainer
import me.wieku.framework.graphics.containers.Container
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.graphics.drawables.sprite.TextSprite
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
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
import java.io.File

class Danser: Game(), KoinComponent {

    lateinit var batch: SpriteBatch
    val bindable = Bindable<Beatmap?>(null)
    val camera = Camera()
    lateinit var mainContainer: Container

    val gameContext: GameContext by inject()
    val lastContextSize = Vector2i()

    lateinit var fpsSprite: TextSprite

    private val counter = FpsCounter()

    override fun setup() {
        batch = SpriteBatch()

        BeatmapManager.loadBeatmaps(System.getenv("localappdata") + "\\osu!\\Songs")

        var beatmap = BeatmapManager.beatmapSets.filter { /*it.metadata!!.title.contains("Windfall", true) &&*/ it.beatmaps.filter { bmap -> bmap.beatmapInfo.version == "Anto & Nuvolina's Extra" }.isNotEmpty() }[0].beatmaps.filter { bmap -> bmap.beatmapInfo.version == "Anto & Nuvolina's Extra" }[0]

        bindable.value = beatmap

        val danserModule = module {
            single { bindable }
            single { updateClock }
        }


        loadKoinModules(danserModule)

        beatmap.loadTrack()

        beatmap.getTrack().play(0.1f)
        beatmap.getTrack().setPosition(beatmap.beatmapMetadata.previewTime.toFloat()/1000-5)

        val flashes = SideFlashes()
        flashes.fillMode = Scaling.Stretch

        val coin = DanserCoin()
        coin.scale = Vector2f(0.6f)
        coin.fillMode = Scaling.Fit

        mainContainer = Container {
            size = Vector2f(1920f, 1080f)
            origin = Origin.TopLeft
        }

        println(System.getenv("localappdata") + "/osu!/Songs/" + beatmap.beatmapSet.directory + File.separator + beatmap.beatmapMetadata.backgroundFile)

        val bgSprite = Sprite {
            texture = Texture(
                FileHandle(
                    "assets/textures/menu/backgrounds/background-1.png",
                    FileType.Classpath
                ),
                4
            ).region
            size = Vector2f(texture!!.getWidth(), texture!!.getHeight())
            fillMode = Scaling.Fill
            anchor = Origin.Centre
        }

        mainContainer.addChild(
            BlurredContainer{
                fillMode = Scaling.Stretch
                addChild(bgSprite)
            }
        )

        mainContainer.addChild(flashes)
        mainContainer.addChild(coin)

        val font = BitmapFont(FileHandle("assets/fonts/Exo2/Exo2.fnt", FileType.Classpath))

        fpsSprite = TextSprite(font) {
            text = "0.00 ms"
            fontSize = 16f
            anchor = Origin.BottomLeft
            origin = Origin.BottomLeft
        }

        mainContainer.addChild(fpsSprite)

        camera.setViewportF(0, 0, 1920, 1080, true)
        camera.update()
    }

    override fun update() {
        fpsSprite.text = String.format("%.2f ms", counter.frameTime)
        bindable.value!!.getTrack().update()
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