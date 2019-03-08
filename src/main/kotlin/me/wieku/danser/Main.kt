package me.wieku.danser

import me.wieku.danser.build.Build
import me.wieku.framework.audio.BassSystem
import me.wieku.framework.audio.Track
import me.wieku.framework.graphics.Shader
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL

import java.io.File

fun main(args: Array<String>) {
	println("Version " + Build.Version)

	glfwInit()
	glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
	glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
	glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
	glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, 1)
	var handle = glfwCreateWindow(1, 1, "testdanser: " + Build.Version, 0, 0)
	glfwMakeContextCurrent(handle)
	GL.createCapabilities()

	var shader = Shader("""
		#version 330

		in vec3 in_position;
		in vec2 in_tex_coord;

		out vec2 tex_coord;
		void main()
		{
			gl_Position = vec4(in_position, 1);
			tex_coord = in_tex_coord;
		}
	""".trimIndent(), """
		#version 330

		uniform sampler2DArray tex;

		in vec2 tex_coord;
		out vec4 color;

		void main()
		{
    		vec4 in_color = texture(tex, vec3(tex_coord, 0));
			color = in_color;
		}
	""".trimIndent())

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