package me.wieku.framework.utils

interface EnumWithId {
    val enumId: Int

    open class Companion<T>(private val defaultReturn: T) where T:Enum<T>, T: EnumWithId {
        private val inverseLookupS = HashMap<String, T>()

        init {
            defaultReturn::class.java.enumConstants.forEach {
                inverseLookupS[it.name] = it
                inverseLookupS[it.enumId.toString()] = it
            }
        }

        operator fun get(value: String) = inverseLookupS[value]?:defaultReturn
        operator fun get(osuId: Int) = get(osuId.toString())
    }
}