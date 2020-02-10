package me.wieku.framework.utils

import java.lang.reflect.Method
import java.util.concurrent.CopyOnWriteArrayList

fun <T> List<T>.binarySearchIndex(fromIndex: Int = 0, toIndex: Int = size, comparison: (Int) -> Int): Int {
    rangeCheck(size, fromIndex, toIndex)

    var low = fromIndex
    var high = toIndex - 1

    while (low <= high) {
        val mid = (low + high).ushr(1) // safe from overflows
        val cmp = comparison(mid)

        if (cmp < 0)
            low = mid + 1
        else if (cmp > 0)
            high = mid - 1
        else
            return mid // key found
    }
    return -(low + 1)  // key not found
}

private fun rangeCheck(size: Int, fromIndex: Int, toIndex: Int) {
    when {
        fromIndex > toIndex -> throw IllegalArgumentException("fromIndex ($fromIndex) is greater than toIndex ($toIndex).")
        fromIndex < 0 -> throw IndexOutOfBoundsException("fromIndex ($fromIndex) is less than zero.")
        toIndex > size -> throw IndexOutOfBoundsException("toIndex ($toIndex) is greater than size ($size).")
    }
}

private var arrayGetMethod: Method? = null

fun <T> CopyOnWriteArrayList<T>.getElements(): Array<T> {
    if (arrayGetMethod == null) {
        arrayGetMethod = CopyOnWriteArrayList::class.java.getDeclaredMethod("getArray")
        arrayGetMethod!!.isAccessible = true
    }

    return arrayGetMethod!!.invoke(this) as Array<T>
}

inline fun <T> CopyOnWriteArrayList<T>.fastForEach(action: (T) -> Unit) {
    val elements = getElements()
    for (i in elements.indices) action(elements[i])
}