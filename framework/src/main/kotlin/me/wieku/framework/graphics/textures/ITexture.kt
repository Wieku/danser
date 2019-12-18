package me.wieku.framework.graphics.textures

import me.wieku.framework.utils.Disposable

interface ITexture: Disposable {

    val id: Int
    val location: Int
    val width: Int
    val height: Int

    fun bind(location: Int)
}
