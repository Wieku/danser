package me.wieku.danser.utils

interface OsuEnum {
    val osuEnumId: Int

    open class Companion<T>(private val defaultReturn: T) where T:Enum<T>, T:OsuEnum {
        private val inverseLookupS = HashMap<String, T>()

        init {
            defaultReturn::class.java.enumConstants.forEach {
                inverseLookupS[it.name] = it
                inverseLookupS[it.osuEnumId.toString()] = it
            }
        }

        operator fun get(value: String) = inverseLookupS[value]?:defaultReturn
        operator fun get(osuId: Int) = get(osuId.toString())
    }
}