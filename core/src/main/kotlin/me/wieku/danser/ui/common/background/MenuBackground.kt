package me.wieku.danser.ui.common.background

import me.wieku.danser.beatmap.Beatmap
import me.wieku.danser.graphics.drawables.triangles.TriangleOrder
import me.wieku.danser.graphics.drawables.triangles.Triangles
import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.graphics.drawables.containers.Container
import me.wieku.framework.graphics.drawables.containers.ParallaxContainer
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.graphics.pixmap.Pixmap
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import org.joml.Vector2f
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File
import java.util.*

class MenuBackground() : ParallaxContainer(), KoinComponent {

    //TODO: texture unloading

    private val beatmapBindable: Bindable<Beatmap?> by inject()
    private val beatmap = Bindable<Beatmap?>(null)

    private val random = Random()

    private val wrapper: Container
    private val triangles: Triangles

    private var pixmap: Pixmap? = null

    private var oldBackground: Sprite? = null
    private var currentBackground: Sprite? = null

    constructor(inContext: MenuBackground.() -> Unit) : this() {
        inContext()
    }

    init {
        beatmap.bindTo(beatmapBindable)

        useScissor = true

        addChild(
            Container {
                fillMode = Scaling.Stretch
            }.also { wrapper = it },
            Triangles {
                shadowAmount = 0.0f
                fillMode = Scaling.Stretch
                minSize = 0.05f
                maxSize = 0.32f
                spawnRate = 0.5f
                baseVelocity = 0.05f
                triangleOrder = TriangleOrder.Latest
            }.also { triangles = it }
        )

        beatmap.addListener(true) { _, newBeatmap, _ ->
            if (newBeatmap == null) return@addListener

            val fileHandle = if (newBeatmap.beatmapInfo.version != "Danser Intro") {
                val tmpHandle = FileHandle(
                    System.getenv("localappdata") + "/osu!/Songs/" + newBeatmap.beatmapSet.directory + File.separator + newBeatmap.beatmapMetadata.backgroundFile,
                    FileType.Absolute
                )
                if (tmpHandle.file.exists()) tmpHandle else FileHandle(
                    "assets/textures/menu/backgrounds/background-1.png",
                    FileType.Classpath
                )
            } else FileHandle("assets/textures/menu/backgrounds/background-1.png", FileType.Classpath)

            oldBackground = currentBackground

            pixmap = Pixmap(fileHandle)
            updateColors(pixmap!!)

            currentBackground = Sprite {
                fillMode = Scaling.Fill
                anchor = Origin.Centre
            }

            wrapper.addChild(currentBackground!!)

            if (oldBackground != null) {
                oldBackground!!.addTransform(
                    Transform(
                        TransformType.Fade,
                        clock.currentTime,
                        clock.currentTime + 500f,
                        1f,
                        0f
                    )
                )
                oldBackground!!.drawForever = false

                currentBackground!!.addTransform(
                    Transform(
                        TransformType.Fade,
                        clock.currentTime,
                        clock.currentTime + 500f,
                        0f,
                        1f
                    )
                )
            }
        }
    }

    override fun draw(batch: SpriteBatch) {
        if (currentBackground!!.texture == null && pixmap != null) {
            currentBackground!!.texture = Texture(pixmap!!).region
            currentBackground!!.size =
                Vector2f(currentBackground!!.texture!!.getWidth(), currentBackground!!.texture!!.getHeight())
        }

        super.draw(batch)
    }

    private fun updateColors(pixmap: Pixmap) {
        triangles.colorArray = Array(50) {
            pixmap.pixelAt(random.nextInt(pixmap.width), random.nextInt(pixmap.height))
        }
    }

}