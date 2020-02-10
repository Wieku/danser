package me.wieku.danser.graphics.drawables

import me.wieku.danser.beatmap.Beatmap
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.graphics.drawables.Drawable
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import org.joml.Vector2f
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.math.cos
import kotlin.math.sin

class Visualizer() : Drawable(), KoinComponent {

    private val beatmapBindable: Bindable<Beatmap?> by inject()

    private val updateDelay = 50f
    private val rounds = 5
    private val bars = 200
    private val baseDecay = 0.0024f
    private val jumpSize = 5

    private val tempSprite: Sprite = Sprite("pixel")
    private val amplitudes = FloatArray(bars)

    private var jumpCounter = 0
    private var deltaSum = 0f

    var barScale = 1f

    constructor(inContext: Visualizer.() -> Unit) : this() {
        inContext()
    }

    override fun update() {
        super.update()

        deltaSum += clock.time.frameTime

        var decay = clock.time.frameTime * baseDecay

        if (deltaSum >= updateDelay) {

            beatmapBindable.value?.let {

                val bTime = (beatmapBindable.value!!.getTrack().getPosition() * 1000).toLong()

                val timingPoint = beatmapBindable.value!!.timing.getPointAt(bTime)

                if (it.getTrack().isRunning) {
                    val fft = it.getTrack().fftData

                    for (i in 0 until bars) {
                        val value = fft[(jumpCounter + i) % bars] * if (timingPoint.kiai) 1.0f else 0.5f
                        if (value > amplitudes[i]) {
                            amplitudes[i] = value
                        }
                    }
                }
            }

            decay = 0f
            jumpCounter = (jumpCounter + jumpSize) % bars
            deltaSum -= updateDelay
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
        val barWidth = Math.PI.toFloat() * drawSize.y / bars.toFloat()
        val barLength = drawSize.y * 1.5f * barScale
        val cutoff = 1 / barLength

        tempPos.set(drawPosition).add(drawOrigin).sub(0f, barWidth / 2)

        tempSprite.drawColor.a = 0.3f * drawColor.a
        tempSprite.drawOrigin.set(0f, barWidth / 2)

        for (i in 0 until rounds) {
            amplitudes.forEachIndexed { j, v ->
                if (v < cutoff) return@forEachIndexed

                val rotation = (i / rounds.toFloat() + j / bars.toFloat()) * 2 * Math.PI.toFloat()

                tempSprite.drawPosition.set(tempPos.x + cos(rotation) * drawSize.y / 2, tempPos.y + sin(rotation) * drawSize.y / 2)
                tempSprite.rotation = rotation
                tempSprite.drawSize.set(v * barLength, barWidth)

                tempSprite.draw(batch)
            }
        }
    }

    override fun dispose() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}