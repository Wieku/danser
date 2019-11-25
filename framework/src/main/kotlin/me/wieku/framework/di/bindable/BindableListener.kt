package me.wieku.framework.di.bindable

interface BindableListener<T> {
    fun valueChanged(bindable: Bindable<T>)
}