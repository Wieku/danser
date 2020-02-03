package me.wieku.danser.graphics.drawables.triangles

enum class TriangleDirection(val directionX: Float, val directionY: Float) {
    Left(-1.0f, 0.0f),
    Right(1.0f, 0.0f),
    Up(0.0f, -1.0f),
    Down(0.0f, 1.0f)
}