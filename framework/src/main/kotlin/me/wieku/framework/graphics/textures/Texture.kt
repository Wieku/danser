package me.wieku.framework.graphics.textures

import me.wieku.framework.resource.FileHandle
import org.lwjgl.opengl.GL32.*
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Texture {
    private lateinit var store: TextureStore
    val region = TextureRegion(this, 0f, 1f, 0f, 1f, 0)

    var width: Int = 0
        private set
    var height: Int = 0
        private set
    var mipmaps: Int = 1
        private set

    constructor(width: Int, height: Int, mipmaps: Int = 1, format: TextureFormat = TextureFormat.RGBA) {
        this.width = width
        this.height = height
        this.mipmaps = mipmaps
        this.store = TextureStore(1, width, height, mipmaps, format)
    }

    constructor(width: Int, height: Int, mipmaps: Int = 1, format: TextureFormat = TextureFormat.RGBA, data: ByteArray? = null) {
        this.width = width
        this.height = height
        this.mipmaps = mipmaps
        this.store = TextureStore(1, width, height, mipmaps, format)

        if (data != null) {
            setData(data)
        }
    }

    constructor(width: Int, height: Int, mipmaps: Int = 1, format: TextureFormat = TextureFormat.RGBA, data: ByteBuffer? = null) {
        this.width = width
        this.height = height
        this.mipmaps = mipmaps
        this.store = TextureStore(1, width, height, mipmaps, format)

        if (data != null) {
            setData(data)
        }
    }

    constructor(width: Int, height: Int, mipmaps: Int = 1, format: TextureFormat = TextureFormat.RGBA, data: FloatArray? = null) {
        this.width = width
        this.height = height
        this.mipmaps = mipmaps
        this.store = TextureStore(1, width, height, mipmaps, format)

        if (data != null) {
            setData(data)
        }
    }

    constructor(width: Int, height: Int, mipmaps: Int = 1, format: TextureFormat = TextureFormat.RGBA, data: FloatBuffer? = null) {
        this.width = width
        this.height = height
        this.mipmaps = mipmaps
        this.store = TextureStore(1, width, height, mipmaps, format)

        if (data != null) {
            setData(data)
        }
    }

    constructor(width: Int, height: Int, mipmaps: Int = 1, format: TextureFormat = TextureFormat.RGBA, data: IntArray? = null) {
        this.width = width
        this.height = height
        this.mipmaps = mipmaps
        this.store = TextureStore(1, width, height, mipmaps, format)

        if (data != null) {
            setData(data)
        }
    }

    constructor(width: Int, height: Int, mipmaps: Int = 1, format: TextureFormat = TextureFormat.RGBA, data: IntBuffer? = null) {
        this.width = width
        this.height = height
        this.mipmaps = mipmaps
        this.store = TextureStore(1, width, height, mipmaps, format)

        if (data != null) {
            setData(data)
        }
    }

    constructor(file: FileHandle, mipmaps: Int = 1) {
        val imageBuffer = file.toBuffer()

        MemoryStack.stackPush().use { stack ->
            val w: IntBuffer = stack.mallocInt(1)
            val h: IntBuffer = stack.mallocInt(1)
            val comp: IntBuffer = stack.mallocInt(1)

            stbi_set_flip_vertically_on_load(false)

            // Use info to read image metadata without decoding the entire image.
            // We don't need this for this demo, just testing the API.
            if (!stbi_info_from_memory(imageBuffer, w, h, comp)) {
                throw RuntimeException("Failed to read image information: " + stbi_failure_reason())
            } else {
                System.out.println("OK with reason: " + stbi_failure_reason())
            }

            //System.out.println("Image width: " + w.get(0));
            //System.out.println("Image height: " + h.get(0));
            //System.out.println("Image components: " + comp.get(0));
            //System.out.println("Image HDR: " + stbi_is_hdr_from_memory(imageBuffer))

            // Decode the image
            var image: ByteBuffer? =
                stbi_load_from_memory(imageBuffer, w, h, comp, 4)
                    ?: throw RuntimeException("Failed to load image: " + stbi_failure_reason())

            var ww = w.get(0)
            var hh = h.get(0)

            this.width = ww
            this.height = hh
            this.mipmaps = if(mipmaps < 1) 1 else mipmaps
            this.store = TextureStore(1, ww, hh)
            setData(image!!)

        }
    }

    fun setData(data: ByteArray, x: Int = 0, y: Int = 0, width: Int = this.width, height: Int = this.height) {
        if (data.size != width * height * store.format.sizePerPixel * (if (store.format.type == GL_FLOAT) 4 else 1)) {
            throw IllegalArgumentException("Wrong number of pixels given!")
        }

        val buffer = MemoryUtil.memAlloc(data.size).put(data).flip() as ByteBuffer

        setData(buffer, x, y, width, height)

        MemoryUtil.memFree(buffer)

    }

    fun setData(data: ByteBuffer, x: Int = 0, y: Int = 0, width: Int = this.width, height: Int = this.height) {
        if (data.limit() != width * height * store.format.sizePerPixel * (if (store.format.type == GL_FLOAT) 4 else 1)) {
            throw IllegalArgumentException("Wrong number of pixels given!")
        }

        glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, x, y, 0, width, height, 1, store.format.format, store.format.type, data)

        if (store.mipmaps > 1) {
            glGenerateMipmap(GL_TEXTURE_2D_ARRAY)
        }
    }

    fun setData(data: FloatArray, x: Int = 0, y: Int = 0, width: Int = this.width, height: Int = this.height) {
        if (data.size != width * height * store.format.sizePerPixel / (if (store.format.type == GL_FLOAT) 1 else 4)) {
            throw IllegalArgumentException("Wrong number of pixels given!")
        }

        val buffer = MemoryUtil.memAllocFloat(data.size).put(data).flip() as FloatBuffer

        setData(buffer, x, y, width, height)

        MemoryUtil.memFree(buffer)

    }

    fun setData(data: FloatBuffer, x: Int = 0, y: Int = 0, width: Int = this.width, height: Int = this.height) {
        if (data.limit() != width * height * store.format.sizePerPixel / (if (store.format.type == GL_FLOAT) 1 else 4)) {
            throw IllegalArgumentException("Wrong number of pixels given!")
        }

        glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, x, y, 0, width, height, 1, store.format.format, store.format.type, data)

        if (store.mipmaps > 1) {
            glGenerateMipmap(GL_TEXTURE_2D_ARRAY)
        }
    }

    fun setData(data: IntArray, x: Int = 0, y: Int = 0, width: Int = this.width, height: Int = this.height) {
        if (data.size != width * height * store.format.sizePerPixel / (if (store.format.type == GL_FLOAT) 1 else 4)) {
            throw IllegalArgumentException("Wrong number of pixels given!")
        }

        val buffer = MemoryUtil.memAllocInt(data.size).put(data).flip() as IntBuffer

        setData(buffer, x, y, width, height)

        MemoryUtil.memFree(buffer)

    }

    fun setData(data: IntBuffer, x: Int = 0, y: Int = 0, width: Int = this.width, height: Int = this.height) {
        if (data.limit() != width * height * store.format.sizePerPixel / (if (store.format.type == GL_FLOAT) 1 else 4)) {
            throw IllegalArgumentException("Wrong number of pixels given!")
        }

        glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, x, y, 0, width, height, 1, store.format.format, store.format.type, data)

        if (store.mipmaps > 1) {
            glGenerateMipmap(GL_TEXTURE_2D_ARRAY)
        }
    }

    fun getID() = store.id
    fun getLayers() = 1

    fun setFiltering(min: TextureFilter, mag: TextureFilter) = store.setFiltering(min, mag)

    fun bind(loc: Int) = store.bind(loc)
    fun getLocation() = store.binding
    fun dispose() = store.dispose()
}