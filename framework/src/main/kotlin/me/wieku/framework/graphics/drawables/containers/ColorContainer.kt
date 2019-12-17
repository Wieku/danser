package me.wieku.framework.graphics.drawables.containers

import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.graphics.textures.TextureFormat
import me.wieku.framework.math.Scaling

class ColorContainer() : Container() {

    private val sprite: Sprite

    init {
        if (pixel == null) {
            pixel = Texture(1, 1, 1, TextureFormat.RGBA, intArrayOf(0xffffffff.toInt()))
        }
        addChild(Sprite {
            texture = pixel!!.region
            fillMode = Scaling.Stretch
        }.also { sprite = it })
    }

    constructor(inContext: ColorContainer.() -> Unit) : this() {
        inContext()
    }

    override fun updateDrawable() {
        super.updateDrawable()
        sprite.shearX = shearX
        sprite.shearY = shearY
        sprite.rotation = rotation
    }

    private companion object {
        private var pixel: Texture? = null
    }
}