package me.wieku.danser

import me.wieku.danser.build.Build
import me.wieku.framework.audio.BassSystem
import me.wieku.framework.audio.Track
import me.wieku.framework.graphics.shaders.Shader
import me.wieku.framework.graphics.vertex.VertexArrayObject
import me.wieku.framework.graphics.vertex.VertexAttribute
import me.wieku.framework.graphics.vertex.VertexAttributeType
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*

import java.io.File

fun main(args: Array<String>) {
    println("Version " + Build.Version)

    glfwInit()
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, 1)
    var handle = glfwCreateWindow(400, 400, "testdanser: " + Build.Version, 0, 0)
    glfwMakeContextCurrent(handle)
    GL.createCapabilities()

    var shader = Shader(
        """
		#version 330

		in vec2 in_position;
		in vec4 in_color;

		out vec4 i_color;
		void main()
		{
			gl_Position = vec4(in_position, 0, 1);
			i_color = in_color;
		}
	""".trimIndent(), """
		#version 330

		in vec4 i_color;
		out vec4 color;

		void main()
		{
			color = i_color;
		}
	""".trimIndent()
    )

    println(glGetError())

    var vao = VertexArrayObject(
        3,
        arrayOf(
            VertexAttribute(
                "in_position",
                VertexAttributeType.Vec2,
                0
            ),
            VertexAttribute(
                "in_color",
                VertexAttributeType.Vec4,
                1
            )
        )
    )

    vao.bind()
    vao.bindToShader(shader)
    vao.setData(
        floatArrayOf(
            -1f, -1f, 1f, 0f, 0f, 1f,
            1f, -1f, 0f, 1f, 0f, 1f,
            -1f, 1f, 0f, 0f, 1f, 1f
        )
    )
    vao.unbind()

    while (!glfwWindowShouldClose(handle)) {

        GL11.glClearColor(0.5f, 0.5f, 0.5f, 1f)
        glClear(GL_COLOR_BUFFER_BIT)
        shader.bind()
        vao.bind()
        vao.draw()
        vao.unbind()

        shader.unbind()
        glfwPollEvents()
        glfwSwapBuffers(handle)
    }

    glfwDestroyWindow(handle)

    BassSystem.initSystem()

    val track = Track(File("audio.mp3").absolutePath)
    track.play()

    //Bass.BASS_ChannelSetSync(stream.asInt(), BASS_SYNC.BASS_SYNC_END, 0, { a, b, c, d-> sema.release()}, null)

    Thread {
        while (true) {
            track.update()
            println(track.beat)
            Thread.sleep(100)
        }
    }.run()
}