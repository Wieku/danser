package me.wieku.danser.beatmap.parsing

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

enum class Section(override val osuEnumId: Int, val separator: String): OsuEnum {
    Unknown(0, ":"),
    General(1 shl 0, ":"),
    Colours(1 shl 1, ","),
    Editor(1 shl 2, ":"),
    Metadata(1 shl 3, ":"),
    TimingPoints(1 shl 4, ","),
    Events(1 shl 5, ","),
    HitObjects(1 shl 6, ","),
    Difficulty(1 shl 7, ":"),
    Variables(1 shl 8, ":");

    companion object: OsuEnum.Companion<Section>(Unknown)
}