package me.wieku.framework.graphics

import org.lwjgl.opengl.ARBTextureStorage.glTexStorage3D
import org.lwjgl.opengl.GL33.*

enum class Filtering(var glId: Int) {
	Nearest(GL_NEAREST),
	Linear(GL_LINEAR),
	MipMap(GL_LINEAR_MIPMAP_LINEAR),
	MipMapNearestNearest(GL_NEAREST_MIPMAP_NEAREST),
	MipMapLinearNearest(GL_LINEAR_MIPMAP_NEAREST),
	MipMapNearestLinear(GL_NEAREST_MIPMAP_LINEAR),
	MipMapLinearLinear(GL_LINEAR_MIPMAP_LINEAR)
}

private class TextureStore(var layers: Int, var width: Int, var height: Int, var mipmaps: Int = 1) {
	var id: Int = -1
	var binding: Int = -1

	init {
		id = glGenTextures()
		if (mipmaps < 1) {
			mipmaps = 1
		}

		bind(0)
		glTexStorage3D(GL_TEXTURE_2D_ARRAY, mipmaps, GL_RGBA8, width, height, layers)
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_BASE_LEVEL, 0)
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAX_LEVEL, mipmaps - 1)
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

		if (mipmaps > 1) {
			setFiltering(Filtering.MipMap, Filtering.Linear)
		} else {
			setFiltering(Filtering.Linear, Filtering.Linear)
		}

	}

	fun bind(loc: Int) {
		binding = loc
		glActiveTexture(GL_TEXTURE0 + loc)
		glBindTexture(GL_TEXTURE_2D_ARRAY, id)
	}

	fun setFiltering(min:Filtering, mag :Filtering) {
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, min.glId)
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, mag.glId)
	}

	fun dispose() {
		glDeleteTextures(id)
	}
}