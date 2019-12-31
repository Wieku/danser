package me.wieku.danser.graphics.drawables

import me.wieku.danser.beatmap.Beatmap
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
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class Triangles() : Container(), KoinComponent {

    private val beatmapBindable: Bindable<Beatmap?> by inject()

    private val separation = 1.4f
    private val bars = 40
    private val triangleSpawnRate = 0.25
    private val random = Random()

    var minSize = 0.120f
    var maxSize = 0.520f
    var colorDark = Vector4f(0f, 0f, 0f, 1f)
    var colorLight = Vector4f(1f, 1f, 1f, 1f)
    var baseVelocity = 0.1f
    var spawnRate = 1f
    var spawnEnabled = true
    var speedMultiplier = 1f

    private var velocity = 0f

    constructor(inContext: Triangles.() -> Unit) : this() {
        inContext()
    }

    fun addTriangles(onscreen: Boolean = false) {
        val maxTriangles = (sqrt(drawSize.x * drawSize.y) * triangleSpawnRate * spawnRate).toInt()

        for (i in 0 until maxTriangles - children.size) {
            addTriangle(onscreen)
        }
    }

    fun addTriangle(onscreen: Boolean) {
        val size = (minSize + (random.nextGaussian().toFloat()*0.28f+1f)/2 * (maxSize - minSize))
        val position = Vector2f(random.nextFloat(), (if (onscreen) random.nextFloat() else 1f) * (1f + size / 2))

        insertChild(
            Sprite("misc/triangle.png") {
                customSize = true
                fillMode = Scaling.Fit

                scale.set(size)

                anchor = Origin.Custom
                customAnchor = position

                color = Vector4f(colorLight).sub(colorDark).mul(random.nextFloat()).add(colorDark)

                flipX = random.nextBoolean()
            },
            if (children.size > 0) random.nextInt(children.size) else 0
        )
    }

    override fun update() {

        if (spawnEnabled)
            addTriangles(children.size == 0)

        val fft = beatmapBindable.value!!.getTrack().fftData

        var boost = 0f

        for (i in 0 until bars) {
            boost += 2 * fft[i] * (bars - i).toFloat() / bars.toFloat()
        }

        velocity = max(velocity, min(boost * 0.15f, 0.6f) * speedMultiplier)

        velocity *= 1.0f - 0.05f * clock.time.frameTime / 16.66667f

        velocity = max(velocity, baseVelocity)

        val toRemove = children.filter {
            it.customAnchor.sub(
                0f,
                clock.time.frameTime / 1000f * velocity * (0.2f + (1.0f - it.scale.y / maxSize * 0.8f) * separation)
            )

            it.customAnchor.y < -it.scale.y / 2
        }

        toRemove.forEach {
            removeChild(it)
        }

        super.invalidate()
        super.update()
    }

}