package me.wieku.framework.graphics.textures.store

import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.ResourceStore

class TextureStore: ResourceStore<Texture>() {
    override val resourceBasePath: String = "assets/textures/"

    init {
        val array = ByteArray(4) { 255.toByte() }
        resourceMap["pixel"] = Texture(1, 1, 1, data = array)
    }

    override fun loadResource(file: FileHandle): Texture {
        return Texture(file, 4)
    }
}