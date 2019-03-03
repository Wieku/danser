package me.wieku.danser

import me.wieku.danser.build.Build
import me.wieku.framework.audio.BassSystem
import me.wieku.framework.audio.Track

import java.io.File

fun main(args: Array<String>) {
	println("Version " + Build.Version)

	BassSystem.initSystem()

	val track = Track(File("audio.mp3").absolutePath)
	track.play()

	//Bass.BASS_ChannelSetSync(stream.asInt(), BASS_SYNC.BASS_SYNC_END, 0, { a, b, c, d-> sema.release()}, null)

	Thread {
		while (true) {
			track.update()
			println(track.beat)
			Thread.sleep(100)
		}
	}.run()
}