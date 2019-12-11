package me.wieku.framework.gui.screen

import me.wieku.framework.graphics.containers.Container
import me.wieku.framework.math.Scaling

open class Screen : Container() {

    init {
        fillMode = Scaling.Stretch
    }

    open fun onEnter(previous: Screen?) {}
    open fun onExit(next: Screen?) {}

}