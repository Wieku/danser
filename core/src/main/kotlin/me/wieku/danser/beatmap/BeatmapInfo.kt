package me.wieku.danser.beatmap

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

class BeatmapInfo(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BeatmapInfo>(BeatmapInfos)

    val version by BeatmapInfos.version
    val onlineId by BeatmapInfos.onlineId
    val audioLeadIn by BeatmapInfos.audioLeadIn
    val countdown by BeatmapInfos.countdown
    val sampleSet by BeatmapInfos.sampleSet
    val stackLeniency by BeatmapInfos.stackLeniency
    val mode by BeatmapInfos.mode
    val letterboxInBreaks by BeatmapInfos.letterboxInBreaks
    val widescreenStoryboard by BeatmapInfos.widescreenStoryboard
    val md5 by BeatmapInfos.md5
}

object BeatmapInfos : IntIdTable() {
    val version = text("version")
    val onlineId = integer("onlineId").nullable()
    val audioLeadIn = integer("audioLeadIn")
    val countdown = integer("countdown")
    val sampleSet = text("sampleSet")
    val stackLeniency = float("stackLeniency").default(0.7f)
    val mode = integer("mode")
    val letterboxInBreaks = integer("letterboxInBreaks")
    val widescreenStoryboard = integer("widescreenStoryboard").default(0)
    val md5 = text("md5")
}