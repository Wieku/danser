package me.wieku.danser.beatmap.timing

import me.wieku.danser.utils.binarySearchApproximate

class BeatmapTiming {

    private var baseBpm: Float = 0f
    private var points = ArrayList<TimingPoint>()

    var baseSampleData: SampleData = SampleData(SampleSet.Normal, SampleSet.Inherited, 1, 1f)
        set(value) {
            sampleData = value
            field = value
        }

    private var sampleData: SampleData = baseSampleData

    fun addTimingPoint(time: Long, bpm: Float, sampleData: SampleData?, kiai: Boolean) {
        if (bpm > 0) {
            baseBpm = bpm
        }

        if (sampleData != null) {
            this.sampleData = SampleData(
                if (sampleData.sampleSet == SampleSet.Inherited) baseSampleData.sampleSet else sampleData.sampleSet,
                sampleData.sampleAddition,
                sampleData.sampleIndex,
                sampleData.sampleVolume
            )
        }

        val point = TimingPoint(time, baseBpm, bpm, this.sampleData, kiai)
        points.add(point)
    }

    fun getPointAt(time: Long): TimingPoint {
        val index = points.binarySearchApproximate(comparison = { point: TimingPoint -> point.time.compareTo(time) })
        check(index != -1) {
            "No timing points found"
        }
        return points[index]
    }

}