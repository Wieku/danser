package me.wieku.danser.graphics.drawables

import me.wieku.danser.beatmap.Beatmap
import me.wieku.framework.audio.Track
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.graphics.containers.Container
import me.wieku.framework.graphics.drawables.Drawable
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.math.Easings
import me.wieku.framework.math.Origin
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

class SideFlashes : Container(), KoinComponent {

    val beatmapBindable: Bindable<Beatmap> by inject()

    private var lastTime = 0L
    private var deltaSum = 0f

    private var volume = 0f
    private var volumeAverage = 0f

    private var progress = 0f
    private var lastProgress = 0f

    private var beatProgress = 0f

   // private val coinSpriteBottom: Sprite
    private val flashLeft: Sprite
    private val flashRight: Sprite

    init {
        /*size = Vector2f(800f, 800f).mul(0.7f)
        position = Vector2f(400f, 400f)*/

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
            flipX = true
            color.w = 1f
            anchor = Origin.CentreLeft
            origin = Origin.CentreLeft
        }

        flashRight = Sprite {
            texture = flashTexture.region
            fillMode = Scaling.FillY
            color.w = 1f
            anchor = Origin.CentreRight
            origin = Origin.CentreRight
        }

        addChild(flashLeft, flashRight)
    }

    override fun update() {
        val time = System.nanoTime()
        if (lastTime == 0L) {
            lastTime = time
        }
        val delta = ((time - lastTime) / 1000000f)

        deltaSum += delta

        if (deltaSum >= 1000f / 60) {

            volume = beatmapBindable.value!!.getTrack().getLevelCombined()
            volumeAverage = volumeAverage * 0.9f + volume * 0.1f

            deltaSum -= 1000f / 60
        }

        val bTime = (beatmapBindable.value!!.getTrack().getPosition() * 1000).toLong()

        val timingPoint = beatmapBindable.value!!.timing.getPointAt(bTime)

        //coinSpriteTop.color.w = if(timingPoint.kiai) 0.12f else 0.3f

        val bProg = ((bTime - timingPoint.time) / timingPoint.baseBpm)

        beatProgress = bProg - floor(bProg)

        val vprog = 1 - ((volume - volumeAverage) / 0.5f)
        val pV = min(1.0f, max(0.0f, 1.0f - (vprog * 0.5f + beatProgress * 0.5f)))

        val ratio = 0.5f.pow(delta / 16.6666666666667f)

        progress = lastProgress * ratio + (pV) * (1 - ratio)
        lastProgress = progress

        //coinBottom.scale.set(1.05f - Easings.OutQuad(progress * 0.05f))
        //coinSpriteTop.scale.set(1.05f + Easings.OutQuad(progress * 0.03f))

        lastTime = time

        invalidate()
        super.update()
    }

    override fun dispose() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}