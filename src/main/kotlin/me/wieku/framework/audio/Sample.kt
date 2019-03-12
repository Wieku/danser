package me.wieku.framework.audio

import jouvieje.bass.Bass.*
import jouvieje.bass.defines.BASS_ATTRIB.BASS_ATTRIB_VOL
import jouvieje.bass.defines.BASS_SAMPLE

class Sample(filename: String) {
    var mainChannel = BASS_SampleLoad(false, filename, 0, 0, 32, BASS_SAMPLE.BASS_SAMPLE_OVER_POS)

    fun play() {
        val channel = BASS_SampleGetChannel(mainChannel, false)
        BASS_ChannelSetAttribute(
            channel.asInt(),
            BASS_ATTRIB_VOL, /*settings.Audio.GeneralVolume*settings.Audio.SampleVolume*/
            0.5f
        )
        BASS_ChannelPlay(channel.asInt(), true)
    }

    fun playV(volume: Float) {
        val channel = BASS_SampleGetChannel(mainChannel, false)
        BASS_ChannelSetAttribute(channel.asInt(), BASS_ATTRIB_VOL, volume)
        BASS_ChannelPlay(channel.asInt(), true)
    }

    fun playRV(volume: Float) {
        val channel = BASS_SampleGetChannel(mainChannel, false)
        BASS_ChannelSetAttribute(
            channel.asInt(),
            BASS_ATTRIB_VOL,/*settings.Audio.GeneralVolume*settings.Audio.SampleVolume**/
            volume
        )
        BASS_ChannelPlay(channel.asInt(), true)
    }
}