package me.wieku.framework.graphics.containers

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
        sprite = Sprite {
            texture = pixel!!.region
            fillMode = Scaling.Stretch
        }
        addChild(sprite)
    }

    constructor(inContext: ColorContainer.() -> Unit) : this() {
        inContext()
    }

    override fun update() {
        super.update()
        sprite.color = color
    }

    private companion object {
        private var pixel: Texture? = null
    }
}