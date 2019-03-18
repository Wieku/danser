package me.wieku.framework.graphics.buffers

import org.lwjgl.opengl.GL33.*

enum class VertexAttributeType(val glId: Int, val size: Int) {
    GlInt(GL_INT, 4),
    GlFloat(GL_FLOAT, 4),
    Vec2(GL_FLOAT_VEC2, 2 * 4),
    Vec3(GL_FLOAT_VEC3, 3 * 4),
    Vec4(GL_FLOAT_VEC4, 4 * 4),
    Mat2(GL_FLOAT_MAT2, 2 * 2 * 4),
    Mat23(GL_FLOAT_MAT2x3, 2 * 3 * 4),
    Mat24(GL_FLOAT_MAT2x4, 2 * 4 * 4),
    Mat3(GL_FLOAT_MAT3, 3 * 3 * 4),
    Mat32(GL_FLOAT_MAT3x2, 3 * 2 * 4),
    Mat34(GL_FLOAT_MAT3x4, 2 * 4 * 4),
    Mat4(GL_FLOAT_MAT4, 4 * 4 * 4),
    Mat42(GL_FLOAT_MAT4x2, 4 * 2 * 4),
    Mat43(GL_FLOAT_MAT4x3, 4 * 3 * 4);

    companion object {
        private var attributeMap = HashMap<Int, VertexAttributeType>()

        init {
            for (attrType in values()) {
                attributeMap[attrType.glId] = attrType
            }
        }

        fun getAttributeByGlType(type: Int) = attributeMap.getOrDefault(type, GlInt)

    }
}

data class VertexAttribute(
    val attributeName: String,
    val attributeType: VertexAttributeType,
    val attributeIndex: Int,
    var location: Int = 0
)

fun Array<VertexAttribute>.vertexSize() = this.sumBy { it.attributeType.size }