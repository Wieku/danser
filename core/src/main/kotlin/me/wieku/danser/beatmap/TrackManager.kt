package me.wieku.danser.beatmap

import me.wieku.framework.di.bindable.Bindable
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

object TrackManager: KoinComponent {

    val bindableBeatmap: Bindable<Beatmap?> by inject()

    val playlistHistory = ArrayList<BeatmapSet>()

    private var currentPlayIndex = 0

    fun start() {
        var beatmap = BeatmapManager.beatmapSets.filter {
            it.beatmaps.filter { bmap -> bmap.beatmapInfo.version == "Anto & Nuvolina's Extra" }
                .isNotEmpty()
        }[0].beatmaps.filter { bmap -> bmap.beatmapInfo.version == "Anto & Nuvolina's Extra" }[0]
        playlistHistory.add(beatmap.beatmapSet)

        startBeatmap(beatmap, true, -1300f)
    }

    fun backwards() {
        currentPlayIndex--
        if (currentPlayIndex >=0) {

            val beatmapSet = playlistHistory[currentPlayIndex]
            startBeatmap(beatmapSet.beatmaps[beatmapSet.beatmaps.size-1])

        } else {
            currentPlayIndex = 0
        }
    }

    fun forward() {
        currentPlayIndex++
        if (currentPlayIndex >= playlistHistory.size) {

            var beatmapSet: BeatmapSet
            var lastIndex: Int
            do {
                beatmapSet = BeatmapManager.beatmapSets.random()
                lastIndex = playlistHistory.lastIndexOf(beatmapSet)
            } while (lastIndex > -1 && playlistHistory.size - lastIndex < 20)

            playlistHistory.add(beatmapSet)
            startBeatmap(beatmapSet.beatmaps[beatmapSet.beatmaps.size-1])

        } else {
            val beatmapSet = playlistHistory[currentPlayIndex]
            startBeatmap(beatmapSet.beatmaps[beatmapSet.beatmaps.size-1])
        }
    }

    fun update() {
        bindableBeatmap.value?.let { beatmap ->
            if (beatmap.getTrack().getPosition() >= beatmap.getTrack().getLength()) {
                forward()
            }
        }
    }

    private fun startBeatmap(beatmap: Beatmap, startAtPreview: Boolean = false, previewOffset: Float = 0f) {
        bindableBeatmap.value?.getTrack()?.stop()
        beatmap.loadTrack()
        beatmap.getTrack().play()
        if (startAtPreview) {
            beatmap.getTrack().setPosition((beatmap.beatmapMetadata.previewTime.toFloat()+previewOffset) / 1000)
        }

        bindableBeatmap.value = beatmap
    }

}