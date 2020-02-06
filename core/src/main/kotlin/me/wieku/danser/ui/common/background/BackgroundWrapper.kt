package me.wieku.danser.ui.common.background

import me.wieku.framework.graphics.drawables.containers.Container
import me.wieku.framework.math.Scaling

class BackgroundWrapper: Container() {

    init {
        fillMode = Scaling.Stretch
        inheritColor = false
    }

    private var firstTime = true

    override var wasUpdated: Boolean
        get() {
            if (firstTime) {
                firstTime = false
                return true
            }

            children.forEach {
                if (it.transforms.isNotEmpty()) return true
            }

            return false
        }
        set(_) {}

}