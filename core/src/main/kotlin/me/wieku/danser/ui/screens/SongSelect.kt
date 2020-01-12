package me.wieku.danser.ui.screens

import javafx.scene.transform.Scale
import me.wieku.danser.beatmap.TrackManager
import me.wieku.danser.ui.common.background.MenuBackground
import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.gui.screen.Screen
import me.wieku.framework.input.MouseButton
import me.wieku.framework.input.event.MouseUpEvent
import me.wieku.framework.math.Scaling
import org.joml.Vector2f
import org.koin.core.KoinComponent

class SongSelect(): Screen(), KoinComponent {

    constructor(inContext: SongSelect.() -> Unit) : this() {
        inContext()
    }

    init {

        addChild(
            MenuBackground {
                parallaxAmount = 1f / 40
                fillMode = Scaling.Stretch
                color.w = 0.3f
            }
        )

    }

    override fun update() {
        TrackManager.update(clock.currentTime)

        super.update()
    }

    override fun onEnter(previous: Screen?) {
        super.onEnter(previous)
        show()
    }

    override fun onResume(previous: Screen?) {
        super.onResume(previous)
        show()
    }

    private fun show() {
        scale = Vector2f(1.2f)

        addTransform(
            Transform(
                TransformType.Fade,
                clock.currentTime,
                clock.currentTime + 200,
                0.5f,
                1f
            )
        )

        addTransform(
            Transform(
                TransformType.Scale,
                clock.currentTime,
                clock.currentTime + 200,
                1.2f,
                1f
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