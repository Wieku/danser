package me.wieku.framework.di.bindable.typed

import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.resource.Parsable
import org.joml.Vector2i

class BindableVector2i(default: Vector2i? = null): Bindable<Vector2i>(default), Parsable {

    override fun parseToString(): String {
        val x = value?.x?:0
        val y = value?.y?:0
        return "${x}x$y"
    }

    override fun parseFrom(data: String) {
        val splitted = data.split("x")
        val vector = Vector2i(splitted[0].toInt(), splitted[1].toInt())
        value = vector
    }
}