package me.wieku.danser

import jouvieje.bass.Bass
import jouvieje.bass.BassInit
import jouvieje.bass.defines.BASS_ATTRIB
import jouvieje.bass.defines.BASS_FX
import jouvieje.bass.defines.BASS_STREAM
import jouvieje.bass.defines.BASS_SYNC
import me.wieku.danser.build.Build
import org.lwjgl.system.Configuration
import org.lwjgl.system.Library
import org.lwjgl.system.Platform
import org.lwjgl.system.SharedLibrary
import java.io.File
import java.net.URL
import java.nio.channels.FileChannel
import java.util.concurrent.Semaphore

fun main(args: Array<String>) {
	println("Version " + Build.Version)

	val field = BassInit::class.java.getDeclaredField("librariesLoaded")
	field.isAccessible = true
	field.set(null, true)

	val clazz = Class.forName("org.lwjgl.system.SharedLibraryLoader")
	val method = clazz.getDeclaredMethod("load", String::class.java, String::class.java, URL::class.java)
	method.isAccessible = true

	val libraries = arrayOf("bass", "bass_fx", "NativeBass"+if (Platform.get() != Platform.MACOSX && System.getProperty("os.arch").contains("64")) "64" else "")
	val use64 = if (Platform.get() == Platform.MACOSX || System.getProperty("os.arch").contains("64")) "64" else "32"

	libraries.forEach {
		val libraryName = System.mapLibraryName(it)
		method.invoke(null, it, libraryName, Library::class.java.classLoader.getResource("$use64/$libraryName")) as FileChannel
		Library.loadSystem(it)
	}

	Bass.BASS_Init(-1, 44100, 0, null, null)

	println(File("audio.mp3").absolutePath)

	val stream = Bass.BASS_StreamCreateFile(false, File("audio.mp3").absolutePath, 0, 0, BASS_STREAM.BASS_STREAM_DECODE or BASS_STREAM.BASS_STREAM_PRESCAN)
	val subStream = Bass.BASS_FX_TempoCreate(stream.asInt(), BASS_FX.BASS_FX_FREESOURCE)
	Bass.BASS_ChannelSetAttribute(subStream.asInt(), jouvieje.bass.enumerations.BASS_ATTRIB.BASS_ATTRIB_TEMPO.asInt(), 50f)
	Bass.BASS_ChannelSetAttribute(subStream.asInt(), jouvieje.bass.enumerations.BASS_ATTRIB.BASS_ATTRIB_TEMPO_PITCH.asInt(), 6f)
	Bass.BASS_ChannelSetAttribute(subStream.asInt(), BASS_ATTRIB.BASS_ATTRIB_VOL, 0.5f)
	if(Bass.BASS_ChannelPlay(subStream.asInt(), false)) {
		println("Music is playing")
	}

	var sema = Semaphore(0)

	Bass.BASS_ChannelSetSync(stream.asInt(), BASS_SYNC.BASS_SYNC_END, 0, { a, b, c, d-> sema.release()}, null)

	sema.acquire()
}