package me.wieku.danser.ui.screens

import me.wieku.danser.beatmap.TrackManager
import me.wieku.danser.graphics.drawables.SideFlashes
import me.wieku.danser.ui.common.background.MenuBackground
import me.wieku.danser.ui.mainmenu.ButtonSystem
import me.wieku.danser.ui.mainmenu.music.MusicOverlay
import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.graphics.drawables.containers.*
import me.wieku.framework.gui.screen.Screen
import me.wieku.framework.input.MouseButton
import me.wieku.framework.input.event.MouseUpEvent
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import org.joml.Vector2f
import org.koin.core.KoinComponent

class MainMenu : Screen(), KoinComponent {

    private var buttonSystem: ButtonSystem
    private var colorContainer: ColorContainer

    init {
        origin = Origin.Custom
        customOrigin = Vector2f(0.5f)

        addChild(
            MenuBackground {
                parallaxAmount = 1f / 40
                fillMode = Scaling.Stretch
            },
            ButtonSystem {
                fillMode = Scaling.Stretch
            }.also { buttonSystem = it },
            MusicOverlay {
                fillMode = Scaling.Stretch
            },
            SideFlashes().apply {
                fillMode = Scaling.Stretch
            },
            ColorContainer {
                fillMode = Scaling.Stretch
                drawForever = false
                color.w = 0f
            }.also { colorContainer = it }
        )
        super.color.w = 0f
    }

    override fun update() {
        TrackManager.update(clock.currentTime)

        super.update()

    }

    override fun onEnter(previous: Screen?) {
        super.onEnter(previous)

        if (previous is LoadingScreen) {
            TrackManager.start(clock.currentTime)

            buttonSystem.beginIntroSequence()

            colorContainer.addTransform(
                Transform(
                    TransformType.Fade,
                    clock.currentTime + 1300,
                    clock.currentTime + 1800,
                    1f,
                    0f
                )
            )

            addTransform(
                Transform(
                    TransformType.Fade,
                    clock.currentTime + 1000,
                    clock.currentTime + 1300,
                    0.5f,
                    1f
                )
            )
        } else {
            addTransform(
                Transform(
                    TransformType.Fade,
                    clock.currentTime,
                    clock.currentTime + 500,
                    0f,
                    1f
                )
            )
        }
    }

    override fun onExit(next: Screen?) {
        super.onExit(next)
        addTransform(
            Transform(
                TransformType.Fade,
                clock.currentTime,
                clock.currentTime + 200,
                1f,
                0f
            )
        )
    }

    override fun onSuspend(next: Screen?) {
        super.onSuspend(next)

        addTransform(
            Transform(
                TransformType.Fade,
                clock.currentTime,
                clock.currentTime + 200,
                1f,
                0f
            )
        )

    }

    override fun onMouseUp(e: MouseUpEvent): Boolean {
        when(e.button) {
            MouseButton.ButtonForward -> TrackManager.forward()
            MouseButton.ButtonBack -> TrackManager.backwards()
            else -> {}
        }

        return super.onMouseUp(e)
    }

}