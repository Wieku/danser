package me.wieku.danser.graphics.drawables.triangles

import me.wieku.danser.beatmap.Beatmap
import me.wieku.framework.animation.Glider
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.graphics.drawables.containers.Container
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import org.joml.Vector2f
import org.joml.Vector4f
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*
import kotlin.math.*

class Triangles() : Container(), KoinComponent {

    private class Triangle(position: Vector2f, size: Float) : Sprite("misc/triangle.png") {
        private val helper = Vector4f()

        private val colorIndex = random.nextInt(Int.MAX_VALUE)
        private val colorShade = random.nextFloat()

        private val oldColor = Vector4f(Float.NaN)
        private val newColor = Vector4f(Float.NaN)

        private val colorGlider = Glider(1f)

        init {
            customSize = true
            fillMode = Scaling.Fit
            scale = Vector2f(size)

            anchor = Origin.Custom
            customAnchor = position

            flipX = random.nextBoolean()
        }

        override fun update() {
            colorGlider.update(clock.currentTime)

            color.set(
                when (colorGlider.value) {
                    0f -> oldColor
                    1f -> newColor
                    else -> helper.set(newColor).sub(oldColor).mul(colorGlider.value).add(oldColor)
                }
            )

            super.update()
        }

        fun updateColors(dark: Vector4f, light: Vector4f) {
            colorTo(helper.set(light).sub(dark).mul(colorShade).add(dark))
        }

        fun updateColors(colorArray: Array<Vector4f>) {
            colorTo(colorArray[colorIndex % colorArray.size])
        }

        private fun colorTo(newColor: Vector4f) {
            oldColor.set(color)

            if (!this.newColor.x.isNaN()) {
                colorGlider.reset()
                colorGlider.addEvent(clock.currentTime, clock.currentTime + 500, 0f, 1f)
            }

            this.newColor.set(newColor)
        }
    }

    private val beatmapBindable: Bindable<Beatmap?> by inject()

    private val separation = 1.4f
    private val bars = 40
    private val triangleSpawnRate = 0.25

    var colorDark = Vector4f(0f, 0f, 0f, 1f)
        set(value) {
            if (value == colorDark) return

            children.forEach {
                (it as Triangle).updateColors(value, colorLight)
            }

            field = value
        }

    var colorLight = Vector4f(1f, 1f, 1f, 1f)
        set(value) {
            if (value == colorLight) return

            children.forEach {
                (it as Triangle).updateColors(colorDark, value)
            }

            field = value
        }

    var colorArray: Array<Vector4f>? = null
        set(value) {
            value?.let {
                children.forEach {
                    (it as Triangle).updateColors(value)
                }
            }

            field = value
        }

    var minSize = 0.120f
    var maxSize = 0.520f

    var baseVelocity = 0.1f
    var speedMultiplier = 1f
    private var velocity = 0f

    var spawnRate = 1f
    var spawnEnabled = true
    var startOnScreen = true

    var triangleOrder = TriangleOrder.Random
    var triangleDirection = TriangleDirection.Up

    constructor(inContext: Triangles.() -> Unit) : this() {
        inContext()
    }

    private fun addTriangles(onscreen: Boolean = false) {
        val maxTriangles = (sqrt(drawSize.x * drawSize.y) * triangleSpawnRate * spawnRate).toInt()

        modificationLock.lock()

        for (i in 0 until maxTriangles - children.size) {
            addTriangle(onscreen)
        }

        if (triangleOrder == TriangleOrder.SmallestToBiggest) {
            children.sortByDescending { it.scale.y }
        } else if (triangleOrder == TriangleOrder.BiggestToSmallest) {
            children.sortBy { it.scale.y }
        }

        modificationLock.unlock()
    }

    fun addTriangle(onscreen: Boolean) {
        val size = minSize + (sin(random.nextFloat() * 2 * PI.toFloat()) * 0.5f + 0.5f) * (maxSize - minSize)

        val position = when (triangleDirection) {
            TriangleDirection.Up -> Vector2f(random.nextFloat(), if (onscreen) random.nextFloat() else 1f + size / 2)
            TriangleDirection.Down -> Vector2f(random.nextFloat(), if (onscreen) random.nextFloat() else -size / 2)
            TriangleDirection.Left -> Vector2f(if (onscreen) random.nextFloat() else 1f + size / 2, random.nextFloat())
            TriangleDirection.Right -> Vector2f(if (onscreen) random.nextFloat() else -size / 2, random.nextFloat())
        }

        val triangle = Triangle(position, size)

        if (triangleDirection.directionX != 0.0f) triangle.rotation = PI.toFloat() / 2

        if (colorArray != null) {
            triangle.updateColors(colorArray!!)
        } else triangle.updateColors(colorDark, colorLight)

        if (triangleOrder == TriangleOrder.Random) {
            insertChild(
                triangle,
                if (children.size > 0) random.nextInt(children.size) else 0
            )
        } else {
            addChild(triangle)
        }
    }

    override fun update() {

        if (spawnEnabled)
            addTriangles(children.size == 0 && startOnScreen)

        var boost = 0f

        beatmapBindable.value?.let {
            if (it.getTrack().isRunning) {
                val fft = it.getTrack().fftData

                for (i in 0 until bars) {
                    boost += 2 * fft[i] * (bars - i).toFloat() / bars.toFloat()
                }
            }
        }

        velocity = max(velocity, min(boost * 0.15f, 0.6f) * speedMultiplier)

        velocity *= 1.0f - 0.05f * clock.time.frameTime / 16.66667f

        velocity = max(velocity, baseVelocity)

        val toRemove = children.filter {
            val base =
                clock.time.frameTime / 1000f * velocity * (0.2f + (1.0f - it.scale.y / maxSize * 0.8f) * separation)

            it.customAnchor.add(
                triangleDirection.directionX * base,
                triangleDirection.directionY * base
            )

            it.customAnchor.y !in -it.scale.y / 2..1f + it.scale.y / 2 || it.customAnchor.x !in -it.scale.x / 2..1f + it.scale.x / 2
        }

        toRemove.forEach {
            removeChild(it)
        }

        super.invalidate()
        super.update()
    }

    private companion object {
        val random = Random()
    }
}