package me.wieku.framework.math.view

import org.joml.Matrix4f
import org.joml.Rectanglef
import org.joml.Vector2f

class Camera {
    private var screenRect = Rectanglef()

    private val projection = Matrix4f()

    private val view = Matrix4f()

    val projectionView = Matrix4f()

    val invProjectionView = Matrix4f()

    private var viewDirty = true
    private var projectionDirty = true

    var origin = Vector2f()
        set(value) {
            if (field == value) return

            viewDirty = true
            field = value
        }

    var position = Vector2f()
        set(value) {
            if (field == value) return

            viewDirty = true
            field = value
        }

    var rotation = 0f
        set(value) {
            if (field == value) return

            viewDirty = true
            field = value
        }

    var scale = Vector2f(1f, 1f)
        set(value) {
            if (field == value) return

            viewDirty = true
            field = value
        }

    fun setViewport(width: Int, height: Int, yDown: Boolean = true) {
        screenRect.minX = -width.toFloat() / 2
        screenRect.maxX = width.toFloat() / 2

        if (yDown) {
            screenRect.minY = height.toFloat() / 2
            screenRect.maxY = -height.toFloat() / 2
        } else {
            screenRect.minY = -height.toFloat() / 2
            screenRect.maxY = height.toFloat() / 2
        }

        projection.identity().ortho(screenRect.minX, screenRect.maxX, screenRect.minY, screenRect.maxY, 1f, -1f)

        projectionDirty = true
    }

    fun setViewport(x: Int, y: Int, width: Int, height: Int, yDown: Boolean = true) {
        screenRect.minX = x.toFloat()
        screenRect.maxX = width.toFloat() + x.toFloat()
        screenRect.minY = if (yDown) height.toFloat() + y.toFloat() else y.toFloat()
        screenRect.maxY = if (yDown) y.toFloat() else height.toFloat() + y.toFloat()

        projection.identity().ortho(screenRect.minX, screenRect.maxX, screenRect.minY, screenRect.maxY, 1f, -1f)

        projectionDirty = true
    }

    fun calculateView() {
        view.identity().translate(position.x, position.y, 0f).rotateZ(rotation).scale(scale.x, scale.y, 1f)
            .translate(-origin.x, -origin.y, 0f)
    }

    fun update() {
        if (viewDirty || projectionDirty) {
            if (viewDirty) {
                calculateView()
            }

            projectionView.set(projection).mul(view)
            invProjectionView.set(projectionView).invert()

            viewDirty = false
            projectionDirty = false
        }
    }

}