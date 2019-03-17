package me.wieku.danser

import me.wieku.danser.build.Build
import me.wieku.framework.audio.BassSystem
import me.wieku.framework.audio.Track
import me.wieku.framework.graphics.shaders.Shader
import me.wieku.framework.graphics.sprite.SpriteBatch
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.graphics.vertex.IndexBufferObject
import me.wieku.framework.graphics.vertex.VertexArrayObject
import me.wieku.framework.graphics.vertex.VertexAttribute
import me.wieku.framework.graphics.vertex.VertexAttributeType
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*

fun main(args: Array<String>) {
    println("Version " + Build.Version)

    glfwInit()
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, 1)
    var handle = glfwCreateWindow(500, 600, "testdanser: " + Build.Version, 0, 0)
    glfwMakeContextCurrent(handle)
    GL.createCapabilities()

    println(glGetError())

    var offt = FloatArray(200)
    var batch = SpriteBatch()

    val color = Vector4f(0.2f, 0.2f, 0.5f, 1f)

    var texture = Texture(1, 1)
    texture.bind(1)
    texture.setData(0, 0, 1, 1, byteArrayOf(255.toByte(), 255.toByte(), 255.toByte(), 255.toByte()))


    BassSystem.initSystem()
    val track = Track(FileHandle("assets/audio.mp3", FileType.Classpath))
    track.play(0.1f)

    Thread {
        while (!glfwWindowShouldClose(handle)) {
            track.update()
            for ((i, d) in track.fftData.withIndex()) {
                if (i >= offt.size) break
                offt[i] = Math.max(d, offt[i] - 0.001f * 16)
            }
            Thread.sleep(16)
        }
    }.start()

    while (!glfwWindowShouldClose(handle)) {

        GL11.glClearColor(0.1f, 0.1f, 0.1f, 1f)
        glClear(GL_COLOR_BUFFER_BIT)

        batch.begin()

        for ((i, d) in offt.withIndex()) {
            batch.draw(texture, ((i.toFloat()+0.5f) * 2) / offt.size - 1, d-1, 2f/offt.size, d*2, color)
        }

        batch.end()

        glfwPollEvents()
        glfwSwapBuffers(handle)
    }

    glfwDestroyWindow(handle)

}