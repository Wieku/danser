package me.wieku.framework.graphics

import org.lwjgl.opengl.GL33.*

enum class ShaderType(glInt: Int) {
	Vertex(GL_VERTEX_SHADER),
	Fragment(GL_VERTEX_SHADER),
	Geometry(GL_VERTEX_SHADER)
}

object ShaderHelper {

	fun loadShader(type: Int, source: String) {

	}

}