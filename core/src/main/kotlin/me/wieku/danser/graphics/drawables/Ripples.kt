package me.wieku.danser.graphics.drawables

import me.wieku.danser.beatmap.Beatmap
import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.graphics.drawables.containers.Container
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.math.Easing
import me.wieku.framework.math.Scaling
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.math.floor

class Ripples() : Container(), KoinComponent {

    constructor(inContext: Ripples.() -> Unit) : this() {
        inContext()
    }

    private val beatmapBindable: Bindable<Beatmap?> by inject()

    private var lastBeatLength = 0f
    private var lastBeatStart = 0f
    private var lastProgress = 0

    var generateRipples = false

    private fun addRipple() {
        val sprite = Sprite("menu/coin-wave.png") {
            fillMode = Scaling.Fit
            drawForever = false
        }

        sprite.addTransform(
            Transform(
                TransformType.Fade,
                clock.currentTime,
                clock.currentTime + 1000,
                0.5f,
                0f,
                Easing.OutQuad
            )
        )

        sprite.addTransform(
            Transform(
                TransformType.Scale,
                clock.currentTime,
                clock.currentTime + 1000,
                1f,
                1.4f,
                Easing.OutQuad
            )
        )

        addChild(sprite)
    }

    override fun update() {

        val bTime = (beatmapBindable.value!!.getTrack().getPosition() * 1000).toLong()

        val timingPoint = beatmapBindable.value!!.timing.getPointAt(bTime)

        if (timingPoint.baseBpm != lastBeatLength) {
            lastProgress = -1
            lastBeatLength = timingPoint.baseBpm
            lastBeatStart = timingPoint.time.toFloat()
        }

        val bProg = (bTime - lastBeatStart) / lastBeatLength
        val progress = floor(bProg).toInt()

        if (progress != lastProgress) {
            if (progress > lastProgress && generateRipples)
                addRipple()
            lastProgress = progress
        }

        invalidate()
        super.update()
    }

    override fun dispose() {}
}