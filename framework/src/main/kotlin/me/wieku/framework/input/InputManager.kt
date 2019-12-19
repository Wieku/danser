package me.wieku.framework.input

import org.joml.Vector2i

abstract class InputManager {

    abstract fun getPosition(): Vector2i

    abstract fun update()

}