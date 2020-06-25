package me.wieku.framework.audio

import com.sun.jna.NativeLibrary
import jouvieje.bass.Bass
import jouvieje.bass.BassInit
import jouvieje.bass.structures.BASS_DEVICEINFO
import me.wieku.framework.configuration.FrameworkConfig
import me.wieku.framework.di.bindable.typed.BindableFloat
import me.wieku.framework.di.bindable.typed.BindableString
import me.wieku.framework.logging.Logging
import org.lwjgl.system.Library
import org.lwjgl.system.Platform
import java.io.File
import java.lang.ref.WeakReference
import java.net.URL
import java.nio.channels.FileChannel

object BassSystem {

    private val logger = Logging.getLogger("runtime")

    internal var tracks: ArrayList<WeakReference<Track>> = ArrayList()

    val audioDevice = BindableString("")
    val generalVolume = BindableFloat(1f)
    val musicVolume = BindableFloat(1f)
    val effectsVolume = BindableFloat(1f)

    init {
        val field = BassInit::class.java.getDeclaredField("librariesLoaded")
        field.isAccessible = true
        field.set(null, true)

        val clazz = Class.forName("org.lwjgl.system.SharedLibraryLoader")
        val loadMethod = clazz.getDeclaredMethod("load", String::class.java, String::class.java, URL::class.java)
        loadMethod.isAccessible = true

        val libraries = arrayOf(
            "bass",
            "bass_fx",
            "NativeBass" + if (Platform.get() != Platform.MACOSX && System.getProperty("os.arch").contains("64")) "64" else ""
        )
        val use64 =
            if (Platform.get() == Platform.MACOSX || System.getProperty("os.arch").contains("64")) "64" else "32"

        libraries.forEach {
            val libraryName = System.mapLibraryName(it)
            loadMethod.invoke(
                null,
                it,
                libraryName,
                Library::class.java.classLoader.getResource("$use64/$libraryName")
            ) as FileChannel

            NativeLibrary.getInstance(System.getProperty("org.lwjgl.librarypath") + File.separator + libraryName)
            Library.loadSystem("bass", it)
        }
    }

    fun initSystem() {

        audioDevice.bindTo(FrameworkConfig.audioDevice)
        generalVolume.bindTo(FrameworkConfig.generalVolume)
        musicVolume.bindTo(FrameworkConfig.musicVolume)
        effectsVolume.bindTo(FrameworkConfig.effectsVolume)

        logger.info("--Available sound devices--")

        getSoundDevices().forEach(logger::info)

        logger.info("--End of the list--")

        val deviceId = getDeviceId(audioDevice.value)

        audioDevice.value = getDeviceName(deviceId)

        val initialized = Bass.BASS_Init(deviceId, 44100, 0, null, null)

        check(initialized) {
            logger.error("BASS failed to initialize")
            "BASS failed to initialize"
        }

        logger.info("BASS Initialized")
        logger.info("BASS Version: ${parseVersion(Bass.BASS_GetVersion())}")
        logger.info("BASS FX Version: ${parseVersion(Bass.BASS_FX_GetVersion())}")
        logger.info("Audio Device: ${audioDevice.value}")

        audioDevice.addListener { _, newValue, _ ->
            Bass.BASS_SetDevice(getDeviceId(newValue))
        }

        generalVolume.addListener { _, _, _ -> updateAll() }
        musicVolume.addListener { _, _, _ -> updateAll() }
        effectsVolume.addListener { _, _, _ -> updateAll() }
    }

    fun getSoundDevices(): List<String> {
        val devices = ArrayList<String>()

        val info = BASS_DEVICEINFO.allocate()
        var i = 0
        while (Bass.BASS_GetDeviceInfo(++i, info)) {
            devices += info.name
        }
        info.release()

        return devices
    }

    fun getDeviceId(name: String): Int {
        val info = BASS_DEVICEINFO.allocate()
        var i = 0
        while (Bass.BASS_GetDeviceInfo(++i, info)) {
            if (name  == info.name) {
                info.release()
                return i
            }
        }
        info.release()

        return 1
    }

    fun getDeviceName(id: Int): String {
        val info = BASS_DEVICEINFO.allocate()

        Bass.BASS_GetDeviceInfo(id, info)

        val name = info.name

        info.release()

        return name
    }

    private fun updateAll() {
        synchronized(tracks) {
            tracks = tracks.filter { it.get() != null } as ArrayList<WeakReference<Track>>
            tracks.forEach { ref ->
                val track = ref.get()!!
                track.setVolume(track.volume, track.isVolumeAbsolute)
            }
        }
    }

    private fun parseVersion(version: Int): String {
        val main = version shr 24 and 0xFF
        val revision0 = version shr 16 and 0xFF
        val revision1 = version shr 8 and 0xFF
        val revision2 = version and 0xFF
        return "%d.%d.%d.%d".format(main, revision0, revision1, revision2)
    }

}