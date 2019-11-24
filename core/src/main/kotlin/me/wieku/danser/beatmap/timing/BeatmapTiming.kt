package me.wieku.danser.beatmap.timing

import me.wieku.danser.utils.binarySearchApproximate

class BeatmapTiming {

    private var baseBpm: Float = 0f
    private var points = ArrayList<TimingPoint>()

    var baseSampleData: SampleData = SampleData(SampleSet.Normal, SampleSet.Normal, 1, 1f)
    private var sampleData: SampleData? = null

    fun addTimingPoint(time: Long, bpm: Float, sampleData: SampleData?, kiai: Boolean) {
        if (bpm > 0) {
            baseBpm = bpm
        }

        check(sampleData != null || this.sampleData != null) {
            "First timing point must have a sample data"
        }

        this.sampleData = sampleData?:this.sampleData

        val point = TimingPoint(time, baseBpm, bpm, this.sampleData!!, kiai)
        points.add(point)
    }


    fun getPointAt(time: Long): TimingPoint {
        val index = points.binarySearchApproximate(comparison = {point:TimingPoint ->  point.time.compareTo(time)})
        check(index != -1) {
            "No timing points found"
        }
        return points[index]
    }

}