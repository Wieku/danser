package me.wieku.framework.math.view

import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Rectanglef
import org.joml.Vector2f

class Camera {
    private var screenRect = Rectanglef()
    var projection = Matrix4f()
        private set

    var view = Matrix4f()
        private set

    var projectionView = Matrix4f()
        private set

    var invProjectionView = Matrix4f()
        private set

    var viewDirty = true

    var origin = Vector2f()
        set(value) {
            viewDirty = true
            field = value
        }

    var position = Vector2f()
        set(value) {
            viewDirty = true
            field = value
        }

    var rotation = 0f
        set(value) {
            viewDirty = true
            field = value
        }

    var scale = Vector2f(1f, 1f)
        set(value) {
            viewDirty = true
            field = value
        }

    //var rebuildCache: bool
    //var cache: []Matrix4f

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

        if (yDown) {
            projection.identity().ortho(screenRect.minX, screenRect.maxX, screenRect.minY, screenRect.maxY, 1f, -1f)
        } else {
            projection.identity().ortho(screenRect.minX, screenRect.maxX, screenRect.minY, screenRect.maxY, -1f, 1f)
        }

        //rebuildCache = true
        viewDirty = true
    }

    /*fun SetOsuViewport(width: Int, height: Int, scale: Float) {
        scl := ((height) * 900.0 / 1080.0) / 384.0 * scale

        if 512.0/384.0 > (width)/(height) {
            scl = ((width) * 900.0 / 1080.0) / 512.0 * scale
        }

        SetViewport(int(settings.Graphics.GetWidth()), int(settings.Graphics.GetHeight()), true)
        SetOrigin(NewVec2d(512.0/2, 384.0/2))
        SetScale(NewVec2d(scl, scl))
        Update()

        rebuildCache = true
        viewDirty = true
    }*/

    fun setViewportF(x: Int, y: Int, width: Int, height: Int, yDown: Boolean = true) {
        screenRect.minX = x.toFloat()
        screenRect.maxX = width.toFloat() + x.toFloat()
        screenRect.minY = if (yDown) height.toFloat()+y.toFloat() else y.toFloat()
        screenRect.maxY = if (yDown) y.toFloat() else height.toFloat() + y.toFloat()

        projection.identity().ortho(screenRect.minX, screenRect.maxX, screenRect.minY, screenRect.maxY, -1f, 1f)
        //rebuildCache = true
        viewDirty = true
    }

    fun calculateView() {
        view.identity().translate(position.x, position.y, 0f).rotateZ(rotation).scale(scale.x, scale.y, 1f)
            .translate(origin.x, origin.y, 0f)
    }

    fun update() {
        if (viewDirty) {
            calculateView()
            projectionView.set(projection).mul(view)
            invProjectionView.set(projectionView).invert()
            //rebuildCache = true
            viewDirty = false
        }
    }

    /*fun GenRotated(rotations int, rotOffset Float) []Matrix4f
    {

        if len(cache) != rotations || rebuildCache {
            if len(cache) != rotations {
                cache = make([] Matrix4f, rotations)
            }

            for i : = 0; i < rotations; i++ {
            cache[i] = projection.Mul4(mgl32.HomogRotate3DZ((i) * (rotOffset))).Mul4(view)
        }
            rebuildCache = false
        }

        return cache
    }*/

    /*fun unproject(screenPos: Vector2f): Vector2f
    {
        val res = invProjectionView.Mul4x1(mgl32.Vec4{ ((screenPos.X + screenRect.minX) / screenRect.maxX), -((screenPos.Y+screenRect.maxY) / screenRect.minY), 0.0, 1.0 })
        return NewVec2d((res[0]), (res[1]))
    }

    fun getWorldRect(): Rectanglef
    {
        res : = invProjectionView.Mul4x1(mgl32.Vec4{ -1.0, 1.0, 0.0, 1.0 })
        var rectangle Rectangle
        rectangle.minX = (res[0])
        rectangle.minY = (res[1])
        res = invProjectionView.Mul4x1(mgl32.Vec4 { 1.0, -1.0, 0.0, 1.0 })
        rectangle.maxX = (res[0])
        rectangle.maxY = (res[1])
        if rectangle.minY > rectangle.maxY {
            a : = rectangle.minY
            rectangle.minY, rectangle.maxY = rectangle.maxY, a
        }
        return rectangle
    }*/

}