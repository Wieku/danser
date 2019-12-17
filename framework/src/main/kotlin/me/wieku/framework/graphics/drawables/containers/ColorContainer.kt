package me.wieku.framework.graphics.drawables.containers

import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.graphics.textures.TextureFormat
import me.wieku.framework.math.Scaling

class ColorContainer() : Container() {

    init {
        if (pixel == null) {
            pixel = Texture(1, 1, 1, TextureFormat.RGBA, intArrayOf(0xffffffff.toInt()))
        }
        addChild(Sprite {
            texture = pixel!!.region
            fillMode = Scaling.Stretch
        })
    }

    constructor(inContext: ColorContainer.() -> Unit) : this() {
        inContext()
    }

    private companion object {
        private var pixel: Texture? = null
    }
}