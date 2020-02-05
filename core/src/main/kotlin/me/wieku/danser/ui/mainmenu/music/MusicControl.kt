package me.wieku.danser.ui.mainmenu.music

import me.wieku.danser.beatmap.Beatmap
import me.wieku.danser.beatmap.TrackManager
import me.wieku.danser.graphics.drawables.triangles.Triangles
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.graphics.drawables.containers.ColorContainer
import me.wieku.framework.graphics.drawables.containers.YogaContainer
import me.wieku.framework.math.Scaling
import org.joml.Vector4f
import org.koin.core.inject
import org.lwjgl.util.yoga.Yoga
import kotlin.math.min

class MusicControl: YogaContainer() {

    private val beatmapBindable: Bindable<Beatmap?> by inject()

    private lateinit var playPauseButton: ControlButton

    init {
        yogaHeightPercent = 100f
        setYogaWidthAuto()
        yogaFlexShrink = 1f
        yogaFlexGrow = 0f
        yogaFlexDirection = Yoga.YGFlexDirectionColumn

        useScissor = true

        addChild(
            ColorContainer {
                fillMode = Scaling.Stretch
                color = Vector4f(0.2f, 0.2f, 0.2f, 0.5f)
            },
            Triangles {
                fillMode = Scaling.Stretch
                minSize = 1f
                maxSize = 2f
                spawnRate = 2f
                colorDark = Vector4f(0.054f, 0.054f, 0.054f, 0.5f)
                colorLight = Vector4f(0.2f, 0.2f, 0.2f, 0.5f)
            },
            YogaContainer {
                yogaHeightPercent = 100f
                setYogaWidthAuto()
                yogaFlexShrink = 1f
                yogaFlexGrow = 1f

                addChild(
                    ControlButton("\uf048") {
                        action = {
                            TrackManager.backwards()
                        }
                    },
                    ControlButton("\uf04b") {
                        action = {
                            beatmapBindable.value?.let {
                                val track = it.getTrack()
                                if (track.isRunning) {
                                    track.pause()
                                } else {
                                    track.resume()
                                }
                            }
                        }
                    }.also { playPauseButton = it },
                    ControlButton("\uf04d") {
                        action = {
                            beatmapBindable.value?.let {
                                val track = it.getTrack()
                                track.pause()
                                track.setPosition(0f)
                            }
                        }
                    },
                    ControlButton("\uf051") {
                        action = {
                            TrackManager.forward()
                        }
                    }
                )
            },
            ProgressBar()
        )
    }

    override fun update() {

        val isPlaying = beatmapBindable.value?.getTrack()?.isRunning ?: false

        playPauseButton.icon = if (isPlaying) "\uf04c" else "\uf04b"

        super.update()
        maskingInfo.radius = min(drawSize.x, drawSize.y) * 0.5f / 2
    }

}