package me.wieku.framework.graphics.textures

import me.wieku.framework.logging.Logging
import org.lwjgl.opengl.ARBTextureStorage.glTexStorage3D
import org.lwjgl.opengl.GL33.*

internal class TextureStore(var layers: Int, var width: Int, var height: Int, var mipmaps: Int = 1, val format: TextureFormat = TextureFormat.RGBA) {

    private val logger = Logging.getLogger("performance")

    var id: Int = -1
    var binding: Int = -1

    var minFilter = TextureFilter.Linear
        private set

    var magFilter = TextureFilter.Linear
        private set

    init {
        id = glGenTextures()
        if (mipmaps < 1) {
            mipmaps = 1
        }

        bind(0)
        glTexStorage3D(GL_TEXTURE_2D_ARRAY, mipmaps, format.internalFormat, width, height, layers)
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_BASE_LEVEL, 0)
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAX_LEVEL, mipmaps - 1)
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

        if (mipmaps > 1) {
            setFiltering(
                TextureFilter.MipMap,
                TextureFilter.Linear
            )
        } else {
            setFiltering(
                TextureFilter.Linear,
                TextureFilter.Linear
            )
        }

        logger.info("TextureStore with size ${width}x${height}x$layers created")
    }

    fun bind(loc: Int) {
        binding = loc
        glActiveTexture(GL_TEXTURE0 + loc)
        glBindTexture(GL_TEXTURE_2D_ARRAY, id)
    }

    fun setFiltering(min: TextureFilter, mag: TextureFilter) {
        minFilter = min
        magFilter = mag
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, min.glId)
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, mag.glId)
    }

    fun dispose() {
        glDeleteTextures(id)
    }
}