package me.wieku.danser.graphics.drawables.triangles

import me.wieku.danser.beatmap.Beatmap
import me.wieku.framework.animation.Glider
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.graphics.drawables.containers.Container
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.math.Scaling
import me.wieku.framework.math.color.Color
import org.joml.Vector2f
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.*

class Triangles() : Container(), KoinComponent {

    private val beatmapBindable: Bindable<Beatmap?> by inject()

    private val bars = 10
    private val triangleSpawnRate = 0.25

    private val triangles = ArrayList<Triangle>()
    private val trianglesToRemove = ArrayList<Triangle>()

    private val tempSprite = TriangleSprite()

    var drawShadows: Boolean = false
        set(value) {
            if (value == field) return

            tempSprite.useShadow = value

            field = value
        }

    var colorDark = Color(0f, 1f)
        set(value) {
            if (value == colorDark) return

            triangles.forEach {
                it.updateColors(value, colorLight)
            }

            field = value
        }

    var colorLight = Color(1f)
        set(value) {
            if (value == colorLight) return

            triangles.forEach {
                it.updateColors(colorDark, value)
            }

            field = value
        }

    var colorArray: Array<Color>? = null
        set(value) {
            value?.let {
                triangles.forEach {
                    it.updateColors(value)
                }
            }

            field = value
        }

    var minSize = 0.120f
    var maxSize = 0.520f

    var baseVelocity = 0.1f
    var speedMultiplier = 1f
    private var velocity = 0f
    var reactive = true
    var separation = 1.4f

    var spawnRate = 1f
    var trianglesMinimum = 0
    var spawnEnabled = true
    var startOnScreen = true

    var triangleOrder = TriangleOrder.Random
    var triangleDirection = TriangleDirection.Up

    private var triangleSize = Vector2f(0.0f)

    constructor(inContext: Triangles.() -> Unit) : this() {
        inContext()
    }

    private fun addTriangles(onscreen: Boolean = false) {
        val maxTriangles =
            max(trianglesMinimum, (sqrt(drawSize.x * drawSize.y) * triangleSpawnRate * spawnRate).toInt())

        val toAdd = maxTriangles - triangles.size

        for (i in 0 until toAdd) {
            addTriangle(onscreen)
        }

        if (toAdd > 0) {
            if (triangleOrder == TriangleOrder.SmallestToBiggest) {
                triangles.sortByDescending { it.size }
            } else if (triangleOrder == TriangleOrder.BiggestToSmallest) {
                triangles.sortBy { it.size }
            }
        }
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

        if (colorArray != null) {
            triangle.updateColors(colorArray!!)
        } else triangle.updateColors(colorDark, colorLight)

        if (triangleOrder == TriangleOrder.Random) {
            triangles.add(
                if (triangles.size > 0) random.nextInt(triangles.size) else 0,
                triangle
            )
        } else {
            triangles.add(triangle)
        }
    }

    override fun update() {
        super.update()

        if (spawnEnabled)
            addTriangles(startOnScreen && triangles.isEmpty())

        var boost = 0f

        if (reactive) {
            beatmapBindable.value?.let {
                if (it.getTrack().isRunning) {
                    val fft = it.getTrack().fftData

                    for (i in 0 until bars) {
                        boost += 2 * fft[i] * (bars - i).toFloat() / bars.toFloat()
                    }
                }
            }
        }

        velocity = max(velocity, min(boost * 0.0725f, 0.3f) * speedMultiplier)

        velocity *= 1.0f - 0.05f * clock.time.frameTime / 16.66667f

        val velocityCurrent = baseVelocity + velocity

        trianglesToRemove.clear()

        triangles.forEach {
            val base =
                clock.time.frameTime / 1000f * velocityCurrent * (1f + separation * (maxSize - it.size) / (maxSize - minSize))

            it.position.add(
                triangleDirection.directionX * base,
                triangleDirection.directionY * base
            )

            it.update(clock.currentTime)

            if (it.position.x !in -it.size / 2..1f + it.size / 2 || it.position.y !in -it.size / 2..1f + it.size / 2) {
                trianglesToRemove.add(it)
            }
        }

        if (triangles.isNotEmpty()) {
            triangles.removeAll(trianglesToRemove)
        }
    }

    override fun updateDrawable() {
        super.updateDrawable()
        triangleSize.set(Scaling.Fit.apply(1.0f, 1.0f, drawSize.x, drawSize.y))
    }

    private var drawArray = emptyArray<Triangle?>()

    override fun draw(batch: SpriteBatch) {
        if (drawArray.size < triangles.size + 2) {
            drawArray = arrayOfNulls(triangles.size + 2) //hack, but prevents ArrayIndexOutOfBoundsException during triangles.toArray. Needs a better solution.
        }
        drawArray = triangles.toArray(drawArray)

        tempSprite.rotation = if (triangleDirection.directionX != 0.0f) PI.toFloat() / 2 else 0.0f

        val scissorUsed = useScissor

        if (scissorUsed) {
            batch.pushMaskingInfo(maskingInfo)
        }

        for (it in drawArray) {
            if (it == null) break

            tempSprite.drawSize.set(triangleSize).mul(it.size)
            tempSprite.drawOrigin.set(tempSprite.drawSize).mul(0.5f)
            tempSprite.drawPosition.set(drawSize).mul(it.position).sub(tempSprite.drawOrigin).add(drawPosition)
            tempSprite.drawColor.set(drawColor).mul(it.color)
            tempSprite.flipX = it.flipX
            tempSprite.draw(batch)
        }

        if (scissorUsed) {
            batch.popMaskingInfo()
        }
    }

    override fun dispose() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private class TriangleSprite : Sprite("misc/triangle.png") {
        var useShadow: Boolean = false
            set(value) {
                if (value == field) return

                textureName = "misc/triangle" + (if (value) "-shadow" else "") + ".png"
                field = value
            }
    }

    private class Triangle(val position: Vector2f, val size: Float) {
        private val helper = Color()

        private val colorIndex = random.nextInt(Int.MAX_VALUE)
        private val colorShade = random.nextFloat()
        val flipX = random.nextBoolean()

        private val oldColor = Color(Float.NaN)
        private val newColor = Color(Float.NaN)
        val color = Color(Float.NaN)

        private var currentTime: Float = 0f
        private val colorGlider = Glider(1f)

        fun update(time: Float) {
            currentTime = time
            colorGlider.update(currentTime)

            color.set(
                when (colorGlider.value) {
                    0f -> oldColor
                    1f -> newColor
                    else -> Color.mix(oldColor, newColor, colorGlider.value, helper)
                }
            )
        }

        fun updateColors(dark: Color, light: Color) {
            colorTo(Color.mix(dark, light, colorShade, helper))
        }

        fun updateColors(colorArray: Array<Color>) {
            colorTo(colorArray[colorIndex % colorArray.size])
        }

        private fun colorTo(newColor: Color) {
            oldColor.set(color)

            if (!this.newColor.x.isNaN()) {
                colorGlider.reset()
                colorGlider.addEvent(currentTime, currentTime + 500, 0f, 1f)
            }

            this.newColor.set(newColor)
        }
    }

    private companion object {
        val random = Random()
    }
}