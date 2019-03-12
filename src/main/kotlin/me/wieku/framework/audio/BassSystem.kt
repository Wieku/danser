package me.wieku.framework.audio

import jouvieje.bass.Bass
import jouvieje.bass.BassInit
import org.lwjgl.system.Library
import org.lwjgl.system.Platform
import java.net.URL
import java.nio.channels.FileChannel

object BassSystem {
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
            Library.loadSystem(it)
        }
    }

    fun initSystem() {
        Bass.BASS_Init(-1, 44100, 0, null, null)
    }

}