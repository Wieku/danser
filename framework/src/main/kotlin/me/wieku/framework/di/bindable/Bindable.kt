package me.wieku.framework.di.bindable

import java.lang.ref.Reference
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

            _value = value

            setValue(value, this)
        }

    private val listeners = ArrayList<BindableListener<T>>()

    private val bounded = ArrayList<WeakReference<Bindable<T>>>()

    private fun setValue(value: T?, source: Bindable<T>) {
        bounded.removeIf { it.get() == null }

        bounded.forEach {
            val bindable = it.get()!!
            if (source == bindable) return@forEach

            bindable.setValue(value, this)
        }

        notifyListeners()
    }

    private fun notifyListeners() {
        listeners.forEach { it.valueChanged(this) }
    }

    fun bindTo(bindable: Bindable<T>) {
        value = bindable._value

        bounded += WeakReference(bindable)
        bindable.bounded += WeakReference(this)
    }

    fun unbindFrom(bindable: Bindable<T>) {
        bounded.removeIf { it.get() == null || it.get()!! == bindable }
        bindable.bounded.removeIf { it.get() == null || it.get()!! == this }
    }

    fun addListener(listener: BindableListener<T>, notifyNow: Boolean = false) {
        listeners.add(listener)

        if (notifyNow) {
            listener.valueChanged(this)
        }
    }

    fun removeListener(listener: BindableListener<T>) {
        listeners.removeIf { reference -> reference == listener }
    }

}