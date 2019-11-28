package me.wieku.danser.graphics.drawables

import me.wieku.danser.beatmap.Beatmap
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.graphics.drawables.Drawable
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.math.Origin
import me.wieku.framework.math.vector2fRad
import org.joml.Vector2f
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.math.pow

class Visualizer : Drawable(), KoinComponent {

    private val beatmapBindable: Bindable<Beatmap> by inject()

    private var lastTime = 0L
    private var deltaSum = 0f

    val bars = 200
    val barLength = 600f
    val baseDecay = 0.0024f
    val jumpSize = 5


    var kiai = false
    var jumpCounter = 0
    var offt = FloatArray(bars)

    val tempSprite: Sprite

    init {
        val texture = Texture(1, 1, data = intArrayOf(0xffffffff.toInt()))
        tempSprite = Sprite {
            this.texture = texture.region
            size = Vector2f(1f, 1f)
            origin = Origin.CentreLeft
        }

    }


    override fun update() {
        super.update()

        val bTime = (beatmapBindable.value!!.getTrack().getPosition() * 1000).toLong()

        val timingPoint = beatmapBindable.value!!.timing.getPointAt(bTime)

        kiai = timingPoint.kiai

        val time = System.nanoTime()
        if (lastTime == 0L) {
            lastTime = time
        }
        val delta = ((time - lastTime) / 1000000f)
        deltaSum += delta

        var decay = delta * baseDecay

        if (deltaSum >= 50f) {

            val fft = beatmapBindable.value!!.getTrack().fftData

            for (i in 0 until bars) {
                val value = fft[(i + jumpCounter) % bars] * if (kiai) 1f else 0.5f
                if (value > offt[i]) {
                    offt[i] = value
                }
            }

            decay = 0f
            jumpCounter = (jumpCounter + jumpSize) % bars
            deltaSum -= 50f
        }

        for (i in 0 until bars) {
            offt[i] -= (offt[i]+0.03f) * decay

            if (offt[i] < 0) {
                offt[i] = 0f
            }
        }



        lastTime = time
    }

    override fun draw(batch: SpriteBatch) {
        val pos = Vector2f(drawPosition).add(drawOrigin)
        for (i in 0 until 5) {
            offt.forEachIndexed { j, v ->
                val rotation = (i / 5.0f + j / bars.toFloat()) * 2 * Math.PI.toFloat()
                val position = vector2fRad(rotation, drawSize.y/2/*2 * 0.66f*/).add(pos)
                tempSprite.position = position
                tempSprite.rotation = rotation
                tempSprite.scale = Vector2f(v * barLength, (2 * Math.PI.toFloat() * /*400 * 0.66f*/drawSize.y/2) / bars.toFloat())
                tempSprite.color.w = 0.3f
                tempSprite.invalidate()
                tempSprite.update()
                batch.draw(tempSprite)
            }
        }
    }

    override fun dispose() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}