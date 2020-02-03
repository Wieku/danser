package me.wieku.danser.ui.mainmenu.music

import me.wieku.danser.beatmap.Beatmap
import me.wieku.danser.graphics.drawables.triangles.Triangles
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.graphics.drawables.containers.ColorContainer
import me.wieku.framework.graphics.drawables.containers.Container
import me.wieku.framework.graphics.drawables.containers.RoundedEdgeContainer
import me.wieku.framework.graphics.drawables.containers.YogaContainer
import me.wieku.framework.graphics.drawables.sprite.TextSprite
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import org.joml.Vector2f
import org.joml.Vector4f
import org.koin.core.inject
import org.lwjgl.util.yoga.Yoga

class MusicOverlay(): Container() {

    private lateinit var textContainer: YogaContainer
    private val beatmapBindable: Bindable<Beatmap?> by inject()

    private val beatmap = Bindable<Beatmap?>(null)

    private var lastWidth = 100f

    private val text = TextSprite("Exo2") {
        text = "Nothing is playing"
        scaleToSize = true
        drawShadow = true
        shadowOffset = Vector2f(0.15f, 0.15f)
        fillMode = Scaling.FillY
        anchor = Origin.CentreRight
        origin = Origin.CentreRight
    }

    constructor(inContext: MusicOverlay.() -> Unit) : this() {
        inContext()
    }

    init {
        beatmap.bindTo(beatmapBindable)

        beatmap.addListener { _, newBeatmap, _ ->
            newBeatmap?.let {
                text.text = String.format(
                    "%s - %s",
                    it.beatmapMetadata.artist,
                    it.beatmapMetadata.title
                )
            }
        }

        addChild(
            YogaContainer {
                isRoot = true
                origin = Origin.TopLeft
                anchor = Origin.TopLeft
                fillMode = Scaling.Stretch
                scale = Vector2f(1f, 0.04f)
                yogaJustifyContent = Yoga.YGJustifyFlexEnd

                addChild(
                    YogaContainer {
                        yogaHeightPercent = 100f
                        yogaFlexShrink = 0f
                        yogaPaddingPercent = Vector4f(20f)
                        addChild(
                            RoundedEdgeContainer {
                                fillMode = Scaling.Stretch
                                color.w = 0.5f
                                radius = 0.5f
                                addChild(
                                    ColorContainer {
                                        fillMode = Scaling.Stretch
                                        color = Vector4f(0.2f, 0.2f, 0.2f, 1f)
                                    },
                                    Triangles {
                                        fillMode = Scaling.Stretch
                                        minSize = 1f
                                        maxSize = 2f
                                        spawnRate = 2f
                                        colorDark = Vector4f(0.054f, 0.054f, 0.054f, 1f)
                                        colorLight = Vector4f(0.2f, 0.2f, 0.2f, 1f)
                                    }
                                )
                            },
                            YogaContainer {
                                yogaSizePercent = Vector2f(100f)

                                addChild(
                                    text
                                )
                            }.also { textContainer = it }
                        )
                    }
                )
            },
            YogaContainer {
                isRoot = true
                origin = Origin.TopLeft
                anchor = Origin.Custom
                customAnchor = Vector2f(0f, 0.04f)
                fillMode = Scaling.Stretch
                scale = Vector2f(1f, 0.04f)
                yogaJustifyContent = Yoga.YGJustifyFlexEnd

                addChild(
                    MusicControl()
                )
            }
        )

    }

    override fun update() {
        if (text.drawSize.x != lastWidth) {
            lastWidth = text.drawSize.x
            textContainer.yogaWidth = lastWidth

            invalidate()
        }

        super.update()
    }

}