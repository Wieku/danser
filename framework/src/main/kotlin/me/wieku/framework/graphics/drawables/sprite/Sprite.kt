package me.wieku.framework.graphics.drawables.sprite

import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.graphics.drawables.Drawable
import me.wieku.framework.graphics.textures.TextureRegion
import org.joml.Vector2f
import org.joml.Vector4f
import java.util.*

open class Sprite(): Drawable() {

    constructor(inContext: Sprite.() -> Unit):this(){
        inContext()
    }

    override fun dispose() {}

    var texture: TextureRegion? = null

    override fun draw(batch: SpriteBatch) {
        batch.draw(this)
    }

}