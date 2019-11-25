package me.wieku.framework.di.bindable

import java.lang.ref.WeakReference

class Bindable<T> (startValue: T? = null) {

    var value: T? = startValue
        set(value) {
            listeners.removeIf { it.get() == null }
            listeners.forEach { it.get()?.valueChanged(this) }
            field = value
        }

    private val listeners = ArrayList<WeakReference<BindableListener<T>>>()

    fun addListener(listener: BindableListener<T>, notifyNow: Boolean = false) {
        listeners.add(WeakReference(listener))
        if (notifyNow) {
            listener.valueChanged(this)
        }
    }

    fun removeListener(listener: BindableListener<T>) {
        listeners.removeIf { reference -> reference.get() == null || reference.get()!! == listener }
    }

}