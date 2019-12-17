package me.wieku.framework.graphics.drawables.containers

import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.utils.MaskingInfo
import kotlin.math.min

class CircularContainer(): Container() {

    private val maskInfo = MaskingInfo()

    constructor(inContext: CircularContainer.() -> Unit):this(){
        inContext()
    }

    override fun update() {
        super.update()
        maskInfo.rect.set(drawPosition.x, drawPosition.y, drawPosition.x+drawSize.x, drawPosition.y + drawSize.y)
        maskInfo.radius = min(drawSize.x, drawSize.y)/2
    }

    override fun draw(batch: SpriteBatch) {
        batch.pushMaskingInfo(maskInfo)
        super.draw(batch)
        batch.popMaskingInfo()
    }

}