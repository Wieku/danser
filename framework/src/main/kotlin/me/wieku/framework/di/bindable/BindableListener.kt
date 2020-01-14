package me.wieku.framework.di.bindable

typealias BindableListener<T> = (oldValue: T, newValue: T, bindable: Bindable<T>) -> Unit