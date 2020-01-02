package me.wieku.framework.utils

import java.io.Serializable
import java.util.*

data class CPair<out A, out B>(
    val first: A,
    val second: B
) : Serializable {

    /**
     * Returns string representation of the [Pair] including its [first] and [second] values.
     */
    override fun toString(): String = "($first, $second)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Pair<*, *>) return false
        return first == other.first && second == other.second
    }

    override fun hashCode(): Int = 31 * first.hashCode() + second.hashCode()
}