package me.wieku.framework.graphics.textures

import org.lwjgl.opengl.GL30.*

enum class TextureFormat(val internalFormat: Int, val format: Int, val type: Int, val sizePerPixel: Int) {
    RGB(GL_RGB8, GL_RGB, GL_UNSIGNED_BYTE, 3),
    RGB32F(GL_RGB32F, GL_RGB, GL_FLOAT, 3),
    RGBA(GL_RGBA8, GL_RGBA, GL_UNSIGNED_BYTE, 4),
    RGBA32F(GL_RGBA32F, GL_RGBA, GL_FLOAT, 4),
    DEPTH(GL_DEPTH_COMPONENT32F, GL_DEPTH_COMPONENT, GL_FLOAT, 1),
    ALPHA(GL_ALPHA8, GL_ALPHA, GL_UNSIGNED_BYTE, 1)
}