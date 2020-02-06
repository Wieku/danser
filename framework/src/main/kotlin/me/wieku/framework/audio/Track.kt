package me.wieku.framework.audio

import jouvieje.bass.Bass.*
import jouvieje.bass.defines.BASS_ATTRIB
import jouvieje.bass.defines.BASS_FX
import jouvieje.bass.defines.BASS_POS.BASS_POS_BYTE
import jouvieje.bass.defines.BASS_STREAM
import me.wieku.framework.configuration.FrameworkConfig
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import me.wieku.framework.time.IClock
import org.lwjgl.BufferUtils
import org.lwjgl.system.MemoryStack
import java.io.File
import java.lang.ref.WeakReference
import kotlin.math.min

class Track(file: FileHandle, val fftMode: FFTMode = FFTMode.FFT512) : IClock {
    private var channelStream = when (file.fileType) {
        FileType.Classpath -> {
            val tmpFile = File.createTempFile("danser", "." + file.file.extension)
            tmpFile.outputStream().use { file.inputStream().copyTo(it) }
            BASS_StreamCreateFile(
                false,
                tmpFile.absolutePath,
                0,
                0,
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

    var leftChannelLevel: Float = 0.0f
        private set

    var rightChannelLevel: Float = 0.0f
        private set

    override var currentTime: Float
        get() = getPosition() * 1000
        set(value) {
            setPosition(value / 1000)
        }

    override var clockRate: Float
        get() = getTempo()
        set(value) {
            setTempo(value)
        }

    override var isRunning: Boolean
        get() = getState() == ChannelStatus.Playing
        set(_) {}

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

    fun setVolume(volume: Float, isAbsolute: Boolean = false) {
        this.volume = volume
        isVolumeAbsolute = isAbsolute
        BASS_ChannelSetAttribute(
            fxChannel.asInt(),
            BASS_ATTRIB.BASS_ATTRIB_VOL,
            min(1f, if (isAbsolute) this.volume else FrameworkConfig.generalVolume.value * FrameworkConfig.musicVolume.value * this.volume)
        )
    }

    fun getVolume(): Float {
        MemoryStack.stackPush().use { stack ->
            var buf = stack.mallocFloat(1)
            BASS_ChannelGetAttribute(fxChannel.asInt(), BASS_ATTRIB.BASS_ATTRIB_VOL, buf)
            return buf.get() / if (isVolumeAbsolute) 1f else FrameworkConfig.generalVolume.value * FrameworkConfig.musicVolume.value
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

    fun getTempo(): Float {
        return MemoryStack.stackPush().use { stack ->
            val buffer = stack.mallocFloat(1)
            BASS_ChannelGetAttribute(
                fxChannel.asInt(),
                jouvieje.bass.enumerations.BASS_ATTRIB.BASS_ATTRIB_TEMPO.asInt(),
                buffer
            )
            buffer.get(0) / 100 + 1f
        }
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