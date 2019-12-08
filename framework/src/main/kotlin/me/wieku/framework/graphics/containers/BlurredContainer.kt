package me.wieku.framework.graphics.containers

import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.graphics.effects.BlurEffect
import me.wieku.framework.math.view.Camera

class BlurredContainer() : Container() {

    private val blur = BlurEffect(1920, 1080)
    private val tempSprite = Sprite()

    var needsRedraw = true

    private var needsResize = true

    private var camera = Camera()

    constructor(inContext: BlurredContainer.() -> Unit) : this() {
        inContext()
    }

    init {
        blur.setBlur(0.5f, 0.5f)
    }

    override fun update() {
        super.update()
        camera.setViewportF(drawPosition.x.toInt(), drawPosition.y.toInt(), drawSize.x.toInt(), drawSize.y.toInt())
        camera.update()
        if (drawSize.x.toInt() != blur.width || drawSize.y.toInt() != blur.height) {
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