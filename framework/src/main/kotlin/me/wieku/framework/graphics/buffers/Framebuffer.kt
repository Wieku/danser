package me.wieku.framework.graphics.buffers

import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.utils.Disposable
import org.joml.Vector4f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL33.*
import java.util.*

class Framebuffer(private var width: Int, private var height: Int, defaultColor: Boolean = true) : Disposable {

    var id: Int
        private set

    private var colorAttachmentsUsed = 0
    private var renderBuffers = ArrayList<Int>()
    private var textureTargets = HashMap<String, Texture>()

    init {
        id = glGenFramebuffers()
        bind(clear = false)
        if (defaultColor) {
            addTextureTarget("color", FramebufferTarget.RGBA)
        }

        unbind()
    }

    fun addRenderbuffer(target: FramebufferTarget) {
        val rbID = glGenRenderbuffers()
        glBindRenderbuffer(GL_RENDERBUFFER, rbID)
        glRenderbufferStorage(GL_RENDERBUFFER, target.textureFormat.format, width, height)

        var targetId = target.attachment

        if (targetId == GL_COLOR_ATTACHMENT0) {
            targetId += colorAttachmentsUsed++
        }

        glFramebufferRenderbuffer(GL_FRAMEBUFFER, targetId, GL_RENDERBUFFER, rbID)
        glBindRenderbuffer(GL_RENDERBUFFER, 0)

        renderBuffers.add(rbID)
    }

    fun addTextureTarget(name: String, target: FramebufferTarget) {
        if (textureTargets.containsKey(name)) {
            throw IllegalStateException("Texture attachment with that name already exists")
        }

        val texture = Texture(width, height, format = target.textureFormat)

        var targetId = target.attachment

        if (targetId == GL_COLOR_ATTACHMENT0) {
            targetId += colorAttachmentsUsed++
        }

        glFramebufferTextureLayer(GL_FRAMEBUFFER, targetId, texture.getID(), 0, 0)
        textureTargets[name] = texture
    }

    fun getTexture(name: String = "color"): Texture? {
        return textureTargets[name]
    }

    fun bind(clear: Boolean = true, clearColor: Vector4f = Vector4f(0f, 0f, 0f, 1f)) {
        stack.push(glGetInteger(GL_FRAMEBUFFER_BINDING))
        glBindFramebuffer(GL_FRAMEBUFFER, id)
        if (clear) {
            GL11.glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w)
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        }
    }

    fun unbind() {
        val binding = stack.pop()
        glBindFramebuffer(GL_FRAMEBUFFER, binding ?: 0)
    }

    override fun dispose() {
        renderBuffers.forEach { glDeleteRenderbuffers(it) }
        textureTargets.values.forEach { it.dispose() }
        glDeleteFramebuffers(id)
    }

    /**
     * Companion to store framebuffer stack (usefull to restore the context of the previous one)
     */
    private companion object {
        private var stack = ArrayDeque<Int>()
    }

}