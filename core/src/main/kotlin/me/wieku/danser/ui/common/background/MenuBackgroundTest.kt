package me.wieku.danser.ui.common.background

import me.wieku.danser.beatmap.Beatmap
import me.wieku.danser.configuration.DanserConfig
import me.wieku.danser.graphics.drawables.triangles.TriangleOrder
import me.wieku.danser.graphics.drawables.triangles.Triangles
import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.graphics.buffers.Framebuffer
import me.wieku.framework.graphics.drawables.Drawable
import me.wieku.framework.graphics.drawables.containers.BlurredContainer
import me.wieku.framework.graphics.drawables.containers.Container
import me.wieku.framework.graphics.drawables.containers.ParallaxContainer
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.graphics.helpers.ViewportHelper
import me.wieku.framework.graphics.helpers.blend.BlendFactor
import me.wieku.framework.graphics.helpers.blend.BlendHelper
import me.wieku.framework.graphics.pixmap.Pixmap
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import me.wieku.framework.math.color.Color
import me.wieku.framework.math.view.Camera
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import org.joml.Vector2f
import org.joml.Vector4f
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File
import java.nio.file.Paths
import java.util.*

class MenuBackgroundTest() : ParallaxContainer(), KoinComponent {

    private class Proxy(val drawable: Container) : Drawable() {

        var prevChildNum = 0
        var prevColor = Color()

        override var wasUpdated: Boolean
            get() {
                val res =
                    prevChildNum != drawable.childNumber || drawable.childNumber > 1 || prevColor != drawable.drawColor
                prevChildNum = drawable.childNumber
                prevColor.set(drawable.drawColor)
                return res
            }
            set(_) {}

        override fun draw(batch: SpriteBatch) {
            BlendHelper.pushBlend()
            BlendHelper.setFunction(BlendFactor.One, BlendFactor.OneMinusSrcAlpha)

            drawable.draw(batch)
            batch.flush()

            BlendHelper.popBlend()
        }

        override fun dispose() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    private val beatmapBindable: Bindable<Beatmap?> by inject()
    private val beatmap = Bindable<Beatmap?>(null)

    private val random = Random()

    private val wrapper: Container
    private val blurred: BlurredContainer
    private val triangles: Triangles

    private lateinit var fbo: Framebuffer
    private val oldSize = Vector2f()

    private var pixmap: Pixmap? = null

    private var oldBackground: Sprite? = null
    private lateinit var currentBackground: Sprite

    private var camera = Camera()

    constructor(inContext: MenuBackgroundTest.() -> Unit) : this() {
        inContext()
    }

    init {
        beatmap.bindTo(beatmapBindable)

        useScissor = true

        addChild(
            Container {
                fillMode = Scaling.Stretch
            }.also { wrapper = it },
            Triangles() {
                fillMode = Scaling.Stretch
                minSize = 0.2f * 0.2f
                maxSize = 0.3f * 0.8f
                spawnRate = 0.175f * 0.5f
                baseVelocity = 0.05f
                shadowAmount = 0.6f
                separation = 0.7f
                triangleOrder = TriangleOrder.Latest
            }.also { triangles = it },
            BlurredContainer {
                blurAmount = 0.3f
                fillMode = Scaling.Stretch
                inheritColor = false
                addChild(
                    Proxy(wrapper)
                )
            }.also { blurred = it }
        )

        beatmap.addListener(true) { _, newBeatmap, _ ->
            if (newBeatmap == null) return@addListener

            val fileHandle = if (newBeatmap.beatmapInfo.version != "Danser Intro") {
                val tmpHandle = FileHandle(
                    Paths.get(DanserConfig.osuSongsDir.value, newBeatmap.beatmapSet.directory, newBeatmap.beatmapMetadata.backgroundFile).toString(),
                    FileType.Absolute
                )
                if (tmpHandle.file.exists()) tmpHandle else FileHandle(
                    "assets/textures/menu/backgrounds/background-1.png",
                    FileType.Classpath
                )
            } else FileHandle("assets/textures/menu/backgrounds/background-1.png", FileType.Classpath)

            if (::currentBackground.isInitialized)
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

        if (currentBackground.texture == null && pixmap != null) {
            currentBackground.texture = Texture(pixmap!!).region
            currentBackground.size =
                Vector2f(currentBackground.texture!!.getWidth(), currentBackground.texture!!.getHeight())
            currentBackground.invalidate()
            currentBackground.update()
            blurred.forceRedraw()
        }

        if (!::fbo.isInitialized || drawSize != oldSize) {
            if (::fbo.isInitialized) fbo.dispose()
            fbo = Framebuffer(drawSize.x.toInt(), drawSize.y.toInt())
            oldSize.set(drawSize)
            blurred.forceRedraw()
        }

        wrapper.draw(batch)
        batch.flush()

        fbo.bind(true, Vector4f(0f))

        camera.setViewport(drawPosition.x.toInt(), drawPosition.y.toInt(), drawSize.x.toInt(), drawSize.y.toInt())
        camera.update()

        val oldCamera = batch.camera
        batch.camera = camera

        ViewportHelper.pushViewport(drawSize.x.toInt(), drawSize.y.toInt())

        triangles.draw(batch)
        batch.flush()

        BlendHelper.pushBlend()
        BlendHelper.setFunction(BlendFactor.DstColor, BlendFactor.ConstantAlpha, BlendFactor.Zero, BlendFactor.One)
        BlendHelper.setColor(Color(0f, 0.2f))

        blurred.draw(batch)
        batch.flush()

        BlendHelper.popBlend()

        fbo.unbind()

        ViewportHelper.popViewport()

        batch.camera = oldCamera

        batch.draw(
            fbo.getTexture()!!,
            drawPosition.x + drawSize.x / 2,
            drawPosition.y + drawSize.y / 2,
            1f,
            -1f,
            Color(1f)
        )
    }

    private fun updateColors(pixmap: Pixmap) {
        triangles.colorArray = Array(50) {
            pixmap.pixelAt(random.nextInt(pixmap.width), random.nextInt(pixmap.height)).mixedWith(Color(0.8f, 1f), 0.8f)
        }
    }

}