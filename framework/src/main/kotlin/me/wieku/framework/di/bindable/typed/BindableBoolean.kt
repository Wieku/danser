package me.wieku.framework.di.bindable.typed

import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.resource.Parsable

class BindableBoolean(default: Boolean = false): Bindable<Boolean>(default), Parsable {

    override fun parseToString(): String {
        return value.toString()
    }

    override fun parseFrom(data: String) {
        value = data.toBoolean()
    }
}