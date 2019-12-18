package me.wieku.framework.graphics.textures.store

import me.wieku.framework.graphics.textures.TextureAtlas
import me.wieku.framework.graphics.textures.TextureFormat
import me.wieku.framework.graphics.textures.TextureRegion
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.ResourceStore
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer
import java.nio.IntBuffer

class TextureAtlasStore: ResourceStore<TextureRegion>() {

    override val resourceBasePath: String = "assets/textures/"

    val textureAtlas = TextureAtlas(4096, 4)

    init {
        val array = ByteArray(16*4) { 255.toByte() }
        resourceMap["pixel"] = textureAtlas.addTexture("pixel", 4, 4, array)
    }

    override fun loadResource(file: FileHandle): TextureRegion {
        return textureAtlas.addTexture(file.file.name, file)
    }
}