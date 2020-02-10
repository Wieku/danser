package me.wieku.framework.graphics.helpers.blend

import me.wieku.framework.utils.EnumWithId
import org.lwjgl.opengl.GL33.*

/**
 * Documentation about these equations can be found [here](https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glBlendEquation.xhtml)
 */
enum class BlendEquation(override val enumId: Int): EnumWithId {
    Add(GL_FUNC_ADD),
    Subtract(GL_FUNC_SUBTRACT),
    ReverseSubtract(GL_FUNC_REVERSE_SUBTRACT),
    Min(GL_MIN),
    Max(GL_MAX)
}