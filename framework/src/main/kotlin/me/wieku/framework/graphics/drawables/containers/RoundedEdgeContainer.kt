package me.wieku.framework.graphics.drawables.containers

import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.utils.MaskingInfo
import org.joml.Vector2f
import org.joml.Vector2i
import kotlin.math.min

open class RoundedEdgeContainer(): Container() {

    var radius = 0f

    init {
        useScissor = true
    }

    constructor(inContext: RoundedEdgeContainer.() -> Unit):this(){
        inContext()
    }

    override fun update() {
        super.update()
        maskInfo.radius = min(drawSize.x, drawSize.y) * radius / 2
    }

    override fun draw(batch: SpriteBatch) {
        batch.pushMaskingInfo(maskInfo)
        super.draw(batch)
        batch.popMaskingInfo()
    }

    private val tempVec = Vector2f()
    override fun isCursorIn(cursorPosition: Vector2i): Boolean {
        return super.isCursorIn(cursorPosition) && tempVec.set(drawSize).mul(0.5f).sub(cursorPosition.x.toFloat(), cursorPosition.y.toFloat()).add(drawPosition).length() <= maskInfo.radius
    }

}