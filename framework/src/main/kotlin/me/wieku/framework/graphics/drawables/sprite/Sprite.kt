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

            field = value
            textureDirty = true
        }

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
        if (textureDirty) {
            texture = textureStore.getResourceOrLoad(textureName).region

            if (!customSize) {
                size = Vector2f(texture!!.getWidth(), texture!!.getHeight())
                invalidate()
            }

            textureDirty = false
        }

        batch.draw(this)
    }

}