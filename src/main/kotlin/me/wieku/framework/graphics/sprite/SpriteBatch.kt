package me.wieku.framework.graphics.sprite

import me.wieku.framework.graphics.shaders.Shader
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import org.lwjgl.opengl.GL33.*

class SpriteBatch {

    private var shader: Shader

    init {
        var location = SpriteBatch::class.java.getPackage().name.replace(".", "/") + "/res/sprite"
        shader = Shader(FileHandle("$location.vsh", FileType.Classpath), FileHandle("$location.fsh", FileType.Classpath))
    }

    private var preSFactor: Int = 0
    private var preDFactor: Int = 0

    fun begin() {

        preSFactor = glGetInteger(GL_BLEND_SRC_ALPHA)
        preDFactor = glGetInteger(GL_BLEND_SRC_ALPHA)

        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)
    }

    fun end() {

        glBlendFunc(preSFactor, preDFactor)
    }

}