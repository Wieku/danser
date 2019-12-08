package me.wieku.framework.gui.screen

import me.wieku.framework.graphics.containers.Container
import me.wieku.framework.math.Scaling

open class Screen : Container() {

    init {
        fillMode = Scaling.Stretch
    }

    open fun onResume(previous: Screen?) {}
    open fun onSuspend(next: Screen?) {}
    open fun onEnter(previous: Screen?) {}
    open fun onExit(next: Screen?) {}

}