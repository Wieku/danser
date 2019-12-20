package me.wieku.danser.ui.mainmenu

import me.wieku.framework.animation.Glider
import me.wieku.framework.font.BitmapFont
import me.wieku.framework.graphics.drawables.containers.ColorContainer
import me.wieku.framework.graphics.drawables.containers.YogaContainer
import me.wieku.framework.graphics.drawables.sprite.TextSprite
import me.wieku.framework.input.event.HoverEvent
import me.wieku.framework.input.event.HoverLostEvent
import me.wieku.framework.math.Easing
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import me.wieku.framework.math.vector2fRad
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import org.joml.Vector2f
import org.joml.Vector4f
import org.lwjgl.util.yoga.Yoga

class MenuButton(text: String, icon: String, font: String, color: Vector4f, private val isFirst: Boolean = false): YogaContainer() {

    private val container: YogaContainer

    private var lastAR = if(isFirst) 1.5f else 1.2f
    private val glider = Glider(lastAR)

    init {
        glider.easing = Easing.OutElastic

        yogaAspectRatio = glider.value
        yogaFlexShrink = 0f
        yogaPaddingPercent = Vector4f(0f)
        yogaFlexDirection = Yoga.YGFlexDirectionColumn
        addChild(
            ColorContainer {
                fillMode = Scaling.Stretch
                shearX = 0.2f
                this.color = color
            },
            YogaContainer {
                fillMode = Scaling.None
                isRoot = true
                yogaFlexDirection = Yoga.YGFlexDirectionColumn
                yogaDirection = Yoga.YGDirectionLTR
                yogaAlignItems = Yoga.YGAlignCenter
                addChild(
                    YogaContainer {
                        yogaSizePercent = Vector2f(100f)
                        yogaAspectRatio = 1f
                        yogaFlexShrink = 1f
                        yogaFlexGrow = 1f
                        yogaJustifyContent = Yoga.YGJustifyCenter
                        yogaAlignItems = Yoga.YGAlignFlexEnd
                        yogaPaddingPercent = Vector4f(0f, 0f, 0f, 10f)
                        addChild(
                            YogaContainer {
                                yogaSizePercent = Vector2f(50f)
                                yogaAspectRatio = 1f
                                addChild(
                                    TextSprite(font) {
                                        this.text = icon
                                        scaleToSize = true
                                        origin = Origin.Centre
                                        drawFromBottom = true
                                        fillMode = Scaling.Fit
                                    }
                                )
                            }
                        )
                    },
                    YogaContainer {
                        yogaFlexShrink = 0f
                        yogaFlexGrow = 1f
                        yogaSizePercent = Vector2f(100f, 20f)
                        yogaPaddingPercent = Vector4f(10f)
                        addChild(
                            YogaContainer {
                                yogaSizePercent = Vector2f(100f)
                                addChild(
                                    TextSprite("Exo2") {
                                        this.text = text
                                        scaleToSize = true
                                        origin = Origin.Centre
                                        fillMode = Scaling.Fit
                                    }
                                )
                            }
                        )
                    }
                )
            }.also { container = it }

        )
    }

    override fun update() {
        glider.update(clock.currentTime)
        super.update()
        if (lastAR != glider.value) {
            yogaAspectRatio = glider.value
            parent!!.invalidate()
            lastAR = glider.value
        }
    }

    override fun updateDrawable() {
        super.updateDrawable()
        container.size.set(drawSize.x*(1f-vector2fRad(Math.PI.toFloat()/2*(1-0.2f), drawSize.y/2).x/drawSize.x), drawSize.y)
    }

    override fun OnHover(e: HoverEvent): Boolean {
        glider.addEvent(clock.currentTime+300f, if(isFirst) 2.1f else 1.8f)
        return false
    }

    override fun OnHoverLost(e: HoverLostEvent): Boolean {
        glider.addEvent(clock.currentTime+300f, if(isFirst) 1.5f else 1.2f)
        return false
    }

}