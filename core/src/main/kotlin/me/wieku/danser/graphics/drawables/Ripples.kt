package me.wieku.danser.graphics.drawables

import me.wieku.danser.beatmap.Beatmap
import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.graphics.drawables.containers.Container
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.math.Easing
import me.wieku.framework.math.Scaling
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.math.floor
import kotlin.math.max

class Ripples() : Container(), KoinComponent {

    constructor(inContext: Ripples.() -> Unit):this(){
        inContext()
    }

    private val beatmapBindable: Bindable<Beatmap?> by inject()

    private var lastBeatLength = 0f
    private var lastBeatStart = 0f
    private var lastProgress = 0

    var generateRipples = false

    private val rippleTexture: Texture = Texture(
        FileHandle(
            "assets/textures/menu/coin-wave.png",
            FileType.Classpath
        ),
        4
    )

    private fun addRipple() {
        val sprite = Sprite {
            texture = rippleTexture.region
            fillMode = Scaling.Fit
            drawForever = false
        }
        sprite.addTransform(Transform(
            TransformType.Fade,
            clock.currentTime,
            clock.currentTime + 1000,
            0.5f,
            0f,
            Easing.OutQuad
        ), false)
        sprite.addTransform(Transform(
            TransformType.Scale,
            clock.currentTime,
            clock.currentTime + 1000,
            1f,
            1.4f,
            Easing.OutQuad
        ), false)
        sprite.adjustTimesToTransformations()
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

        val beatLength = max(300f, lastBeatLength)
        val bProg = ((bTime - lastBeatStart) / lastBeatLength)
        val progress = floor(bProg).toInt()

        if (progress > lastProgress) {
            if (generateRipples)
                addRipple()
            lastProgress++
        }

        invalidate()
        super.update()
    }

    override fun dispose() {}
}