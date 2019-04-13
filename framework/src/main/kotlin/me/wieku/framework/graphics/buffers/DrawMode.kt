package me.wieku.framework.graphics.buffers

import org.lwjgl.opengl.GL15.*

enum class DrawMode(val glEnumID: Int) {
    StaticDraw(GL_STATIC_DRAW),
    DynamicDraw(GL_DYNAMIC_DRAW),
    StreamDraw(GL_STREAM_DRAW),
    StaticRead(GL_STATIC_READ),
    DynamicRead(GL_DYNAMIC_READ),
    StreamRead(GL_STREAM_READ),
    StaticCopy(GL_STATIC_COPY),
    DynamicCopy(GL_DYNAMIC_COPY),
    StreamCopy(GL_STREAM_COPY),
}