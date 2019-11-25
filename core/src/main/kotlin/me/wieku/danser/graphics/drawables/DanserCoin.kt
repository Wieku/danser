package me.wieku.danser.graphics.drawables

import me.wieku.danser.beatmap.Beatmap
import me.wieku.framework.audio.Track
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.graphics.drawables.Drawable
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.math.Easings
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import org.joml.Vector2f
import kotlin.math.floor

class DanserCoin(val beatmapBindable: Bindable<Beatmap>, private val batch: SpriteBatch): Drawable {

    private var lastTime = 0L
    private var deltaSum = 0f

    private var volume = 0f
    private var volumeAverage = 0f

    private var progress = 0f
    private var lastProgress = 0f

    private var beatProgress = 0f

    //private val batch = SpriteBatch()
    private val coinSprite: Sprite

    init {
        println("Preopoen")
        coinSprite = Sprite(
            Texture(
                FileHandle(
                    "assets/coinbig.png",
                    FileType.Classpath
                )
            ).region, 1f, 1f, Vector2f(0.5f, 0.5f)
        )
        println("postopen")
    }

    override fun draw() {
        val time = System.nanoTime()
        val delta = ((time-lastTime)/1000000f)

        deltaSum += delta

        if(deltaSum >= 1000f/60) {

            volume = beatmapBindable.value!!.getTrack().getLevelCombined()

            volumeAverage = volumeAverage * 0.9f + volume * 0.1f

            deltaSum -= 1000f/60
        }

        val bTime = (beatmapBindable.value!!.getTrack().getPosition()*1000).toLong()

        val timingPoint = beatmapBindable.value!!.timing.getPointAt(bTime)

        val bProg = ((bTime-timingPoint.time)/timingPoint.realBpm)

        beatProgress = bProg - floor(bProg)

        val vprog = 1 - ((volume - volumeAverage) / 0.5f)
        val pV = Math.min(1.0f, Math.max(0.0f, 1.0f-(vprog*0.5f+beatProgress*0.5f)))

        val ratio = Math.pow(0.5, delta/16.6666666666667).toFloat()

        progress = lastProgress*ratio + (pV)*(1-ratio)
        lastProgress = progress

        batch.begin()

        coinSprite.scale.set((1.05f - Easings.OutQuad(progress*0.05f)) /** scl * pl.hover*/)
        coinSprite.color.w = 1.0f

        batch.draw(coinSprite)

        coinSprite.scale.set((1.05f + Easings.OutQuad(progress*0.03f)) /** scl * pl.hover*/)
        coinSprite.color.w = 0.3f

        batch.draw(coinSprite)

        batch.end()

        lastTime = time
    }

    override fun dispose() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}