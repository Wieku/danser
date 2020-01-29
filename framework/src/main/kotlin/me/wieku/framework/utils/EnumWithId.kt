package me.wieku.framework.utils

interface EnumWithId {
    val enumId: Int

    open class Companion<T>(private val defaultReturn: T) where T : Enum<T>, T : EnumWithId {
        private val inverseLookup = HashMap<String, T>()

        init {
            defaultReturn::class.java.enumConstants.forEach {
                inverseLookup[it.name] = it
                inverseLookup[it.enumId.toString()] = it
            }
        }

        operator fun get(value: String) = inverseLookup[value] ?: defaultReturn
        operator fun get(enumId: Int) = get(enumId.toString())
    }
}