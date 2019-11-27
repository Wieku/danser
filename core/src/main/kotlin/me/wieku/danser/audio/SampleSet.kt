package me.wieku.danser.audio

import me.wieku.framework.utils.EnumWithId

enum class SampleSet(override val enumId: Int): EnumWithId {
    Inherited(0),
    Normal(1),
    All(1),
    Soft(2),
    Drum(3);

    companion object: EnumWithId.Companion<SampleSet>(Normal)
}