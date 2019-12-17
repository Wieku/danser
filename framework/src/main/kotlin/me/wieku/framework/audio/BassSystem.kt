package me.wieku.framework.audio

import jouvieje.bass.Bass
import jouvieje.bass.BassInit
import jouvieje.bass.structures.BASS_DEVICEINFO
import me.wieku.framework.configuration.FrameworkConfig
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.di.bindable.BindableListener
import org.lwjgl.system.Library
import org.lwjgl.system.Platform
import java.lang.ref.WeakReference
import java.net.URL
import java.nio.channels.FileChannel

object BassSystem {

    internal var tracks: ArrayList<WeakReference<Track>> = ArrayList()

    init {
        val field = BassInit::class.java.getDeclaredField("librariesLoaded")
        field.isAccessible = true
        field.set(null, true)

        val clazz = Class.forName("org.lwjgl.system.SharedLibraryLoader")
        val method = clazz.getDeclaredMethod("load", String::class.java, String::class.java, URL::class.java)
        method.isAccessible = true

        val libraries = arrayOf(
            "bass",
            "bass_fx",
            "NativeBass" + if (Platform.get() != Platform.MACOSX && System.getProperty("os.arch").contains("64")) "64" else ""
        )
        val use64 =
            if (Platform.get() == Platform.MACOSX || System.getProperty("os.arch").contains("64")) "64" else "32"

        libraries.forEach {
            val libraryName = System.mapLibraryName(it)
            method.invoke(
                null,
                it,
                libraryName,
                Library::class.java.classLoader.getResource("$use64/$libraryName")
            ) as FileChannel

            //Library.loadSystem(it)
            Library.loadSystem("bass", it)
        }
    }

    fun initSystem() {
        println("--Available sound devices--")

        getSoundDevices().forEach{println(it)}

        println("--End of the list--")

        val deviceId = getDeviceId(FrameworkConfig.audioDevice.value)

        println("Device id: $deviceId")

        FrameworkConfig.audioDevice.value = getDeviceName(deviceId)

        Bass.BASS_Init(deviceId, 44100, 0, null, null)

        FrameworkConfig.audioDevice.addListener(object: BindableListener<String> {
            override fun valueChanged(bindable: Bindable<String>) {
                Bass.BASS_SetDevice(getDeviceId(bindable.value))
            }
        })

        val volumeCallback = object: BindableListener<Float> {
            override fun valueChanged(bindable: Bindable<Float>) {
                updateAll()
            }
        }

        FrameworkConfig.generalVolume.addListener(volumeCallback)
        FrameworkConfig.musicVolume.addListener(volumeCallback)
        FrameworkConfig.effectsVolume.addListener(volumeCallback)
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

}