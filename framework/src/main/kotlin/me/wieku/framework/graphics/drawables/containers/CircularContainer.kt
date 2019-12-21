package me.wieku.framework.graphics.drawables.containers

import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.utils.MaskingInfo
import org.joml.Vector2f
import org.joml.Vector2i
import kotlin.math.min

open class CircularContainer(): RoundedEdgeContainer() {

    private val maskInfo = MaskingInfo()

    constructor(inContext: CircularContainer.() -> Unit):this(){
        inContext()
    }

    init {
        radius = 1f
    }

}