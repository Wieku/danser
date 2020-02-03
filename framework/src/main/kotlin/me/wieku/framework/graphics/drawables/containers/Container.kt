package me.wieku.framework.graphics.drawables.containers

import me.wieku.framework.graphics.drawables.Drawable
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.input.InputHandler
import me.wieku.framework.utils.MaskingInfo
import org.joml.Matrix4f
import org.joml.Vector2i
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.collections.ArrayList

open class Container() : Drawable() {

    protected val children = ArrayList<Drawable>()
    private val childrenToRemove = ArrayList<Drawable>()

    protected val maskInfo = MaskingInfo()
    var useScissor = false

    private val lock = ReentrantReadWriteLock()
    protected val accessLock = lock.readLock()
    protected val modificationLock = lock.writeLock()

    override var wasUpdated: Boolean
        get() {
            children.forEach {
                if (it.wasUpdated) return true
            }
            return false
        }
        set(value) {}

    constructor(inContext: Container.() -> Unit) : this() {
        inContext()
    }

    open fun addChild(vararg drawable: Drawable) {
        modificationLock.lock()

        children.addAll(drawable)
        drawable.forEach { it.parent = this }

        modificationLock.unlock()
    }

    open fun insertChild(drawable: Drawable, index: Int) {
        modificationLock.lock()

        children.add(index, drawable)
        drawable.parent = this

        modificationLock.unlock()
    }

    open fun removeChild(drawable: Drawable) {
        modificationLock.lock()

        children.remove(drawable)
        drawable.parent = null

        modificationLock.unlock()
    }

    override fun invalidate() {
        super.invalidate()

        accessLock.lock()

        children.forEach { it.invalidate() }

        accessLock.unlock()
    }

    override fun update() {
        super.update()
        if (useScissor) {
            maskInfo.rect.set(drawPosition.x, drawPosition.y, drawPosition.x + drawSize.x, drawPosition.y + drawSize.y)
            maskInfo.maskToLocalCoords.set(transformInfo)
        }

        accessLock.lock()

        children.forEach {
            it.update()
            if (it.canBeDeleted()) {
                childrenToRemove.add(it)
            }
        }

        accessLock.unlock()

        modificationLock.lock()

        children.removeAll(childrenToRemove)
        childrenToRemove.clear()

        modificationLock.unlock()
    }

    private val tempMatrix1 = Matrix4f()
    private val tempMatrix2 = Matrix4f()
    private val tempMatrix3 = Matrix4f()

    override fun draw(batch: SpriteBatch) {
        val scissorUsed = useScissor

        if (scissorUsed) {
            batch.pushMaskingInfo(maskInfo)
        }

        accessLock.lock()

        children.forEach { it.draw(batch) }

        accessLock.unlock()

        if (scissorUsed) {
            batch.popMaskingInfo()
        }
    }

    override fun dispose() {
        children.forEach { it.dispose() }
    }

    override fun buildInputQueue(cursorPosition: Vector2i, queue: ArrayDeque<InputHandler>) {
        super.buildInputQueue(cursorPosition, queue)

        accessLock.lock()

        children.forEach {
                it.buildInputQueue(cursorPosition, queue)
            }

        accessLock.unlock()
    }

}