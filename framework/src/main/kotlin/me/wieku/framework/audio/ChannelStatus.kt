package me.wieku.framework.audio

import me.wieku.framework.utils.EnumWithId

enum class ChannelStatus(override val enumId: Int): EnumWithId {
    Stopped(0),
    Playing(1),
    Stalled(2),
    Paused(3);

    companion object: EnumWithId.Companion<ChannelStatus>(Stopped)
}