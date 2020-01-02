package me.wieku.danser.ui.mainmenu.music

import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.audio.SampleStore
import me.wieku.framework.graphics.drawables.containers.ColorContainer
import me.wieku.framework.graphics.drawables.containers.YogaContainer
import me.wieku.framework.graphics.drawables.sprite.TextSprite
import me.wieku.framework.input.event.ClickEvent
import me.wieku.framework.input.event.HoverEvent
import me.wieku.framework.input.event.HoverLostEvent
import me.wieku.framework.math.Easing
import me.wieku.framework.math.Scaling
import org.joml.Vector2f
import org.joml.Vector4f
import org.koin.core.KoinComponent
import org.koin.core.inject

class ControlButton(_icon: String): YogaContainer(), KoinComponent {

    private val sampleStore: SampleStore by inject()

    private lateinit var highlight: ColorContainer
    private lateinit var text: TextSprite

    var icon: String
        get() = text.text
        set(value) {
            text.text = value
        }

    init {
        yogaHeightPercent = 100f
        yogaPaddingPercent = Vector4f(20f)
        yogaAspectRatio = 1f
        addChild(
            ColorContainer {
                fillMode = Scaling.Stretch
                color.w = 0f
            }.also { highlight = it },
            YogaContainer {
                yogaSizePercent = Vector2f(100f)
                addChild(
                    TextSprite("FontAwesome-Solid") {
                        text = _icon
                        scaleToSize = true
                        fillMode = Scaling.FillY
                    }.also { text = it }
                )
            }
        )
    }

    constructor(_icon: String, inContext: ControlButton.() -> Unit) : this(_icon) {
        inContext()
    }

    override fun onClick(e: ClickEvent): Boolean {
        sampleStore.getResourceOrLoad("menu/menuhit.wav").play()
        return super.onClick(e)
    }

    override fun onHover(e: HoverEvent): Boolean {
        sampleStore.getResourceOrLoad("menu/menuclick.wav").play()
        highlight.addTransform(
            Transform(
                TransformType.Fade,
                clock.currentTime,
                clock.currentTime + 200f,
                highlight.color.w,
                0.2f
            )
        )

        text.addTransform(
            Transform(
                TransformType.Scale,
                clock.currentTime,
                clock.currentTime + 200f,
                text.scale.x,
                1.1f
            )
        )

        return super.onHover(e)
    }

    override fun onHoverLost(e: HoverLostEvent): Boolean {
        highlight.addTransform(
            Transform(
                TransformType.Fade,
                clock.currentTime,
                clock.currentTime + 200f,
                highlight.color.w,
                0f
            )
        )
        text.addTransform(
            Transform(
                TransformType.Scale,
                clock.currentTime,
                clock.currentTime + 200f,
                text.scale.x,
                1f
            )
        )
        return super.onHoverLost(e)
    }

}