package me.wieku.danser.audio

import me.wieku.danser.beatmap.parsing.OsuEnum

enum class SampleSet(override val osuEnumId: Int): OsuEnum {
    Inherited(0),
    Normal(1),
    All(1),
    Soft(2),
    Drum(3);

    companion object: OsuEnum.Companion<SampleSet>(Normal)
}