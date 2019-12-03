package me.wieku.framework.graphics.containers

import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.graphics.effects.BlurEffect
import me.wieku.framework.math.view.Camera
import me.wieku.framework.utils.MaskingInfo
import org.joml.Rectanglef
import org.joml.Vector2f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL33
import kotlin.math.min

class BlurredContainer(): Container() {

    private val blur = BlurEffect(1920, 1080)
    private val tempSprite = Sprite()

    private var lastDrawSize = Vector2f(-1000f, -1000f)

    var needsRedraw = true

    private var needsResize = true

    private var camera = Camera()

    constructor(inContext: BlurredContainer.() -> Unit):this(){
        inContext()
    }

    init {
        blur.setBlur(0.5f, 0.5f)
    }

    override fun update() {
        super.update()
        camera.setViewportF(drawPosition.x.toInt(), drawPosition.y.toInt(), drawSize.x.toInt(), drawSize.y.toInt())
        camera.update()
        if (lastDrawSize != drawSize) {
            lastDrawSize.set(drawSize)
            needsRedraw = true
            needsResize = true
        }
    }

    override fun updateDrawable() {
        super.updateDrawable()
        tempSprite.apply {
            drawPosition = this@BlurredContainer.drawPosition
            drawOrigin = this@BlurredContainer.drawOrigin
            drawSize = this@BlurredContainer.drawSize
            shearX = this@BlurredContainer.shearX
            shearY = this@BlurredContainer.shearY
            additive = this@BlurredContainer.additive
            flipX = this@BlurredContainer.flipX
            flipY = this@BlurredContainer.flipY
        }
    }

    override fun draw(batch: SpriteBatch) {
        if (needsResize) {
            blur.resize(drawSize.x.toInt(), drawSize.y.toInt())
            needsResize = false
        }

        if (needsRedraw) {
            batch.flush()
            blur.begin()
            val oldCamera = batch.camera
            batch.camera = camera
            super.draw(batch)
            batch.flush()
            tempSprite.apply {
                texture = blur.endAndProcess().region
            }
            batch.camera = oldCamera
            needsRedraw = false
        }

        batch.draw(tempSprite)
    }

}