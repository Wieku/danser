package me.wieku.framework.graphics.drawables.containers

import me.wieku.framework.graphics.drawables.Drawable
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.utils.synchronized

open class Container(): Drawable() {

    override var wasUpdated: Boolean
        get() {
            children.forEach {
                if (it.wasUpdated) return true
            }
            return false
        }
        set(value) {}

    constructor(inContext: Container.() -> Unit):this(){
        inContext()
    }

    protected val children = ArrayList<Drawable>()

    fun addChild(vararg drawable: Drawable) {
        children.synchronized {
            addAll(drawable)
        }

        drawable.forEach { it.parent = this }
    }

    fun insertChild(drawable: Drawable, index: Int) {
        children.synchronized {
            add(index, drawable)
        }
        drawable.parent = this
    }

    fun removeChild(drawable: Drawable) {
        children.synchronized {
            remove(drawable)
        }

        drawable.parent = null
    }

    override fun invalidate() {
        super.invalidate()
        children.forEach { it.invalidate() }
    }

    override fun update() {
        super.update()
        children.synchronized {
            forEach { it.update() }
        }
        children.synchronized {
            removeIf { it.canBeDeleted() }
        }
    }

    override fun draw(batch: SpriteBatch) {
        children.synchronized {
            forEach { it.draw(batch) }
        }
    }

    override fun dispose() {
        children.forEach { it.dispose() }
    }

}