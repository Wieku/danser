package me.wieku.danser.ui.mainmenu

import me.wieku.danser.graphics.drawables.DanserCoin
import me.wieku.framework.animation.Glider
import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.graphics.drawables.containers.ColorContainer
import me.wieku.framework.graphics.drawables.containers.Container
import me.wieku.framework.graphics.drawables.containers.ParallaxContainer
import me.wieku.framework.math.Easing
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import org.joml.Vector2f
import org.joml.Vector4f

class ButtonSystem() : ParallaxContainer() {

    private var coinPos = Glider(0.5f)

    private var clicked = false

    private var coin: DanserCoin
    private var left: Container
    private var buttons: RightButtonsContainer

    constructor(inContext: ButtonSystem.() -> Unit) : this() {
        inContext()
    }

    init {
        parallaxAmount = 1f / 60
        addChild(
            ColorContainer {
                color = Vector4f(0.2f, 0.2f, 0.2f, 1f)
                scale = Vector2f(1f, 0f)
                color.w = 0f
                fillMode = Scaling.StretchY
                origin = Origin.CentreRight
                anchor = Origin.None
            }.also { left = it },
            RightButtonsContainer().also { buttons = it },
            DanserCoin().apply {
                scale = Vector2f(0.6f)
                anchor = Origin.Custom
                customAnchor = Vector2f(0.5f, 0.5f) //0.3f
                fillMode = Scaling.Fit
                inheritColor = false
                action = { click() }
            }.also { coin = it }
        )
    }

    fun click() {
        clicked = !clicked
        left.addTransform(
            Transform(
                TransformType.ScaleVector,
                clock.currentTime,
                clock.currentTime+300f,
                Vector2f(1f, if(clicked) 0f else 0.12f),
                Vector2f(1f, if(clicked) 0.12f else 0f),
                Easing.InOutQuad
            )
        )
        left.addTransform(
            Transform(
                TransformType.Fade,
                clock.currentTime,
                clock.currentTime+300f,
                if(clicked) 0f else 1f,
                if(clicked) 1f else 0f,
                Easing.InOutQuad
            )
        )

        buttons.show(clicked)

        coinPos.addEvent(clock.currentTime+300f, if(clicked) 0.3f else 0.5f, Easing.InOutQuad)
        coin.addTransform(
            Transform(
                TransformType.Scale,
                clock.currentTime,
                clock.currentTime+300f,
                if(clicked) 0.6f else 0.35f,
                if(clicked) 0.35f else 0.6f,
                Easing.InOutQuad
            )
        )
    }

    override fun update() {
        coinPos.update(clock.currentTime)
        coin.customAnchor.set(coinPos.value, 0.5f)

        super.update()

        if (coin.wasUpdated) {
            left.position.set(coin.drawSize).mul(0.10f, 0.5f).add(coin.drawPosition)
            left.size.x = buttons.position.x
            left.invalidate()
            left.update()

            buttons.position.set(coin.drawSize).mul(0.9f, 0.5f).add(coin.drawPosition)
            buttons.size.x = drawSize.x - buttons.position.x
            buttons.invalidate()
            buttons.update()
        }
    }

    fun beginIntroSequence() {
        coin.addTransform(
            Transform(
                TransformType.Color4,
                clock.currentTime,
                clock.currentTime + 1300,
                Vector4f(0f),
                Vector4f(1f)
            )
        )
        coin.addTransform(
            Transform(
                TransformType.Scale,
                clock.currentTime,
                clock.currentTime + 1000,
                2.5f,
                1.5f,
                Easing.Linear
            )
        )
        coin.addTransform(
            Transform(
                TransformType.Scale,
                clock.currentTime + 1000,
                clock.currentTime + 1300,
                1.5f,
                0.6f,
                Easing.OutBack
            )
        )
    }

}

//03
//0.35