package me.wieku.danser.beatmap.timing

import me.wieku.danser.audio.SampleData
import java.lang.Float.max

class TimingPoint(
    val time: Long,
    val baseBpm: Float,
    private val bpm: Float,
    val sampleData: SampleData,
    val kiai: Boolean
) {

    var realBpm: Float = if (bpm > 0) {
        bpm
    } else {
        baseBpm / max(0.1f, -100.0f / bpm)
    }
        private set
}