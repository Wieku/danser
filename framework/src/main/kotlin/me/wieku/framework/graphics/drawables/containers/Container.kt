package me.wieku.framework.graphics.drawables.containers

import me.wieku.framework.graphics.drawables.Drawable
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.input.InputHandler
import me.wieku.framework.utils.MaskingInfo
import me.wieku.framework.utils.fastForEach
import org.joml.Matrix4f
import org.joml.Vector2i
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.ArrayList

open class Container() : Drawable() {

    protected val children = CopyOnWriteArrayList<Drawable>()
    private val childrenToRemove = ArrayList<Drawable>()

    val maskingInfo = MaskingInfo()
    var useScissor = false

    override var wasUpdated: Boolean
        get() {
            children.fastForEach {
                if (it.wasUpdated) return true
            }
            return false
        }
        set(_) {}

    val childNumber: Int
        get() = children.size

    constructor(inContext: Container.() -> Unit) : this() {
        inContext()
    }

    open fun addChild(vararg drawable: Drawable) {
        children.addAll(drawable)
        drawable.forEach { it.parent = this }
    }

    open fun insertChild(drawable: Drawable, index: Int) {
        children.add(index, drawable)
        drawable.parent = this
    }

    open fun removeChild(drawable: Drawable) {

        children.remove(drawable)
        drawable.parent = null
    }

    override fun invalidate() {
        super.invalidate()

        children.fastForEach { it.invalidate() }
    }

    override fun update() {
        super.update()
        if (useScissor) {
            maskingInfo.rect.set(drawPosition.x, drawPosition.y, drawPosition.x + drawSize.x, drawPosition.y + drawSize.y)
            maskingInfo.maskToLocalCoords.set(transformInfo)
        }

        children.forEach {
            it.update()
            if (it.canBeDeleted()) {
                childrenToRemove.add(it)
            }
        }

        children.removeAll(childrenToRemove)
        childrenToRemove.clear()
    }

    private val tempMatrix1 = Matrix4f()
    private val tempMatrix2 = Matrix4f()
    private val tempMatrix3 = Matrix4f()

    override fun draw(batch: SpriteBatch) {
        val scissorUsed = useScissor

        if (scissorUsed) {
            batch.pushMaskingInfo(maskingInfo)
        }

        children.fastForEach { it.draw(batch) }

        if (scissorUsed) {
            batch.popMaskingInfo()
        }
    }

    override fun dispose() {
        children.fastForEach { it.dispose() }
    }

    override fun buildInputQueue(cursorPosition: Vector2i, queue: ArrayDeque<InputHandler>) {
        super.buildInputQueue(cursorPosition, queue)

        children.fastForEach {
                it.buildInputQueue(cursorPosition, queue)
            }
    }

}