package me.wieku.danser.graphics.drawables

import me.wieku.danser.beatmap.Beatmap
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.graphics.drawables.Drawable
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.math.Origin
import me.wieku.framework.math.rot
import me.wieku.framework.math.vector2fRad
import org.joml.Vector2f
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.math.min

class Visualizer() : Drawable(), KoinComponent {

    constructor(inContext: Visualizer.() -> Unit) : this() {
        inContext()
    }

    private val beatmapBindable: Bindable<Beatmap?> by inject()

    private var deltaSum = 0f

    private val bars = 200
    private val baseDecay = 0.0024f
    private val jumpSize = 5

    private var jumpCounter = 0
    private var amplitudes = FloatArray(bars)

    var barScale = 1f

    private val tempSprite: Sprite = Sprite("pixel")

    override fun update() {
        super.update()

        deltaSum += clock.time.frameTime

        var decay = clock.time.frameTime * baseDecay

        if (deltaSum >= 50f) {

            beatmapBindable.value?.let {

                val bTime = (beatmapBindable.value!!.getTrack().getPosition() * 1000).toLong()

                val timingPoint = beatmapBindable.value!!.timing.getPointAt(bTime)

                if (it.getTrack().isRunning) {
                    val fft = it.getTrack().fftData

                    for (i in 0 until bars) {
                        val value = fft[(i + jumpCounter) % bars] * if (timingPoint.kiai) 1f else 0.5f
                        if (value > amplitudes[i]) {
                            amplitudes[i] = value
                        }
                    }
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

    private val tempPos = Vector2f(0f)

    override fun draw(batch: SpriteBatch) {
        tempPos.set(drawPosition).add(drawOrigin)

        val barWidth = Math.PI.toFloat() * drawSize.y / bars.toFloat()
        val cutoff = drawSize.y * 1.5f * barScale

        for (i in 0 until 5) {
            amplitudes.forEachIndexed { j, v ->
                if (v < 1 / cutoff) return@forEachIndexed

                val rotation = (i / 5.0f + j / bars.toFloat()) * 2 * Math.PI.toFloat()

                tempSprite.drawPosition.set(drawSize.y / 2, 0f).rot(rotation).add(tempPos)
                tempSprite.drawOrigin.set(0f, 0.5f)
                tempSprite.rotation = rotation
                tempSprite.drawSize.set(v * drawSize.y * 1.5f * barScale, barWidth)
                tempSprite.drawColor.w = 0.3f * drawColor.w

                tempSprite.draw(batch)
            }
        }
    }

    override fun dispose() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}