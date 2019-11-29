package me.wieku.framework.graphics.containers

import me.wieku.framework.graphics.drawables.Drawable
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch

open class Container(): Drawable() {

    constructor(inContext: Container.() -> Unit):this(){
        inContext()
    }

    protected val children = ArrayList<Drawable>()

    fun addChild(vararg drawable: Drawable) {
        children.addAll(drawable)
        drawable.forEach { it.parent = this }
    }

    fun insertChild(drawable: Drawable, index: Int) {
        children.add(index, drawable)
        drawable.parent = this
    }

    fun removeChild(drawable: Drawable) {
        children.remove(drawable)
        drawable.parent = null
    }

    override fun invalidate() {
        super.invalidate()
        children.forEach { it.invalidate() }
    }

    override fun update() {
        super.update()
        children.forEach { it.update() }
        children.removeIf { it.canBeDeleted() }
    }

    override fun draw(batch: SpriteBatch) {
        children.forEach { it.draw(batch) }
    }

    override fun dispose() {
        children.forEach { it.dispose() }
    }

}