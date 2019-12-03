package me.wieku.framework.di.bindable.typed

import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.resource.Parsable

class BindableFloat(default: Float = 0f): Bindable<Float>(default), Parsable {

    override fun parseToString(): String {
        return value.toString()
    }

    override fun parseFrom(data: String) {
        value = data.toFloat()
    }
}