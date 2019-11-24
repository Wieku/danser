package me.wieku.danser.beatmap.timing

import me.wieku.danser.utils.binarySearchApproximate

class BeatmapTiming {

    private var baseBpm: Float = 0f
    private var points = ArrayList<TimingPoint>();

    fun addTimingPoint(time: Long, bpm: Float, sampleData: SampleData, kiai: Boolean) {
        if (bpm > 0) {
            baseBpm = bpm
        }
        val point = TimingPoint(time, baseBpm, bpm, sampleData, kiai)
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