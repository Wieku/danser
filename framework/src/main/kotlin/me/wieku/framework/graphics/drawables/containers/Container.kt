package me.wieku.framework.graphics.drawables.containers

import me.wieku.framework.graphics.drawables.Drawable
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.input.InputHandler
import me.wieku.framework.utils.MaskingInfo
import me.wieku.framework.utils.synchronized
import org.joml.Vector2i
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.round

open class Container(): Drawable() {

    protected val maskInfo = MaskingInfo()

    var useScissor = false

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

    open fun addChild(vararg drawable: Drawable) {
        children.synchronized {
            addAll(drawable)
        }

        drawable.forEach { it.parent = this }
    }

    open fun insertChild(drawable: Drawable, index: Int) {
        children.synchronized {
            add(index, drawable)
        }
        drawable.parent = this
    }

    open fun removeChild(drawable: Drawable) {
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

    override fun updateDrawable() {
        super.updateDrawable()
        maskInfo.rect.set(drawPosition.x, drawPosition.y, drawPosition.x+ round(drawSize.x), round(drawPosition.y + drawSize.y))
    }

    override fun draw(batch: SpriteBatch) {
        val scissorUsed = useScissor

        if (scissorUsed) {
            batch.pushMaskingInfo(maskInfo)
        }

        children.synchronized {
            forEach { it.draw(batch) }
        }

        if (scissorUsed) {
            batch.popMaskingInfo()
        }
    }

    override fun dispose() {
        children.forEach { it.dispose() }
    }

    override fun buildInputQueue(cursorPosition: Vector2i, queue: ArrayDeque<InputHandler>) {
        super.buildInputQueue(cursorPosition, queue)
        children.synchronized {
            forEach {
                it.buildInputQueue(cursorPosition, queue)
            }
        }
    }

}