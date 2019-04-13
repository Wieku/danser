/*
package me.wieku.danser.beatmap

class Difficulty(val hpDrain: Float, val cs: Float, val od: Float, val ar: Float) {
    var preempt = 0f
    var fadeIn = 0f
    var circleRadius = 0f
    //var mods = ArrayList<Mod>
    var hit50 = 0
    var hit100 = 0
    var hit300 = 0
    var spinnerRatio = 0f

    fun calculate() {
        var hpDrain = this.hpDrain
        var cs = this.cs
        var od = this.od
        var ar = this.ar

        */
/*if diff.Mods&HardRock > 0 {
            ar = math.Min(ar * 1.4, 10)
            cs *= 1.3
            od = math.Min(od * 1.4, 10)
            hpDrain *= 1.4
        }

        if diff.Mods&Easy > 0 {
            ar /= 2
            cs /= 2
            od /= 2
            hpDrain /= 2
        }*//*


        circleRadius = 32 * (1.0f - 0.7f * (cs - 5) / 5)
        preempt = difficultyRate(ar, 1800f, 1200f, 450f)
        fadeIn = difficultyRate(ar, 1200f, 800f, 300f)
        hit50 = (150 + 50 * (5 - od) / 5).toInt()
        hit100 = (100 + 40 * (5 - od) / 5).toInt()
        hit300 = (50 + 30 * (5 - od) / 5).toInt()
        spinnerRatio = difficultyRate(od, 3f, 5f, 7.5f)
    }

    */
/*func (diff *Difficulty) SetMods(mods Modifier) {
        diff.Mods = mods
        diff.calculate()
    }*//*


    fun getModifiedTime(time: Float): Float {
        */
/*if diff.Mods&DoubleTime > 0 {
            return float64(time) / 1.5
        } else if diff.Mods&HalfTime > 0 {
            return float64(time) / 0.75
        } else {
            return float64(time)
        }*//*

        return time
    }

    companion object {
        fun difficultyRate(diff: Float, min: Float, mid: Float, max: Float): Float {
            if (diff > 5) {
                return mid + (max - mid) * (diff - 5) / 5
            }
            if (diff < 5) {
                return mid - (mid - min) * (5 - diff) / 5
            }
            return mid
        }
    }
}*/
