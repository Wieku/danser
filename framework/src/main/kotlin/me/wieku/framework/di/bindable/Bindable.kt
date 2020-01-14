package me.wieku.framework.di.bindable

import java.lang.ref.WeakReference

open class Bindable<T> (startValue: T) {

    /**
     * Backing value for this bindable
     */
    private var _value: T = startValue

    var value: T
        get() = _value
        set(value) {
            if (value == _value) return

            setValue(value, this)
        }

    private val listeners = ArrayList<BindableListener<T>>()

    private val bindables = ArrayList<WeakReference<Bindable<T>>>()

    private fun setValue(value: T, source: Bindable<T>) {
        bindables.removeIf { it.get() == null }

        bindables.forEach {
            val bindable = it.get()!!
            if (source === bindable) return@forEach

            bindable.setValue(value, this)
        }

        notifyListeners(_value, value)
        _value = value
    }

    private fun notifyListeners(previousValue: T, newValue: T) {
        listeners.forEach { it(previousValue, newValue, this) }
    }

    fun bindTo(bindable: Bindable<T>) {
        value = bindable._value

        bindables += WeakReference(bindable)
        bindable.bindables += WeakReference(this)
    }

    fun unbindFrom(bindable: Bindable<T>) {
        bindables.removeIf { it.get() == null || it.get()!! === bindable }
        bindable.bindables.removeIf { it.get() == null || it.get()!! === this }
    }

    fun addListener(notifyNow: Boolean = false, listener: BindableListener<T>) {
        listeners.add(listener)

        if (notifyNow) {
            listener(_value, _value, this)
        }
    }

    fun addListener(listener: BindableListener<T>) = addListener(false, listener)

}