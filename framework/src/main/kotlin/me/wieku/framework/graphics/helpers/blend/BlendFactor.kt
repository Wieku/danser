package me.wieku.framework.graphics.helpers.blend

import me.wieku.framework.utils.EnumWithId
import org.lwjgl.opengl.GL33.*

/**
 * Documentation about these factors can be found [here](https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glBlendFunc.xhtml)
 */
enum class BlendFactor(override val enumId: Int): EnumWithId {
    Zero(GL_ZERO),
    One(GL_ONE),
    SrcColor(GL_SRC_COLOR),
    OneMinusSrcColor(GL_ONE_MINUS_SRC_COLOR),
    DstColor(GL_DST_COLOR),
    OneMinusDstColor(GL_ONE_MINUS_DST_COLOR),
    SrcAlpha(GL_SRC_ALPHA),
    OneMinusSrcAlpha(GL_ONE_MINUS_SRC_ALPHA),
    DstAlpha(GL_DST_ALPHA),
    OneMinusDstAlpha(GL_ONE_MINUS_DST_ALPHA),
    ConstantColor(GL_CONSTANT_COLOR),
    OneMinusConstantColor(GL_ONE_MINUS_CONSTANT_COLOR),
    ConstantAlpha(GL_CONSTANT_ALPHA),
    OneMinusConstantAlpha(GL_ONE_MINUS_CONSTANT_ALPHA),
    SrcAlphaSaturate(GL_SRC_ALPHA_SATURATE),
    Src1Color(GL_SRC1_COLOR),
    OneMinusSrc1Color(GL_ONE_MINUS_SRC1_COLOR),
    Src1Alpha(GL_SRC1_ALPHA),
    OneMinusSrc1Alpha(GL_ONE_MINUS_SRC1_ALPHA)
}