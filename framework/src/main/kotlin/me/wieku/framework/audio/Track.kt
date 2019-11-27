package me.wieku.framework.audio

import jouvieje.bass.Bass.*
import jouvieje.bass.defines.BASS_ATTRIB
import jouvieje.bass.defines.BASS_FX
import jouvieje.bass.defines.BASS_POS.BASS_POS_BYTE
import jouvieje.bass.defines.BASS_STREAM
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import org.lwjgl.BufferUtils
import org.lwjgl.system.MemoryStack
import java.lang.ref.WeakReference

class Track(file: FileHandle, val fftMode: FFTMode = FFTMode.FFT512) {
    private var channelStream = when (file.fileType) {
        FileType.Classpath -> {
            val buffer = file.toBuffer()
            BASS_StreamCreateFile(
                true,
                buffer,
                0,
                buffer.limit().toLong(),
                BASS_STREAM.BASS_STREAM_DECODE or BASS_STREAM.BASS_STREAM_PRESCAN
            )
        }
        FileType.Local, FileType.Absolute -> {
            BASS_StreamCreateFile(
                false,
                file.absolutePath(),
                0,
                0,
                BASS_STREAM.BASS_STREAM_DECODE or BASS_STREAM.BASS_STREAM_PRESCAN
            )
        }
    }

    private var fxChannel = BASS_FX_TempoCreate(channelStream.asInt(), BASS_FX.BASS_FX_FREESOURCE)

    private var dataBuffer = BufferUtils.createByteBuffer(fftMode.bins * 4)

    internal var volume = 1f
    internal var isVolumeAbsolute = false

    var fftData = FloatArray(fftMode.bins)
        private set

    var peak: Float = 0.0f
        private set

    var beat: Float = 0.0f
        private set

    var leftChannelLevel: Float = 0.0f
        private set

    var rightChannelLevel: Float = 0.0f
        private set

    init {
        synchronized(BassSystem.tracks) {
            BassSystem.tracks.add(WeakReference(this))
        }
    }

    fun play(volume: Float = 1f, isAbsolute: Boolean = false) {
        setVolume(volume, isAbsolute)
        BASS_ChannelPlay(fxChannel.asInt(), true)
    }

    fun pause() {
        BASS_ChannelPause(fxChannel.asInt())
    }

    fun resume() {
        BASS_ChannelPlay(fxChannel.asInt(), false)
    }

    fun stop() {
        BASS_ChannelStop(fxChannel.asInt())
        BASS_ChannelStop(channelStream.asInt())
    }

    fun setVolume(vol: Float, isAbsolute: Boolean = false) {
        volume = vol
        isVolumeAbsolute = isAbsolute
        BASS_ChannelSetAttribute(
            fxChannel.asInt(),
            BASS_ATTRIB.BASS_ATTRIB_VOL,
            if (isAbsolute) vol else BassSystem.globalVolume * BassSystem.musicVolume * vol
        )
    }

    fun getVolume(): Float {
        MemoryStack.stackPush().use { stack ->
            var buf = stack.mallocFloat(1)
            BASS_ChannelGetAttribute(fxChannel.asInt(), BASS_ATTRIB.BASS_ATTRIB_VOL, buf)
            return buf.get() / (if (isVolumeAbsolute) 1f else (BassSystem.globalVolume * BassSystem.musicVolume))
        }
    }

    fun getLength(): Float {
        return BASS_ChannelBytes2Seconds(
            fxChannel.asInt(),
            BASS_ChannelGetLength(fxChannel.asInt(), BASS_POS_BYTE)
        ).toFloat()
    }

    fun setPosition(pos: Float) {
        BASS_ChannelSetPosition(
            fxChannel.asInt(),
            BASS_ChannelSeconds2Bytes(fxChannel.asInt(), pos.toDouble()),
            BASS_POS_BYTE
        )
    }

    fun getPosition(): Float {
        return BASS_ChannelBytes2Seconds(
            fxChannel.asInt(),
            BASS_ChannelGetPosition(fxChannel.asInt(), BASS_POS_BYTE)
        ).toFloat()
    }

    fun setTempo(tempo: Float) {
        BASS_ChannelSetAttribute(
            fxChannel.asInt(),
            jouvieje.bass.enumerations.BASS_ATTRIB.BASS_ATTRIB_TEMPO.asInt(),
            (tempo - 1.0f) * 100
        )
    }

    fun setPitch(tempo: Float) {
        BASS_ChannelSetAttribute(
            fxChannel.asInt(),
            jouvieje.bass.enumerations.BASS_ATTRIB.BASS_ATTRIB_TEMPO_PITCH.asInt(),
            (tempo - 1.0f) * 12
        )
    }

    fun getState(): ChannelStatus {
        return ChannelStatus[BASS_ChannelIsActive(fxChannel.asInt())]
    }

    fun update() {
        BASS_ChannelGetData(fxChannel.asInt(), dataBuffer, fftMode.bassInt)

        dataBuffer.asFloatBuffer().get(fftData)

        var allPeak = 0f
        var beatPeak = 0f

        for ((i, v) in fftData.withIndex()) {
            allPeak = Math.max(allPeak, v)

            if (i in 1..4) {
                beatPeak = Math.max(beatPeak, v)
            }
        }

        beat = beatPeak
        peak = allPeak

        val level = BASS_ChannelGetLevel(fxChannel.asInt())

        leftChannelLevel = (level and 0xffff).toFloat() / 0x8000
        rightChannelLevel = (level ushr 16).toFloat() / 0x8000
    }

    fun getLevelCombined(): Float {
        return (leftChannelLevel + rightChannelLevel) / 2
    }

    protected fun finalize() {
        stop()
    }

}