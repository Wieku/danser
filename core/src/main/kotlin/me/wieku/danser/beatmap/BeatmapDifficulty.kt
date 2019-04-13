package me.wieku.danser.beatmap

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

class BeatmapDifficulty(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<BeatmapDifficulty>(BeatmapDifficulties)

    var hpDrain by BeatmapDifficulties.hpDrain
    var cs by BeatmapDifficulties.cs
    var od by BeatmapDifficulties.od
    var ar by BeatmapDifficulties.ar
    var sliderMultiplier by BeatmapDifficulties.sliderMultiplier
    var sliderTickRate by BeatmapDifficulties.sliderTickRate

}

object BeatmapDifficulties: IntIdTable() {
    val hpDrain = float("hpDrain").default(5f)
    val cs = float("cs").default(5f)
    val od = float("od").default(5f)
    val ar = float("ar").default(5f)
    val sliderMultiplier = float("sliderMultiplier").default(1f)
    val sliderTickRate = integer("sliderTickRate").default(0)
}