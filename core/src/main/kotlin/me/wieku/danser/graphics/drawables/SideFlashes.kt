package me.wieku.danser.graphics.drawables

import me.wieku.danser.beatmap.Beatmap
import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.graphics.containers.Container
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.math.floor
import kotlin.math.max

class SideFlashes : Container(), KoinComponent {

    private val beatmapBindable: Bindable<Beatmap?> by inject()

    private val flashLeft: Sprite
    private val flashRight: Sprite

    private var lastBeatLength = 0f
    private var lastBeatStart = 0f
    private var lastProgress = 0

    init {

        val flashTexture = Texture(
            FileHandle(
                "assets/flash.png",
                FileType.Classpath
            ),
            4
        )

        flashLeft = Sprite {
            texture = flashTexture.region
            fillMode = Scaling.FillY
            size.x = 0.4f
            flipY = true
            color.w = 0f
            anchor = Origin.CentreLeft
            origin = Origin.CentreLeft
            additive = true
        }

        flashRight = Sprite {
            texture = flashTexture.region
            size.x = 0.4f
            fillMode = Scaling.FillY
            color.w = 0f
            anchor = Origin.CentreRight
            origin = Origin.CentreRight
            additive = true
        }

        addChild(flashLeft, flashRight)
    }

    override fun update() {

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

            if (timingPoint.kiai || timingPoint.timeSignature == 1 ||progress % timingPoint.timeSignature == 0) {
                if (!timingPoint.kiai || progress % 2 == 0) {
                    flashLeft.transforms.clear()
                    flashLeft.addTransform(
                        Transform(
                            TransformType.Fade,
                            clock.currentTime,
                            clock.currentTime + beatLength,
                            0.6f * beatmapBindable.value!!.getTrack().leftChannelLevel,
                            0f
                        ), false
                    )
                }

                if (!timingPoint.kiai || progress % 2 == 1) {
                    flashRight.transforms.clear()
                    flashRight.addTransform(
                        Transform(
                            TransformType.Fade,
                            clock.currentTime,
                            clock.currentTime + beatLength,
                            0.6f * beatmapBindable.value!!.getTrack().rightChannelLevel,
                            0f
                        ), false
                    )
                }
            }
            lastProgress++
        }

        invalidate()
        super.update()
    }

    override fun dispose() {}
}