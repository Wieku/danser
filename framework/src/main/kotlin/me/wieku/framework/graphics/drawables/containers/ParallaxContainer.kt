package me.wieku.framework.graphics.drawables.containers

import me.wieku.framework.input.InputManager
import me.wieku.framework.math.Origin
import org.joml.Vector2f
import org.joml.Vector2i
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.math.max
import kotlin.math.min

open class ParallaxContainer() : Container(), KoinComponent {

    private val lastPos = Vector2i(-65535)
    private val inputManager: InputManager by inject()
    private var wasUpdatedOnce = false

    var parallaxAmount = 0f

    constructor(inContext: ParallaxContainer.() -> Unit) : this() {
        inContext()
    }

    init {
        origin = Origin.Custom
        customOrigin = Vector2f(0.5f)
    }

    override fun update() {
        val pos = inputManager.getPosition()
        if (pos != lastPos && wasUpdatedOnce) {
            val posX = -(max(0, min(pos.x, drawSize.x.toInt())) / drawSize.x - 0.5f)
            val posY = -(max(0, min(pos.y, drawSize.y.toInt())) / drawSize.y - 0.5f)
            customOrigin.set(posX * parallaxAmount + 0.5f, posY * parallaxAmount + 0.5f)
            invalidate()
            lastPos.set(pos)
        }

        super.update()
    }

    override fun updateDrawable() {
        scale.mul(1f + parallaxAmount)
        super.updateDrawable()
        scale.mul(1f / (1f + parallaxAmount))
        wasUpdatedOnce = true
    }

}