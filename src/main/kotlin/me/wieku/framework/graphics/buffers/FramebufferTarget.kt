package me.wieku.framework.graphics.buffers

import me.wieku.framework.graphics.textures.TextureFormat
import org.lwjgl.opengl.GL30.*

enum class FramebufferTarget(val attachment: Int, val textureFormat: TextureFormat) {
    RGB(GL_COLOR_ATTACHMENT0, TextureFormat.RGB),
    RGB32F(GL_COLOR_ATTACHMENT0, TextureFormat.RGBA32F),
    RGBA(GL_COLOR_ATTACHMENT0, TextureFormat.RGBA),
    RGBA32F(GL_COLOR_ATTACHMENT0, TextureFormat.RGBA32F),
    DEPTH(GL_DEPTH_ATTACHMENT, TextureFormat.DEPTH),
    ALPHA(GL_COLOR_ATTACHMENT0, TextureFormat.ALPHA)
}