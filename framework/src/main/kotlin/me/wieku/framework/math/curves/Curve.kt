package me.wieku.framework.math.curves

interface Curve<T> {
    fun pointAt(t: Float): T
    fun pointAt(t: Float, dest: T): T
    fun getLength(): Float
}