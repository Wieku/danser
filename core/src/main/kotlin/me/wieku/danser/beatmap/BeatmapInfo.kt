package me.wieku.danser.beatmap

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

class BeatmapInfo(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BeatmapInfo>(BeatmapInfos)

    var fileVersion by BeatmapInfos.fileVersion
    var version by BeatmapInfos.version
    var onlineId by BeatmapInfos.onlineId
    var audioLeadIn by BeatmapInfos.audioLeadIn
    var countdown by BeatmapInfos.countdown
    var sampleSet by BeatmapInfos.sampleSet
    var stackLeniency by BeatmapInfos.stackLeniency
    var mode by BeatmapInfos.mode
    var letterboxInBreaks by BeatmapInfos.letterboxInBreaks
    var widescreenStoryboard by BeatmapInfos.widescreenStoryboard
    var md5 by BeatmapInfos.md5
}

object BeatmapInfos : IntIdTable() {
    val fileVersion = integer("fileVersion")
    val version = text("version")
    val onlineId = integer("onlineId").nullable()
    val audioLeadIn = integer("audioLeadIn")
    val countdown = bool("countdown")
    val sampleSet = text("sampleSet")
    val stackLeniency = float("stackLeniency").default(0.7f)
    val mode = integer("mode")
    val letterboxInBreaks = bool("letterboxInBreaks")
    val widescreenStoryboard = bool("widescreenStoryboard").default(false)
    val md5 = text("md5")
}