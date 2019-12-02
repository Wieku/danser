package me.wieku.framework.utils

inline fun <T> T.synchronized(block: T.() -> Unit) where T : Any {
    synchronized(this) {
        block()
    }
}