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
import kotlin.math.min

class Visualizer() : Drawable(), KoinComponent {

    constructor(inContext: Visualizer.() -> Unit):this(){
        inContext()
    }

    private val beatmapBindable: Bindable<Beatmap?> by inject()

    private var deltaSum = 0f

    private val bars = 200
    private val barLength = 600f
    private val baseDecay = 0.0024f
    private val jumpSize = 5

    private var jumpCounter = 0
    private var amplitudes = FloatArray(bars)

    private val tempSprite: Sprite

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

        deltaSum += clock.time.frameTime

        var decay = clock.time.frameTime * baseDecay

        if (deltaSum >= 50f) {

            val fft = beatmapBindable.value!!.getTrack().fftData

            for (i in 0 until bars) {
                val value = fft[(i + jumpCounter) % bars] * if (timingPoint.kiai) 1f else 0.5f
                if (value > amplitudes[i]) {
                    amplitudes[i] = value
                }
            }

            decay = 0f
            jumpCounter = (jumpCounter + jumpSize) % bars
            deltaSum -= 50f
        }

        for (i in 0 until bars) {
            amplitudes[i] -= (amplitudes[i] + 0.03f) * decay

            if (amplitudes[i] < 0) {
                amplitudes[i] = 0f
            }
        }

    }

    override fun draw(batch: SpriteBatch) {
        val pos = Vector2f(drawPosition).add(drawOrigin)
        for (i in 0 until 5) {
            amplitudes.forEachIndexed { j, v ->
                if (v < 1 / /* min(*/drawSize.y*1.5f/*, barLength)*/) return@forEachIndexed
                val rotation = (i / 5.0f + j / bars.toFloat()) * 2 * Math.PI.toFloat()
                val position = vector2fRad(rotation, drawSize.y / 2).add(pos)
                tempSprite.position = position
                tempSprite.rotation = rotation
                tempSprite.scale = Vector2f(v * /*min(drawSize.y*1.5f, barLength)*/drawSize.y*1.5f, (2 * Math.PI.toFloat() * drawSize.y / 2) / bars.toFloat())
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