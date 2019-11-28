package me.wieku.framework.graphics.drawables

import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import me.wieku.framework.utils.Disposable
import org.joml.Vector2f
import org.joml.Vector4f

abstract class Drawable(): Disposable {

    constructor(inContext: Drawable.() -> Unit):this(){
        inContext()
    }

    /**
     * Relative position of drawable
     */
    var position = Vector2f()

    var fixedPosition = Vector2f()

    //var computedPosition = Vector2f()

    var drawPosition = Vector2f()


    var childOffset = Vector2f()


    var anchor = Origin.Centre

    var customAnchor = Vector2f()

    var origin = Origin.Centre

    var customOrigin = Vector2f()

    //var computedOrigin = Vector2f()

    var drawOrigin = Vector2f()


    var inheritScale = true

    var scale = Vector2f(1f)

    var drawScale = Vector2f(1f)


    var fillMode = Scaling.None

    var size = Vector2f(1f)

    //var computedSize = Vector2f()

    var drawSize = Vector2f()

    var color = Vector4f(1f, 1f, 1f, 1f)

    /**
     * parent of this drawable
     */
    var parent: Drawable? = null

    var isValid = false
        private set

    open fun invalidate() {
        isValid = false
    }

    open fun update() {
        if (!isValid) {
            updateDrawable()
        }
    }

    private fun updateDrawable() {

        drawScale.set(scale)

        if (parent != null) {
            parent?.let {

                /*if (inheritScale) {
                    drawScale.mul(it.drawScale)
                }*/

                //computedSize.set(fillMode.apply(size.x, size.y, it.computedSize.x, it.computedSize.y))
                drawSize.set(fillMode.apply(size.x, size.y, it.drawSize.x, it.drawSize.y)).mul(drawScale)

                //drawSize.set(computedSize).mul(drawScale)

                /*computedOrigin.set(if (origin == Origin.Custom) customOrigin else origin.offset).mul(computedSize)

                drawOrigin.set(computedOrigin).mul(drawScale)*/

                drawOrigin.set(if (origin == Origin.Custom) customOrigin else origin.offset).mul(drawSize)

                //drawOrigin.set(computedOrigin).mul(drawScale)

                val anchorV = Vector2f()

                if (anchor != Origin.None) {
                    anchorV.set(if (anchor == Origin.Custom) customAnchor else anchor.offset)

                    //anchorV.mul(it.computedSize).add(it.computedPosition).add(it.childOffset)
                    anchorV.mul(it.drawSize).add(it.drawPosition).add(it.childOffset)

                }

                //computedPosition.set(position).sub(computedOrigin).add(anchorV)
                drawPosition.set(position).sub(drawOrigin).add(anchorV)
                return
            }
        } else {
            /*computedSize.set(size.x, size.y)
            drawSize.set(computedSize).mul(drawScale)

            computedOrigin.set(if (origin == Origin.Custom) customOrigin else origin.offset).mul(computedSize)
            drawOrigin.set(computedOrigin).mul(drawScale)

            computedPosition.set(position).sub(computedOrigin)
            drawPosition.set(position).sub(drawOrigin)*/

            drawSize.set(size.x, size.y).mul(drawScale)
            drawOrigin.set(if (origin == Origin.Custom) customOrigin else origin.offset).mul(drawSize)
            drawPosition.set(position).sub(drawOrigin)
        }
    }

    abstract fun draw(batch: SpriteBatch)
}