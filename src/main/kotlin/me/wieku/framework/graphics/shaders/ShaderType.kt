package me.wieku.framework.graphics.shaders

import org.lwjgl.opengl.GL33.*

enum class ShaderType(val glId: Int) {
    Vertex(GL_VERTEX_SHADER),
    Fragment(GL_FRAGMENT_SHADER),
    Geometry(GL_GEOMETRY_SHADER)
}