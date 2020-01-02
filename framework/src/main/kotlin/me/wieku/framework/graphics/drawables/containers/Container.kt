package me.wieku.framework.graphics.drawables.containers

import me.wieku.framework.graphics.drawables.Drawable
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.input.InputHandler
import me.wieku.framework.utils.MaskingInfo
import me.wieku.framework.utils.synchronized
import org.joml.Vector2i
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.collections.ArrayList
import kotlin.math.round

open class Container() : Drawable() {

    protected val maskInfo = MaskingInfo()

    var useScissor = false


    private val lock = ReentrantReadWriteLock()
    private val accessLock = lock.readLock()
    private val modificationLock = lock.writeLock()

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

    protected val children = ArrayList<Drawable>()

    open fun addChild(vararg drawable: Drawable) {
        modificationLock.lock()
        //children.synchronized {
            children.addAll(drawable)
        //}

        drawable.forEach { it.parent = this }
        modificationLock.unlock()
    }

    open fun insertChild(drawable: Drawable, index: Int) {
        modificationLock.lock()
        children.//synchronized {
            add(index, drawable)
        //}
        drawable.parent = this
        modificationLock.unlock()
    }

    open fun removeChild(drawable: Drawable) {
        modificationLock.lock()
        children.synchronized {
            remove(drawable)
        }

        drawable.parent = null
        modificationLock.unlock()
    }

    override fun invalidate() {
        super.invalidate()
        children.forEach { it.invalidate() }
    }

    override fun update() {
        super.update()
        maskInfo.rect.set(drawPosition.x, drawPosition.y, drawPosition.x+ round(drawSize.x), round(drawPosition.y + drawSize.y))

        accessLock.lock()

        children.forEach {
            it.update()
        }

        accessLock.unlock()

        modificationLock.lock()

        children.removeIf { it.canBeDeleted() }

        modificationLock.unlock()
    }

    override fun draw(batch: SpriteBatch) {
        val scissorUsed = useScissor

        if (scissorUsed) {
            batch.pushMaskingInfo(maskInfo)
        }

        accessLock.lock()

        //children.synchronized {
            children.forEach { it.draw(batch) }
        //}

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