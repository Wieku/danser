package me.wieku.danser.graphics.drawables

import me.wieku.danser.beatmap.Beatmap
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.graphics.containers.Container
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.math.Easings
import me.wieku.framework.math.Scaling
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import org.joml.Vector2f
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class DanserCoin : Container(), KoinComponent {

    val beatmapBindable: Bindable<Beatmap> by inject()

    private var deltaSum = 0f

    private var volume = 0f
    private var volumeAverage = 0f

    private var progress = 0f
    private var lastProgress = 0f

    private var beatProgress = 0f

    private val coinBottom: Container
    private val coinTop: Sprite

    init {

        val bottomTexture = Texture(
            FileHandle(
                "assets/coin-base.png",
                FileType.Classpath
            ),
            4
        )

        val overlayTexture = Texture(
            FileHandle(
                "assets/coin-overlay.png",
                FileType.Classpath
            ),
            4
        )

        coinBottom = Container {
            fillMode = Scaling.Fill
            addChild(
                Sprite {
                    texture = bottomTexture.region
                    fillMode = Scaling.Fit
                    inheritScale = true
                    color.w = 1f
                },
                Triangles {
                    fillMode = Scaling.Fit
                    inheritScale = true
                    scale = Vector2f(0.95f)
                },
                Sprite {
                    texture = overlayTexture.region
                    fillMode = Scaling.Fit
                    inheritScale = true
                    color.w = 1f
                }
            )
        }

        coinTop = Sprite {
            texture = overlayTexture.region
            fillMode = Scaling.FillY
            color.w = 0.3f
        }


        addChild(coinBottom, coinTop)
    }

    override fun update() {

        deltaSum += clock.time.frameTime

        if (deltaSum >= 1000f / 60) {

            volume = beatmapBindable.value!!.getTrack().getLevelCombined()
            volumeAverage = volumeAverage * 0.9f + volume * 0.1f

            deltaSum -= 1000f / 60
        }

        val bTime = (beatmapBindable.value!!.getTrack().getPosition() * 1000).toLong()

        val timingPoint = beatmapBindable.value!!.timing.getPointAt(bTime)

        coinTop.color.w = if (timingPoint.kiai) 0.12f else 0.3f

        val bProg = ((bTime - timingPoint.time) / timingPoint.baseBpm)

        beatProgress = bProg - floor(bProg)

        val vprog = 1 - ((volume - volumeAverage) / 0.5f)
        val pV = min(1.0f, max(0.0f, 1.0f - (vprog * 0.5f + beatProgress * 0.5f)))

        val ratio = 0.5f.pow(clock.time.frameTime / 16.6666666666667f)

        progress = lastProgress * ratio + (pV) * (1 - ratio)
        lastProgress = progress

        coinBottom.scale.set(1.05f - Easings.OutQuad(progress * 0.05f))
        coinTop.scale.set(1.05f + Easings.OutQuad(progress * 0.03f))

        invalidate()
        super.update()
    }

    override fun dispose() {}
}