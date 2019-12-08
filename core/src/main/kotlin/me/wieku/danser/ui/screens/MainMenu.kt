package me.wieku.danser.ui.screens

import me.wieku.danser.beatmap.Beatmap
import me.wieku.danser.beatmap.BeatmapManager
import me.wieku.danser.graphics.drawables.DanserCoin
import me.wieku.danser.graphics.drawables.SideFlashes
import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.di.bindable.BindableListener
import me.wieku.framework.font.BitmapFont
import me.wieku.framework.graphics.containers.BlurredContainer
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.TextSprite
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.gui.screen.Screen
import me.wieku.framework.gui.screen.ScreenCache
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import org.joml.Vector2f
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File

class MainMenu : Screen(), KoinComponent {

    val beatmapBindable: Bindable<Beatmap?> by inject()

    init {

        val flashes = SideFlashes()
        flashes.fillMode = Scaling.Stretch

        val coin = DanserCoin()
        coin.scale = Vector2f(0.6f)
        coin.fillMode = Scaling.Fit

        //println(System.getenv("localappdata") + "/osu!/Songs/" + beatmap.beatmapSet.directory + File.separator + beatmap.beatmapMetadata.backgroundFile)

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

        addChild(
            BlurredContainer {
                fillMode = Scaling.Stretch
                addChild(bgSprite)
            }
        )

        addChild(flashes)
        addChild(coin)

        val font = BitmapFont(FileHandle("assets/fonts/Exo2/Exo2.fnt", FileType.Classpath))

        val text = TextSprite(font) {
            text = "Nothing is playing"
            fontSize = 32f
            anchor = Origin.TopRight
            origin = Origin.TopRight
        }

        beatmapBindable.addListener(object : BindableListener<Beatmap?> {
            override fun valueChanged(bindable: Bindable<Beatmap?>) {
                if (bindable.value != null) {
                    text.text = String.format(
                        "%s - %s",
                        bindable.value!!.beatmapMetadata.artist,
                        bindable.value!!.beatmapMetadata.title
                    )
                }
            }
        })

        addChild(text)
    }

    override fun update() {
        invalidate()
        super.update()
    }

    override fun onEnter(previous: Screen?) {
        super.onEnter(previous)
        addTransform(
            Transform(
                TransformType.Fade,
                clock.currentTime,
                clock.currentTime + 1000,
                0f,
                1f
            ), false
        )

        var beatmap = BeatmapManager.beatmapSets.filter {
            /*it.metadata!!.title.contains("Windfall", true) &&*/ it.beatmaps.filter { bmap -> bmap.beatmapInfo.version == "Anto & Nuvolina's Extra" }
            .isNotEmpty()
        }[0].beatmaps.filter { bmap -> bmap.beatmapInfo.version == "Anto & Nuvolina's Extra" }[0]

        beatmap.loadTrack()

        beatmap.getTrack().play(0.1f)
        beatmap.getTrack().setPosition(beatmap.beatmapMetadata.previewTime.toFloat() / 1000 - 5)

        beatmapBindable.value = beatmap
    }


}