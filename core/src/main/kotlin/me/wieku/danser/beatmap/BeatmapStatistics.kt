package me.wieku.danser.beatmap

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

class BeatmapStatistics(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BeatmapStatistics>(BeatmapStatisticss)

    var lastModified by BeatmapStatisticss.lastModified
    var timeAdded by BeatmapStatisticss.timeAdded
    var playCount by BeatmapStatisticss.playCount
    var lastPlayed by BeatmapStatisticss.lastPlayed
    var starRating by BeatmapStatisticss.starRating
    var isFavorite by BeatmapStatisticss.favorite
}

object BeatmapStatisticss : IntIdTable() {
    val lastModified = integer("lastModified").default(-1)
    val timeAdded = integer("timeAdded").default(-1)
    val playCount = integer("playCount").default(0)
    val lastPlayed = integer("lastPlayed").default(-1)
    val starRating = float("starRating").default(0f)
    val favorite = bool("favorite").default(false)
}