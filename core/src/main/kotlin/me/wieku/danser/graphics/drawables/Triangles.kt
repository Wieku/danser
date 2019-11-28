package me.wieku.danser.graphics.drawables

import me.wieku.danser.beatmap.Beatmap
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.graphics.containers.Container
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.math.Origin
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import me.wieku.framework.utils.MaskingInfo
import org.joml.Vector2f
import org.joml.Vector4f
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class Triangles : Container(), KoinComponent {

    private val beatmapBindable: Bindable<Beatmap> by inject()

    private val maskInfo = MaskingInfo()

    private val separation = 1.4f
    private val minSize = 0.120f
    private val maxSize = 0.520f
    private val bars = 40
    private val triangleSpawnRate = 0.25

    private var lastTime = 0L

    private var velocity = 0f

    private val triangleTexture: Texture = Texture(
        FileHandle(
            "assets/triangle.png",
            FileType.Classpath
        ),
        4
    )

    fun addTriangles(onscreen: Boolean = false) {
        val maxTriangles = (sqrt(drawSize.x * drawSize.y) * triangleSpawnRate).toInt()

        for (i in 0 until maxTriangles - children.size) {
            addTriangle(onscreen)
        }

    }

    fun addTriangle(onscreen: Boolean) {
        val size = (minSize + Math.random().toFloat() * (maxSize - minSize))
        val position =
            Vector2f(Math.random().toFloat(), (if (onscreen) Math.random().toFloat() else 1f) * (1f + size / 2))
        val col = 0.054f + Math.random().toFloat() * (/*0.117f*/0.2f - 0.054f)
        val sprite = Sprite {
            texture = triangleTexture.region
            this.size = Vector2f(size)
            this.scale = Vector2f(1f)
            inheritScale = false
            anchor = Origin.Custom
            customAnchor = position
            color = Vector4f(col, col, col, /*0.5f + Math.random().toFloat() * 0.5f*/1f)
        }

        sprite.flipX = Math.random().toFloat() >= 0.5

        insertChild(sprite, (Math.random() * (max(children.size - 1, 0)).toDouble()).toInt())
    }

    override fun update() {
        super.update()
        val time = System.nanoTime()
        if (lastTime == 0L) {
            lastTime = time
        }
        val delta = ((time - lastTime) / 1000000f)

        var boost = 0f

        if (children.size == 0) {
            addTriangles(true)
        }

        val fft = beatmapBindable.value!!.getTrack().fftData

        for (i in 0 until bars) {
            boost += 2 * fft[i] * (bars - i).toFloat() / bars.toFloat()
        }

        velocity = max(velocity, min(boost * 1.5f, 6f))

        velocity *= 1.0f - 0.05f * delta / 16.66667f

        val localVelocity = (velocity + 0.5f) / drawSize.y

        var toRemove = children.filter {
            it.customAnchor.sub(
                0f,
                delta / 16.6667f * localVelocity * (0.2f + (1.0f - it.size.y / maxSize * 0.8f) * separation)
            )
            it.scale.set(drawSize.y)
            it.customAnchor.y < -it.size.y / 2
        }

        toRemove.forEach {
            removeChild(it)
        }

        addTriangles(false)

        lastTime = time

        super.invalidate()
        super.update()

        /*println("${position.x}, ${position.y}, ${position.x+drawSize.x}, ${position.y + drawSize.y}")
        println("${drawPosition.x}, ${drawPosition.y}, ${drawPosition.x+drawSize.x}, ${drawPosition.y + drawSize.y}")

        println()*/

        maskInfo.rect.set(drawPosition.x, drawPosition.y, drawPosition.x+drawSize.x, drawPosition.y + drawSize.y)
        maskInfo.radius = drawSize.y/2
    }

    override fun draw(batch: SpriteBatch) {
        batch.pushMaskingInfo(maskInfo)
        super.draw(batch)
        batch.popMaskingInfo()
    }

}