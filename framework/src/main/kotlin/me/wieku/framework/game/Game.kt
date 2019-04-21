package me.wieku.framework.game

import me.wieku.framework.utils.Disposable

interface Game: Disposable {
    fun setup()
    fun update(delta: Float)
    fun draw(delta: Float)
    fun focus()
    fun unfocus()
}