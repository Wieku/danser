package me.wieku.framework.graphics.buffers

import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.utils.Disposable
import org.lwjgl.opengl.GL33.*
import java.util.*

class Framebuffer(private var width: Int, private var height: Int, private var hasDepth: Boolean = false) : Disposable {

    private var stack = ArrayDeque<Int>()

    var texture = Texture(width, height)
        private set

    var id: Int
        private set

    private var depthID: Int = 0

    init {
        texture = Texture(width, height)

        id = glGenFramebuffers()

        bind()
        texture.bind(0)
        glFramebufferTextureLayer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, texture.getID(), 0, 0)

        if (hasDepth) {
            depthID = glGenRenderbuffers()
            glBindRenderbuffer(GL_RENDERBUFFER, depthID)
            glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height)
            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthID)
            glBindRenderbuffer(GL_RENDERBUFFER, 0)
        }

        unbind()

    }

    fun bind() {
        stack.push(glGetInteger(GL_FRAMEBUFFER_BINDING))
        glBindFramebuffer(GL_FRAMEBUFFER, id)
    }

    fun unbind() {
        var binding = stack.pop()
        glBindFramebuffer(GL_FRAMEBUFFER, binding ?: 0)
    }

    override fun dispose() {
        glDeleteFramebuffers(id)
        if (hasDepth) {
            glDeleteRenderbuffers(depthID)
        }
    }

}