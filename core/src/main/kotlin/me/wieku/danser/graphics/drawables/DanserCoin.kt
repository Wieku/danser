package me.wieku.danser.graphics.drawables

import me.wieku.danser.beatmap.Beatmap
import me.wieku.danser.graphics.drawables.triangles.Triangles
import me.wieku.framework.animation.Glider
import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.audio.SampleStore
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.graphics.drawables.containers.CircularContainer
import me.wieku.framework.graphics.drawables.containers.ColorContainer
import me.wieku.framework.graphics.drawables.containers.Container
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.input.event.ClickEvent
import me.wieku.framework.input.event.HoverEvent
import me.wieku.framework.input.event.HoverLostEvent
import me.wieku.framework.math.Easing
import me.wieku.framework.math.Easings
import me.wieku.framework.math.Scaling
import org.joml.Vector2f
import org.joml.Vector2i
import org.joml.Vector4f
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class DanserCoin : Container(), KoinComponent {

    private val beatmapBindable: Bindable<Beatmap?> by inject()
    private val sampleStore: SampleStore by inject()

    private val defaultBeatLength = 1000f

    private var deltaSum = 0f

    private var volume = 0f
    private var volumeAverage = 0f

    private var progress = 0f
    private var lastProgress = 0f

    private var beatProgress = 0f

    private var lastBeatLength = 0f
    private var lastBeatStart = 0f
    private var lastBeatProgress = 0

    private var clickedState = false

    private val coinInflate = Glider(1f)

    private val ripples: Ripples
    private val coinBottom: Container
    private lateinit var triangles: Triangles
    private lateinit var circularContainer: CircularContainer
    private lateinit var coinFlash: ColorContainer
    private val coinTop: Sprite

    init {
        coinBottom = Container {
            fillMode = Scaling.Fill
            addChild(
                Visualizer {
                    fillMode = Scaling.Fit
                    scale = Vector2f(0.95f)
                },
                CircularContainer {
                    scale = Vector2f(0f)
                    fillMode = Scaling.Fit
                    addChild(
                        ColorContainer {
                            fillMode = Scaling.Stretch
                            color = Vector4f(28f / 255, 28f / 255, 28f / 255, 1f)
                        },
                        Triangles {
                            fillMode = Scaling.Fit
                            spawnRate = 0.25f
                            speedMultiplier = 0.33f
                            spawnEnabled = false
                            colorDark = Vector4f(0.054f, 0.054f, 0.054f, 1f)
                            colorLight = Vector4f(0.2f, 0.2f, 0.2f, 1f)
                        }.also { triangles = it }
                    )
                }.also { circularContainer = it },
                Sprite("menu/coin-overlay.png") {
                    fillMode = Scaling.Fit
                },
                CircularContainer {
                    scale = Vector2f(0.95f)
                    fillMode = Scaling.Fit
                    addChild(
                        ColorContainer {
                            fillMode = Scaling.Stretch
                            color = Vector4f(1f, 1f, 1f, 0f)
                        }.also { coinFlash = it }
                    )
                }
            )
        }

        coinTop = Sprite("menu/coin-overlay.png") {
            fillMode = Scaling.FillY
            color.w = 0.3f
        }

        addChild(
            Ripples {
                fillMode = Scaling.Fit
            }.also { ripples = it },
            coinBottom,
            coinTop
        )
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

        val bProg = when (beatmapBindable.value!!.getTrack().isRunning) {
            true -> (bTime - timingPoint.time) / timingPoint.baseBpm
            false -> clock.currentTime / defaultBeatLength
        }

        beatProgress = bProg - floor(bProg)

        if (timingPoint.baseBpm != lastBeatLength) {
            lastBeatProgress = -1
            lastBeatLength = timingPoint.baseBpm
            lastBeatStart = timingPoint.time.toFloat()
        }

        val beatLength = max(300f, lastBeatLength)
        val b1Prog = (bTime - lastBeatStart) / lastBeatLength
        val progress1 = floor(b1Prog).toInt()

        if (progress1 != lastBeatProgress) {
            if (timingPoint.kiai) {
                coinFlash.addTransform(
                    Transform(
                        TransformType.Fade,
                        clock.currentTime,
                        clock.currentTime + beatLength,
                        0.6f,
                        0f,
                        Easing.OutQuad
                    ), false
                )
            }

            if (triangles.spawnEnabled && isHovered) {
                sampleStore.getResourceOrLoad("menu/heartbeat.mp3").play(1.5f)
            }

            lastBeatProgress = progress1
        }

        val vprog = if (beatmapBindable.value!!.getTrack().isRunning) 1f - (volume - volumeAverage) / 0.5f else 1f
        val pV = min(1.0f, max(0.0f, 1.0f - (vprog * 0.5f + beatProgress * 0.5f)))

        val ratio = 0.5f.pow(clock.time.frameTime / 16.6666666666667f)

        progress = lastProgress * ratio + pV * (1 - ratio)
        lastProgress = progress

        coinBottom.scale.set(1.05f - Easings.OutQuad(progress * 0.05f))
        coinTop.scale.set(1.05f + Easings.OutQuad(progress * 0.03f))

        coinInflate.update(clock.currentTime)
        invalidate()
        super.update()
    }

    override fun updateDrawable() {
        val inflate = coinInflate.value
        scale.mul(inflate)
        super.updateDrawable()
        scale.mul(1f / inflate)
    }

    fun introBegin() {
        circularContainer.addTransform(
            Transform(
                TransformType.Scale,
                clock.currentTime + 600,
                clock.currentTime + 1300f,
                0f,
                0.95f
            )
        )
    }

    fun introFinished() {
        triangles.spawnEnabled = true
        ripples.generateRipples = true
    }

    override fun isCursorIn(cursorPosition: Vector2i): Boolean {
        return circularContainer.isCursorIn(cursorPosition)
    }

    override fun onClick(e: ClickEvent): Boolean {
        sampleStore.getResourceOrLoad("menu/menuhit1.wav").play()
        clickedState = !clickedState
        ripples.generateRipples = !clickedState
        return super.onClick(e)
    }

    override fun onHover(e: HoverEvent): Boolean {
        coinInflate.addEvent(
            clock.currentTime + 200f,
            1.25f,
            Easing.OutQuad
        )
        return true
    }

    override fun onHoverLost(e: HoverLostEvent): Boolean {
        coinInflate.addEvent(
            clock.currentTime + 200f,
            1f,
            Easing.OutQuad
        )
        return super.onHoverLost(e)
    }

    override fun dispose() {}
}