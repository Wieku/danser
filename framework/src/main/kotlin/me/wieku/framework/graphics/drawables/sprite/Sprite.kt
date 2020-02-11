package me.wieku.framework.graphics.drawables.sprite

import me.wieku.framework.graphics.drawables.Drawable
import me.wieku.framework.graphics.textures.TextureRegion
import me.wieku.framework.graphics.textures.store.TextureStore
import org.joml.Vector2f
import org.koin.core.KoinComponent
import org.koin.core.inject

open class Sprite() : Drawable(), KoinComponent {

    private val textureStore: TextureStore by inject()

    private var textureDirty = false

    protected var textureName = ""
        set(value) {
            if (value == field) return

            textureDirty = true

            field = value
        }

    var customSize = false
    set(value) {
        if (value == field) return

        if (!value && texture != null) {
            size = Vector2f(texture!!.getWidth(), texture!!.getHeight())
            invalidate()
        }

        field = value
    }

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
        set(value) {
            if (value == field) return

            if (!customSize && value != null) {
                size = Vector2f(value.getWidth(), value.getHeight())
                invalidate()
            }

            field = value
        }

    override fun draw(batch: SpriteBatch) {
        if (textureDirty) {
            texture = textureStore.getResourceOrLoad(textureName).region

            textureDirty = false
        }

        batch.draw(this)
    }

}