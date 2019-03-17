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

    var shader = Shader(FileHandle("assets/test.vsh", FileType.Classpath), FileHandle("assets/test.fsh", FileType.Classpath))

    println(glGetError())

    var offt = FloatArray(200)
    var arr = FloatArray(4 * offt.size * 2)
    var iarr = ShortArray(6 * offt.size)

    var vao = VertexArrayObject(
        4 * offt.size,
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

    var batch = SpriteBatch()

    var ibo = IndexBufferObject(6 * offt.size)

    var texture = Texture(FileHandle("assets/testimg.jpg", FileType.Classpath))

    texture.bind(1)

    vao.bind()
    vao.bindToShader(shader)
    vao.unbind()

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
        shader.bind()
        //shader.setUniform("tex", 1f)
        shader.setUniform("col", 0.2f, 0.2f, 0.5f, 1f)
        vao.bind()
        ibo.bind()
        for ((i, d) in offt.withIndex()) {
            val x0 = (i.toFloat() * 2 / offt.size - 1)
            val x1 = ((i + 1).toFloat() * 2 / offt.size - 1)
            val y0 = -1f
            val y1 = d * 2 - 1

            arr[i * 8] = x0
            arr[i * 8 + 1] = y0
            arr[i * 8 + 2] = x1
            arr[i * 8 + 3] = y0
            arr[i * 8 + 4] = x0
            arr[i * 8 + 5] = y1
            arr[i * 8 + 6] = x1
            arr[i * 8 + 7] = y1

            iarr[i * 6] = (i*4).toShort()
            iarr[i * 6 + 1] = (i*4+1).toShort()
            iarr[i * 6 + 2] = (i*4+2).toShort()
            iarr[i * 6 + 3] = (i*4+2).toShort()
            iarr[i * 6 + 4] = (i*4+1).toShort()
            iarr[i * 6 + 5] = (i*4+3).toShort()

        }
        vao.setData(arr)
        ibo.setData(iarr)
        ibo.draw()
        ibo.unbind()
        vao.unbind()

        shader.unbind()
        glfwPollEvents()
        glfwSwapBuffers(handle)
    }

    glfwDestroyWindow(handle)

}