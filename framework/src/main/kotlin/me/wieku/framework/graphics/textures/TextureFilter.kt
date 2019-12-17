package me.wieku.framework.graphics.textures

import org.lwjgl.opengl.GL33.*

enum class TextureFilter(val glId: Int) {
    Nearest(GL_NEAREST),
    Linear(GL_LINEAR),
    MipMap(GL_LINEAR_MIPMAP_LINEAR),
    MipMapNearestNearest(GL_NEAREST_MIPMAP_NEAREST),
    MipMapLinearNearest(GL_LINEAR_MIPMAP_NEAREST),
    MipMapNearestLinear(GL_NEAREST_MIPMAP_LINEAR),
    MipMapLinearLinear(GL_LINEAR_MIPMAP_LINEAR)
}