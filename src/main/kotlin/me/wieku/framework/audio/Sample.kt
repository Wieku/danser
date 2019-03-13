package me.wieku.framework.audio

import jouvieje.bass.Bass.*
import jouvieje.bass.defines.BASS_ATTRIB.BASS_ATTRIB_VOL
import jouvieje.bass.defines.BASS_SAMPLE

class Sample(filename: String) {
    var mainChannel = BASS_SampleLoad(false, filename, 0, 0, 16, BASS_SAMPLE.BASS_SAMPLE_OVER_POS)

    fun play(volume: Float = 1f, isAbsolute: Boolean = false) {
        val channel = BASS_SampleGetChannel(mainChannel, false)
        BASS_ChannelSetAttribute(
            channel.asInt(),
            BASS_ATTRIB_VOL,
            if (isAbsolute) volume else BassSystem.globalVolume * BassSystem.sampleVolume * volume
        )
        BASS_ChannelPlay(channel.asInt(), true)
    }

}