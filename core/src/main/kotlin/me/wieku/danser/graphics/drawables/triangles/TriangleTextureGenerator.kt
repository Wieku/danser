package me.wieku.danser.graphics.drawables.triangles

import me.wieku.framework.graphics.buffers.Framebuffer
import me.wieku.framework.graphics.drawables.containers.BlurredContainer
import me.wieku.framework.graphics.drawables.containers.Container
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.graphics.helpers.ViewportHelper
import me.wieku.framework.graphics.helpers.blend.BlendFactor
import me.wieku.framework.graphics.helpers.blend.BlendHelper
import me.wieku.framework.graphics.pixmap.Pixmap
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.graphics.textures.TextureFilter
import me.wieku.framework.graphics.textures.TextureRegion
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import me.wieku.framework.math.color.Color
import me.wieku.framework.math.equalsEpsilon
import me.wieku.framework.math.view.Camera
import org.joml.Vector2f
import org.joml.Vector4f

class TriangleTextureGenerator() {

    private val size = 1024

    private lateinit var fbo: Framebuffer

    private lateinit var blurred: BlurredContainer
    private var triangleContainer: Container

    private lateinit var batch: SpriteBatch

    init {

        triangleContainer = Container {
            size = Vector2f(this@TriangleTextureGenerator.size.toFloat())
            origin = Origin.TopLeft
            addChild(
                BlurredContainer {
                    blurAmount = 0.3f
                    fillMode = Scaling.Stretch
                    inheritColor = false
                    addChild(
                        Sprite("misc/triangle.png") {
                            fillMode = Scaling.Fit
                            scale = Vector2f(0.798f)
                            color = Color(0f, 1f)
                        }
                    )
                }.also { blurred = it  },
                Sprite("misc/triangle.png") {
                    fillMode = Scaling.Fit
                    scale = Vector2f(0.8f)
                }
            )
        }
    }

    private var camera = Camera()

    fun generate(shadowAmount: Float): TextureRegion {
        blurred.blurAmount = shadowAmount.coerceIn(0f, 1f) * 2.5f
        blurred.color.a = if (shadowAmount.equalsEpsilon(0f)) 0.0f else 1.0f

        if (!::batch.isInitialized) {
            batch = SpriteBatch(10)
            val camera = Camera()
            camera.setViewport(0, 0, size, size)
            camera.update()
            batch.camera = camera
        }

        if (!::fbo.isInitialized) {
            fbo = Framebuffer(size, size)
            fbo.getTexture()!!.setFiltering(TextureFilter.Nearest, TextureFilter.Linear)
        }

        fbo.bind(true, Vector4f(0f))

        ViewportHelper.pushViewport(size, size)

        triangleContainer.update()

        batch.begin()
        triangleContainer.draw(batch)
        batch.end()

        fbo.unbind()

        ViewportHelper.popViewport()

        return fbo.getTexture()!!.region
    }

}