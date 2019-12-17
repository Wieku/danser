package me.wieku.framework.graphics.textures

import me.wieku.framework.resource.FileHandle
import org.joml.Rectanglef
import org.lwjgl.opengl.GL32.*
import org.lwjgl.opengl.GL33
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

class TextureAtlas(_size: Int, _mipmaps: Int = 1, private val format: TextureFormat = TextureFormat.RGBA): ITexture {

    private data class Rectangle(var x: Int, var y: Int, var width: Int, var height: Int) {
        val area: Int
            get() = width * height
    }

    private lateinit var store: TextureStore
    val region = TextureRegion(this, 0f, 1f, 0f, 1f, 0)

    override val width: Int = _size
    override val height: Int = _size

    var size: Int = _size
        private set

    var mipmaps: Int = _mipmaps
        private set

    var padding: Int = 0
    private var subTextures = HashMap<String, TextureRegion>()
    private var emptySpaces = HashMap<Int, ArrayList<Rectangle>>()

    fun addTexture(name: String, width: Int, height: Int, data: ByteArray): TextureRegion {
        bind(store.binding)

        if (data.size != width * height * format.sizePerPixel) {
            throw IllegalArgumentException("Wrong number of pixels given!")
        }

        if (this.width < width || this.height < height) {
            throw IllegalArgumentException(String.format("Texture is too big! Atlas size: %dx%d, texture size: %dx%d", this.width, this.height, width, height))
        }

        val imBounds = Rectangle(0, 0, width + padding, height + padding)
        var layer = -1
        while (++layer < store.layers) {
            var j = -1
            var smallest = Rectangle(0, 0, store.width, store.height)

            emptySpaces[layer]?.forEachIndexed { i, space ->
                if (imBounds.width <= space.width && imBounds.height <= space.height) {
                    if (space.area <= smallest.area) {
                        j = i
                        smallest = space
                    }
                }
            }

            if (j == -1) {
                if (layer == store.layers-1) {
                    newLayer()
                }
                continue
            } else {
                val dw = smallest.width - imBounds.width
                val dh = smallest.height - imBounds.height

                val rect1 = when {
                    dh > dw -> Rectangle(smallest.x + imBounds.width, smallest.y, smallest.width - imBounds.width, imBounds.height)
                    else -> Rectangle(smallest.x + imBounds.width, smallest.y, smallest.width - imBounds.width, smallest.height)
                }

                val rect2 = when {
                    dh > dw -> Rectangle(smallest.x, smallest.y + imBounds.height, smallest.width, smallest.height - imBounds.height)
                    else -> Rectangle(smallest.x, smallest.y + imBounds.height, imBounds.width, smallest.height - imBounds.height)
                }

                emptySpaces[layer]?.removeAt(j)
                emptySpaces[layer]?.add(rect1)
                emptySpaces[layer]?.add(rect2)

                val buffer = MemoryUtil.memAlloc(data.size).put(data).flip() as ByteBuffer
                
                glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, smallest.x, smallest.y, layer, width, height, 1, format.format, format.type, buffer)
                glGenerateMipmap(GL_TEXTURE_2D_ARRAY)

                MemoryUtil.memFree(buffer)
                
                val region = TextureRegion( this, (smallest.x+0.5f)/store.width, (smallest.y+0.5f)/store.height, 0f, 0f, layer)
                region.U2 = region.U1 + (width-1).toFloat()/store.width
                region.V2 = region.V1 + (height-1).toFloat()/store.height
                subTextures[name] = region
                return region
            }

        }

        throw IllegalStateException("Failed to add texture")
    }

    private fun newLayer() {
        emptySpaces[store.layers] = mutableListOf(Rectangle(0, 0, store.width, store.height)) as ArrayList<Rectangle>

        val layers = store.layers + 1

        var fbo = glGenFramebuffers()
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fbo)

        val dstStore = TextureStore(layers, store.width, store.height, store.mipmaps, format)
        dstStore.setFiltering(store.minFilter, store.magFilter)
        dstStore.bind(store.binding)

        for (layer in 0 until layers-1) {
            for (level in 0 until store.mipmaps) {
                glFramebufferTextureLayer(GL_READ_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, store.id, level, layer)

                val div = 1 shl level
                glCopyTexSubImage3D(GL_TEXTURE_2D_ARRAY, level, 0, 0, layer, 0, 0, store.width/div, store.height/div)
            }
        }

        glDeleteFramebuffers(fbo)
        store.dispose()

        store = dstStore
    }

    fun getID() = store.id
    fun getLayers() = 1

    fun setFiltering(min: TextureFilter, mag: TextureFilter) = store.setFiltering(min, mag)

    fun bind(loc: Int) = store.bind(loc)
    fun getLocation() = store.binding
    override fun dispose() = store.dispose()
}