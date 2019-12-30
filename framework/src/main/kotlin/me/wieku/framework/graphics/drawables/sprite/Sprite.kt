package me.wieku.framework.graphics.drawables.sprite

import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.graphics.drawables.Drawable
import me.wieku.framework.graphics.textures.TextureRegion
import me.wieku.framework.graphics.textures.store.TextureAtlasStore
import me.wieku.framework.graphics.textures.store.TextureStore
import org.joml.Vector2f
import org.joml.Vector4f
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

open class Sprite() : Drawable(), KoinComponent {

    private var textureName = ""
    private val textureStore: TextureStore by inject()

    var customSize = false

    constructor(inContext: Sprite.() -> Unit) : this() {
        inContext()
    }

    constructor(textureName: String) : this() {
        this.textureName = textureName
    }

    constructor(textureName: String, inContext: Sprite.() -> Unit) : this(textureName) {
        inContext()
    }

    override fun dispose() {}

    var texture: TextureRegion? = null

    override fun draw(batch: SpriteBatch) {
        if (texture == null && textureName != "") {
            texture = textureStore.getResourceOrLoad(textureName).region
            if (!customSize) {
                size = Vector2f(texture!!.getWidth(), texture!!.getHeight())
                invalidate()
            }
        }
        batch.draw(this)
    }

}