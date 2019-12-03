package me.wieku.framework.di.bindable.typed

import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.resource.Parsable

class BindableEnum<T>(default: T? = null): Bindable<T>(default), Parsable where T: Enum<T> {

    override fun parseToString(): String {
        return value.toString()
    }

    override fun parseFrom(data: String) {
        value = java.lang.Enum.valueOf(value?.javaClass , data)
    }
}