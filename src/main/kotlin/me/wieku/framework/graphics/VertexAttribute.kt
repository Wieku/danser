package me.wieku.framework.graphics

import org.lwjgl.opengl.GL33.*

enum class VertexAttributeType(val glId: Int) {
	GlInt(GL_INT),
	GlFloat(GL_FLOAT),
	Vec2(GL_FLOAT_VEC2),
	Vec3(GL_FLOAT_VEC3),
	Vec4(GL_FLOAT_VEC4),
	Mat2(GL_FLOAT_MAT2),
	Mat23(GL_FLOAT_MAT2x3),
	Mat24(GL_FLOAT_MAT2x4),
	Mat3(GL_FLOAT_MAT3),
	Mat32(GL_FLOAT_MAT3x2),
	Mat34(GL_FLOAT_MAT3x4),
	Mat4(GL_FLOAT_MAT4),
	Mat42(GL_FLOAT_MAT4x2),
	Mat43(GL_FLOAT_MAT4x3);

	companion object {
		private var attributeMap = HashMap<Int, VertexAttributeType>()

		init {
			for (attrType in values()) {
				attributeMap[attrType.glId] = attrType
			}
		}

		fun getAttributeByGlType(type: Int) = attributeMap[type]!!

	}
}

data class VertexAttribute(val attributeName: String, val attributeType: VertexAttributeType, val attributeIndex: Int, var location: Int = 0)