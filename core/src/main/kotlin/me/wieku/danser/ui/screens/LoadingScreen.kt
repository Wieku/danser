package me.wieku.danser.ui.screens

import me.wieku.danser.beatmap.Beatmap
import me.wieku.danser.beatmap.BeatmapManager
import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.font.BitmapFont
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

class LoadingScreen : Screen(), KoinComponent {

    private val text: TextSprite
    private val beatmapBindable: Bindable<Beatmap?> by inject()
    private val stack: ScreenCache by inject()
    private val mainMenu: MainMenu
    private val coin: Sprite

    init {

        /*val overlayTexture = Texture(
            FileHandle(
                "assets/textures/menu/coin-overlay.png",
                FileType.Classpath
            ),
            4
        )*/

        coin = Sprite("menu/coin-overlay.png") {
            fillMode = Scaling.Fit
            scale = Vector2f(0.66f)
        }

        text = TextSprite("Exo2") {
            text = "Loading awesomeness"
            fontSize = 32f
            anchor = Origin.Custom
            customAnchor = Vector2f(0.5f, 0.9f)
            //origin = Origin.Centre
        }

        addChild(coin)
        addChild(text)
        addChild(TextSprite("Exo2") {
            text = "Early build. Please visit github.com/Wieku/danser for more info"
            fontSize = 16f
            anchor = Origin.TopLeft
            origin = Origin.TopLeft
        })

        mainMenu = MainMenu()
    }

    override fun onEnter(previous: Screen?) {
        super.onEnter(previous)
        addTransform(
            Transform(
                TransformType.Fade,
                clock.currentTime,
                clock.currentTime + 2000,
                0f,
                1f
            ), false
        )

        Thread {
            BeatmapManager.loadBeatmaps(System.getenv("localappdata") + "\\osu!\\Songs")
            stack.push(mainMenu)
        }.start()
    }

    override fun onExit(next: Screen?) {
        super.onExit(next)
        /*coin.addTransform(
            Transform(
                TransformType.Scale,
                clock.currentTime,
                clock.currentTime + 500,
                0.66f,
                3f
            ), false
        )

        text.addTransform(
            Transform(
                TransformType.Fade,
                clock.currentTime,
                clock.currentTime + 250,
                1f,
                0f
            ), false
        )*/

        addTransform(
            Transform(
                TransformType.Fade,
                clock.currentTime,
                clock.currentTime + 500,
                1f,
                0f
            ), false
        )
    }

}