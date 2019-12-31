package me.wieku.danser.ui.common

import me.wieku.framework.animation.Glider
import me.wieku.framework.graphics.drawables.containers.ColorContainer
import me.wieku.framework.graphics.drawables.containers.RoundedEdgeContainer
import me.wieku.framework.graphics.drawables.containers.YogaContainer
import me.wieku.framework.graphics.drawables.sprite.TextSprite
import me.wieku.framework.math.Easing
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import org.joml.Vector2f
import org.joml.Vector4f
import org.lwjgl.util.yoga.Yoga

class StatisticsRow() : YogaContainer() {

    private lateinit var typeText: TextSprite
    private lateinit var dataText: TextSprite
    private lateinit var textContainer: YogaContainer

    private var lastWidth = 100f
    private var targetWidth = 100f
    private var widthGlider = Glider(100f)

    var type: String
        get() = typeText.text
        set(value) {
            typeText.text = value
        }

    var data: String
        get() = dataText.text
        set(value) {
            dataText.text = value
        }

    constructor(inContext: StatisticsRow.() -> Unit) : this() {
        inContext()
    }

    init {
        yogaSizePercent = Vector2f(100f)
        yogaFlexDirection = Yoga.YGFlexDirectionRow
        yogaJustifyContent = Yoga.YGJustifyFlexEnd
        yogaAlignItems = Yoga.YGAlignCenter
        yogaPaddingPercent = Vector4f(5f)

        addChild(
            YogaContainer {
                yogaSizePercent = Vector2f(100f)
                yogaFlexShrink = 1f
                //yogaMarginPercent = Vector4f(0f, 0f, 0.5f, 0f)
                yogaPaddingPercent = Vector4f(10f)
                yogaJustifyContent = Yoga.YGJustifyFlexEnd
                addChild(
                    YogaContainer {
                        addChild(
                            TextSprite("Exo2") {
                                scale = Vector2f(0.7f)
                                scaleToSize = true
                                drawDigitsMonospace = true
                                fillMode = Scaling.FillY
                                anchor = Origin.CentreRight
                                origin = Origin.CentreRight
                                drawShadow = true
                                shadowOffset = Vector2f(0f, 0.1f)
                            }.also { typeText = it }
                        )
                    }
                )
            },
            YogaContainer {
                yogaHeightPercent = 100f
                yogaFlexShrink = 0f
                yogaPaddingPercent = Vector4f(20f)
                addChild(
                    RoundedEdgeContainer {
                        radius = 0.5f
                        fillMode = Scaling.Stretch
                        addChild(
                            ColorContainer {
                                color = Vector4f(0f, 0f, 0f, 0.5f)
                                fillMode = Scaling.Stretch
                            }
                        )
                    },
                    YogaContainer {
                        yogaWidth = 100f
                        addChild(
                            TextSprite("Exo2") {
                                scaleToSize = true
                                drawDigitsMonospace = true
                                fillMode = Scaling.FillY
                                scale = Vector2f(1f)
                                anchor = Origin.CentreRight
                                origin = Origin.CentreRight
                            }.also { dataText = it }
                        )
                    }.also { textContainer = it }
                )
            }
        )
    }

    override fun update() {

        if (targetWidth != dataText.drawSize.x) {

            if (targetWidth > dataText.drawSize.x) {
                widthGlider.addEvent(clock.currentTime + 1000, clock.currentTime+1500, targetWidth, dataText.drawSize.x, Easing.OutQuad)
            } else {
                widthGlider.reset()
                widthGlider.value = dataText.drawSize.x
            }

            targetWidth = dataText.drawSize.x
        }

        widthGlider.update(clock.currentTime)

        if (widthGlider.value != lastWidth) {
            lastWidth = widthGlider.value
            textContainer.yogaWidth = lastWidth

            invalidate()
        }

        super.update()
    }

}