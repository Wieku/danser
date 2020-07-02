package me.wieku.danser.beatmap

import me.wieku.danser.beatmap.parsing.BeatmapParser
import me.wieku.framework.animation.Glider
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.logging.Logging
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*
import kotlin.math.min

object TrackManager : KoinComponent {

    private val logger = Logging.getLogger("runtime")

    private val bindableBeatmap: Bindable<Beatmap?> by inject()

    private val playlistHistory = ArrayList<BeatmapSet>()

    private val volumeGlider = Glider(0f)

    private var currentPlayIndex = -1

    fun start(time: Double) {
        var beatmap = Beatmap("cYsmix - Peer Gynt (Wieku) [Danser Intro].osu")
        BeatmapParser().parse(
            FileHandle(
                "assets/beatmaps/cYsmix - Peer Gynt/cYsmix - Peer Gynt (Wieku) [Danser Intro].osu",
                FileType.Classpath
            ), beatmap
        )

        startBeatmap(beatmap, true, -1300f)
        volumeGlider.addEvent(time, time + 1300f, 0f, 1f)
    }

    fun backwards() {
        if (currentPlayIndex == -1) return
        currentPlayIndex--
        if (currentPlayIndex >= 0) {

            val beatmapSet = playlistHistory[currentPlayIndex]
            startBeatmap(beatmapSet.beatmaps[beatmapSet.beatmaps.size - 1])

        } else {
            currentPlayIndex = 0
        }
    }

    fun forward() {
        if (BeatmapManager.beatmapSets.size == 0) return

        currentPlayIndex++
        if (currentPlayIndex >= playlistHistory.size) {

            var beatmapSet: BeatmapSet
            var lastIndex: Int
            do {
                beatmapSet = BeatmapManager.beatmapSets.random()
                lastIndex = playlistHistory.lastIndexOf(beatmapSet)
            } while (lastIndex > -1 && playlistHistory.size - lastIndex < min(BeatmapManager.beatmapSets.size, 20))

            playlistHistory.add(beatmapSet)
            startBeatmap(beatmapSet.beatmaps[beatmapSet.beatmaps.size - 1])

        } else {
            val beatmapSet = playlistHistory[currentPlayIndex]
            startBeatmap(beatmapSet.beatmaps[beatmapSet.beatmaps.size - 1])
        }
    }

    fun update(time: Double) {

        volumeGlider.update(time)

        bindableBeatmap.value?.let { beatmap ->
            beatmap.getTrack().setVolume(volumeGlider.value)
            if (beatmap.getTrack().getPosition() >= beatmap.getTrack().getLength()) {
                forward()
            }
        }
    }

    private fun startBeatmap(beatmap: Beatmap, startAtPreview: Boolean = false, previewOffset: Float = 0f) {
        bindableBeatmap.value?.getTrack()?.stop()
        logger.info("Changed current beatmap to: ${beatmap.beatmapMetadata.artist} - ${beatmap.beatmapMetadata.title}")
        beatmap.loadTrack(startAtPreview)
        beatmap.getTrack().play(volumeGlider.value)
        if (startAtPreview) {
            beatmap.getTrack().setPosition((beatmap.beatmapMetadata.previewTime.toDouble() + previewOffset) / 1000)
        } else {
            beatmap.getTrack().setPosition((previewOffset / 1000).toDouble())
        }

        bindableBeatmap.value = beatmap
    }

}