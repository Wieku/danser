package me.wieku.danser.ui.screens

import me.wieku.danser.beatmap.Beatmap
import me.wieku.danser.beatmap.TrackManager
import me.wieku.danser.graphics.drawables.DanserCoin
import me.wieku.danser.graphics.drawables.SideFlashes
import me.wieku.danser.ui.mainmenu.ButtonSystem
import me.wieku.danser.ui.mainmenu.RightButtonsContainer
import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.di.bindable.BindableListener
import me.wieku.framework.graphics.drawables.containers.*
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.TextSprite
import me.wieku.framework.gui.screen.Screen
import me.wieku.framework.input.InputManager
import me.wieku.framework.math.Easing
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import org.joml.Vector2f
import org.joml.Vector4f
import org.koin.core.KoinComponent
import org.koin.core.inject

class MainMenu : Screen(), KoinComponent {

    private var buttonSystem: ButtonSystem
    private val beatmapBindable: Bindable<Beatmap?> by inject()

    private var colorContainer: ColorContainer

    init {
        val bgSprite = Sprite("menu/backgrounds/background-1.png") {
            fillMode = Scaling.Fill
            anchor = Origin.Centre
        }

        addChild(
            ParallaxContainer {
                parallaxAmount = 1f/40
                fillMode = Scaling.Stretch
                addChild(
                    BlurredContainer {
                        fillMode = Scaling.Stretch
                        scale = Vector2f(1.2f)
                        blurAmount = 0.3f
                        anchor = Origin.Custom
                        customAnchor = Vector2f(0.5f, 0.5f)
                        addChild(
                            bgSprite
                        )
                    }/*.also { background = it }*/
                )
            },
            ButtonSystem {
                fillMode = Scaling.Stretch
            }.also { buttonSystem = it }
        )

        val text = TextSprite("Exo2") {
            text = "Nothing is playing"
            scaleToSize = true
            drawShadow = true
            shadowOffset = Vector2f(0.15f, 0.15f)
            fillMode = Scaling.FillY
            anchor = Origin.CentreRight
            origin = Origin.CentreRight
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

        addChild(
            YogaContainer {
                isRoot = true
                origin = Origin.TopLeft
                anchor = Origin.TopLeft
                fillMode = Scaling.Stretch
                scale = Vector2f(1f, 0.04f)
                addChild(
                    ColorContainer {
                        fillMode = Scaling.Stretch
                        color = Vector4f(0.2f, 0.2f, 0.2f, 1f)
                    },
                    YogaContainer {
                        yogaSizePercent = Vector2f(100f)
                        yogaPaddingPercent = Vector4f(20f)
                        addChild(
                            YogaContainer {
                                yogaSizePercent = Vector2f(100f)
                                addChild(
                                    text
                                )
                            }
                        )
                    }
                )
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
        TrackManager.update()

        super.update()

    }

    override fun onEnter(previous: Screen?) {
        super.onEnter(previous)

        TrackManager.start()

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
    }


}