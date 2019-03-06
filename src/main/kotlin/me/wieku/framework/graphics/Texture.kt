package me.wieku.framework.graphics

import org.lwjgl.opengl.GL32.*
import org.lwjgl.system.MemoryUtil
import java.lang.IllegalStateException
import java.nio.ByteBuffer

class Texture(val width: Int, val height: Int, mipmaps: Int = 1) {
	private var store = TextureStore(1, width, height, mipmaps)
	val region = TextureRegion(this, 0f, 1f, 0f, 1f, 0)

	fun setData(x: Int, y: Int, width: Int, height: Int, data: ByteArray) {
		if (data.size != width * height * 4) {
			throw IllegalStateException("Wrong number of pixels given!")
		}

		var buffer: ByteBuffer = MemoryUtil.memAlloc(data.size).put(data).flip() as ByteBuffer

		glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, x, y, 0, width, height, 1, GL_RGBA, GL_UNSIGNED_BYTE, buffer)

		MemoryUtil.memFree(buffer)

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