package me.wieku.framework.graphics.drawables.containers

open class CircularContainer(): RoundedEdgeContainer() {

    constructor(inContext: CircularContainer.() -> Unit):this(){
        inContext()
    }

    init {
        radius = 1f
    }

}