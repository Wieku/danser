package me.wieku.danser

import me.wieku.danser.build.Build
import me.wieku.framework.audio.BassSystem
import me.wieku.framework.audio.Track
import me.wieku.framework.graphics.shaders.Shader
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.graphics.vertex.VertexArrayObject
import me.wieku.framework.graphics.vertex.VertexAttribute
import me.wieku.framework.graphics.vertex.VertexAttributeType
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*

import java.io.File
import java.nio.file.Paths

fun main(args: Array<String>) {
    println("Version " + Build.Version)

    glfwInit()
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, 1)
    var handle = glfwCreateWindow(1024, 400, "testdanser: " + Build.Version, 0, 0)
    glfwMakeContextCurrent(handle)
    GL.createCapabilities()

    var shader = Shader(FileHandle("assets/test.vsh", FileType.Classpath), FileHandle("assets/test.fsh", FileType.Classpath))

    println(glGetError())

    var vao = VertexArrayObject(
        6*256,
        arrayOf(
            VertexAttribute(
                "in_position",
                VertexAttributeType.Vec2,
                0
            )/*,
            VertexAttribute(
                "in_color",
                VertexAttributeType.Vec4,
                1
            )*//*,
            VertexAttribute(
                "in_tex_coord",
                VertexAttributeType.Vec2,
                2
            )*/
        )
    )

    var arr = FloatArray(6*256*2)

    var texture = Texture(FileHandle("assets/testimg.jpg", FileType.Classpath))

    texture.bind(1)

    vao.bind()
    vao.bindToShader(shader)
    vao.setData(
        floatArrayOf(
            -1f, -1f, 1f, 0f, 0f, 1f, 0f, 1f,
            1f, -1f, 0f, 1f, 0f, 1f, 1f, 1f,
            -1f, 1f, 0f, 1f, 0f, 1f, 0f, 0f,
            -1f, 1f, 0f, 1f, 0f, 1f, 0f, 0f,
            1f, -1f, 0f, 1f, 0f, 1f, 1f, 1f,
            1f, 1f, 0f, 0f, 1f, 1f, 1f, 0f
        )
    )
    vao.unbind()

    BassSystem.initSystem()
    val track = Track(FileHandle("assets/audio.mp3", FileType.Classpath))
    track.play()

    Thread {
        while (!glfwWindowShouldClose(handle)) {
            track.update()
            Thread.sleep(16)
        }
    }.start()

    while (!glfwWindowShouldClose(handle)) {

        GL11.glClearColor(0.5f, 0.5f, 0.5f, 1f)
        glClear(GL_COLOR_BUFFER_BIT)
        shader.bind()
        //shader.setUniform("tex", 1f)
        shader.setUniform("col", 1f, 1f, 0f, 1f)
        vao.bind()

        for ((i, d) in track.fftData.withIndex()){
            if (i>255) break

            val x0 = (i.toFloat()*2/255 - 1)
            val x1 = ((i+1).toFloat()*2/255 - 1)
            val y0 = -1f
            val y1 = d*2 - 1

            arr[i*12] = x0
            arr[i*12+1] = y0
            arr[i*12+2] = x1
            arr[i*12+3] = y0
            arr[i*12+4] = x0
            arr[i*12+5] = y1

            arr[i*12+6] = x0
            arr[i*12+7] = y1
            arr[i*12+8] = x1
            arr[i*12+9] = y0
            arr[i*12+10] = x1
            arr[i*12+11] = y1

        }
        vao.setData(arr)
        vao.draw()
        vao.unbind()

        shader.unbind()
        glfwPollEvents()
        glfwSwapBuffers(handle)
    }

    glfwDestroyWindow(handle)

}