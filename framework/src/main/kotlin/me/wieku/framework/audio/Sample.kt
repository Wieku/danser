package me.wieku.framework.audio

import jouvieje.bass.Bass.*
import jouvieje.bass.defines.BASS_ATTRIB.BASS_ATTRIB_VOL
import jouvieje.bass.defines.BASS_SAMPLE
import jouvieje.bass.utils.BufferUtils
import jouvieje.bass.utils.Pointer
import me.wieku.framework.configuration.FrameworkConfig
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import kotlin.math.min

class Sample(file: FileHandle) {
    private var mainChannel = when (file.fileType) {
        FileType.Classpath -> {
            val buffer = file.toBuffer()
            BASS_SampleLoad(
                true,
                BufferUtils.asPointer(buffer),
                0,
                buffer.limit(),
                16,
                BASS_SAMPLE.BASS_SAMPLE_OVER_POS
            )
        }
        FileType.Local, FileType.Absolute -> {
            BASS_SampleLoad(
                false,
                file.absolutePath(),
                0,
                0,
                16,
                BASS_SAMPLE.BASS_SAMPLE_OVER_POS
            )
        }
    }

    fun play(volume: Float = 1f, isAbsolute: Boolean = false) {
        val channel = BASS_SampleGetChannel(mainChannel, false)
        BASS_ChannelSetAttribute(
            channel.asInt(),
            BASS_ATTRIB_VOL,
            min(1f, if (isAbsolute) volume else FrameworkConfig.generalVolume.value * FrameworkConfig.effectsVolume.value * volume)
        )
        BASS_ChannelPlay(channel.asInt(), true)
    }

}