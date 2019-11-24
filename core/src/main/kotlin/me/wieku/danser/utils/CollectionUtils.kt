package me.wieku.danser.utils

public fun <T> List<T>.binarySearchApproximate(fromIndex: Int = 0, toIndex: Int = size, comparison: (T) -> Int): Int {
    rangeCheck(size, fromIndex, toIndex)

    var low = fromIndex
    var high = toIndex - 1

    while (low <= high) {
        val mid = (low + high).ushr(1) // safe from overflows
        val midVal = get(mid)
        val cmp = comparison(midVal)

        when {
            cmp < 0 -> {
                if (mid + 1 >= size || comparison(get(mid + 1)) > 1) {
                    return mid
                }
                low = mid + 1
            }
            cmp > 0 -> {
                if (mid - 1 < 0 || comparison(get(mid - 1)) <= 0) {
                    return mid
                }
                high = mid - 1
            }
            else -> return mid
        } // key found
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