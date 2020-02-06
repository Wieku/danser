package me.wieku.danser.ui.mainmenu

import me.wieku.danser.graphics.drawables.DanserCoin
import me.wieku.danser.graphics.drawables.triangles.TriangleDirection
import me.wieku.danser.graphics.drawables.triangles.Triangles
import me.wieku.framework.animation.Glider
import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.graphics.drawables.containers.ColorContainer
import me.wieku.framework.graphics.drawables.containers.Container
import me.wieku.framework.graphics.drawables.containers.ParallaxContainer
import me.wieku.framework.math.Easing
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import me.wieku.framework.math.color.Color
import org.joml.Vector2f
import org.joml.Vector4f

class ButtonSystem() : ParallaxContainer() {

    private var coinPos = Glider(0.5f)

    private var clicked = false

    private var coin: DanserCoin
    private var background: Container
    private var buttons: RightButtonsContainer

    private var introFinish = 0f
    private var introFinished = false

    constructor(inContext: ButtonSystem.() -> Unit) : this() {
        inContext()
    }

    init {
        parallaxAmount = 1f / 80
        addChild(
            ColorContainer {
                color = Color(0.2f, 0.2f, 0.2f, 1f)
                scale = Vector2f(1f, 0f)
                color.w = 0f
                fillMode = Scaling.Stretch
                addChild(
                    Triangles {
                        useScissor = true
                        maskingInfo.blendRange = 0f
                        trianglesMinimum = 100
                        fillMode = Scaling.Stretch
                        triangleDirection = TriangleDirection.Down
                        colorDark = Color(0.9f, 0.9f, 0.9f, 1f)
                        reactive = false
                    }
                )
            }.also { background = it },
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
        background.addTransform(
            Transform(
                TransformType.ScaleVector,
                clock.currentTime + if (clicked) 100f else 0f,
                clock.currentTime + 400f,
                Vector2f(1f, if(clicked) 0f else 0.12f),
                Vector2f(1f, if(clicked) 0.12f else 0f),
                Easing.InOutQuad
            )
        )
        background.addTransform(
            Transform(
                TransformType.Fade,
                clock.currentTime + if (clicked) 100f else 0f,
                clock.currentTime+400f,
                if(clicked) 0f else 1f,
                if(clicked) 1f else 0f,
                Easing.InOutQuint
            )
        )

        buttons.show(clicked)

        coinPos.addEvent(clock.currentTime+400f, if(clicked) 0.3f else 0.5f, Easing.InOutQuad)
        coin.addTransform(
            Transform(
                TransformType.Scale,
                clock.currentTime,
                clock.currentTime + if (clicked) 300f else 400f,
                if(clicked) 0.5f else 0.3f,
                if(clicked) 0.3f else 0.5f,
                Easing.InOutQuint
            )
        )
    }

    override fun update() {
        if (!introFinished && clock.currentTime >= introFinish) {
            coin.introFinished()
            introFinished = true
        }
        coinPos.update(clock.currentTime)
        coin.customAnchor.set(coinPos.value, 0.5f)

        super.update()

        if (coin.wasUpdated) {
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
                0.5f,
                Easing.OutBack
            )
        )

        coin.introBegin()
        introFinish = clock.currentTime + 1300
    }

}

//03
//0.35