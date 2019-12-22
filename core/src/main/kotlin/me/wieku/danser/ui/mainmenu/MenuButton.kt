package me.wieku.danser.ui.mainmenu

import me.wieku.danser.beatmap.Beatmap
import me.wieku.framework.animation.Glider
import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.font.BitmapFont
import me.wieku.framework.graphics.drawables.Drawable
import me.wieku.framework.graphics.drawables.containers.ColorContainer
import me.wieku.framework.graphics.drawables.containers.YogaContainer
import me.wieku.framework.graphics.drawables.sprite.TextSprite
import me.wieku.framework.input.event.ClickEvent
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
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.lwjgl.util.yoga.Yoga
import kotlin.math.floor
import kotlin.math.max

class MenuButton(private val text: String, icon: String, font: String, color: Vector4f, private val isFirst: Boolean = false): YogaContainer(), KoinComponent {

    private lateinit var iconDrawable: Drawable
    private val beatmapBindable: Bindable<Beatmap?> by inject()

    private var lastBeatLength = 0f
    private var lastBeatStart = 0f
    private var lastProgress = 0

    private val container: YogaContainer
    private val flash: ColorContainer

    private var lastAR = 0.01f
    private val glider = Glider(0.01f)
    private val jGlider = Glider(0f)

    private var armed = false

    init {
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
            ColorContainer {
                fillMode = Scaling.Stretch
                shearX = 0.2f
                this.color.w = 0f
            }.also { flash = it },
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
                                        drawShadow = true
                                        shadowOffset = Vector2f(0f, 0.1f)
                                        origin = Origin.Centre
                                        anchor = Origin.Custom
                                        customAnchor = Vector2f(0.5f)
                                        drawFromBottom = true
                                        fillMode = Scaling.Fit
                                    }.also { iconDrawable = it }
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
                                        this.text = this@MenuButton.text
                                        drawShadow = true
                                        shadowOffset = Vector2f(0f, 0.1f)
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

        if (beatmapBindable.value != null) {
            val bTime = (beatmapBindable.value!!.getTrack().getPosition() * 1000).toLong()

            val timingPoint = beatmapBindable.value!!.timing.getPointAt(bTime)

            if (timingPoint.baseBpm != lastBeatLength) {
                lastProgress = -1
                lastBeatLength = timingPoint.baseBpm
                lastBeatStart = timingPoint.time.toFloat()
            }

            val beatLength = max(300f, lastBeatLength)
            val bProg = ((bTime - lastBeatStart) / lastBeatLength)
            val progress = floor(bProg).toInt()

            if (progress > lastProgress) {
                if (isHovered) {
                    flash.addTransform(
                        Transform(
                            TransformType.Fade,
                            clock.currentTime,
                            clock.currentTime + beatLength,
                            0.25f,
                            0f
                        )
                    )
                    iconDrawable.addTransform(
                        Transform(
                            TransformType.Rotate,
                            clock.currentTime,
                            clock.currentTime + beatLength,
                            if (progress%2==0) 0.2f else -0.2f,
                            if (progress%2==0) -0.2f else 0.2f
                        )
                    )
                    jGlider.addEvent(clock.currentTime , clock.currentTime + beatLength/2, 0f, 0.3f, Easing.OutQuad)
                    jGlider.addEvent(clock.currentTime+ beatLength/2 , clock.currentTime + beatLength, 0.3f, 0f, Easing.InQuad)
                }
                lastProgress++
            }
        }



        glider.update(clock.currentTime)
        jGlider.update(clock.currentTime)
        //println(jGlider.value)
        iconDrawable.customAnchor.set(0.5f, 0.5f-jGlider.value)
        iconDrawable.invalidate()
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

    override fun onHover(e: HoverEvent): Boolean {
        if (!armed) return false
        glider.addEvent(clock.currentTime+300f, if(isFirst) 2.2f else 1.8f, Easing.OutElastic)
        return false
    }

    override fun onHoverLost(e: HoverLostEvent): Boolean {
        if (!armed) return false
        glider.addEvent(clock.currentTime+300f, if(isFirst) 1.6f else 1.2f, Easing.OutElastic)
        iconDrawable.transforms.clear()
        iconDrawable.addTransform(
            Transform(
                TransformType.Rotate,
                clock.currentTime,
                clock.currentTime + 300f,
                iconDrawable.rotation,
                0f
            )
        )
        return false
    }

    fun show(clicked: Boolean) {
        armed = clicked
        addTransform(
            Transform(
                TransformType.Fade,
                clock.currentTime,
                clock.currentTime + 300f,
                if (clicked) 0f else 1f,
                if (clicked) 1f else 0f,
                Easing.OutQuad
            )
        )
        if (clicked) {
            glider.addEvent(clock.currentTime+300f, if(isFirst) 1.6f else 1.2f, Easing.OutQuad)
        } else {
            glider.addEvent(clock.currentTime+300f, 0.01f, Easing.OutQuad)
        }
    }

    override fun onClick(e: ClickEvent): Boolean {
        println("Clicked $text")
        return super.onClick(e)
    }

}