package me.wieku.danser.beatmap

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

class BeatmapSet(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BeatmapSet>(BeatmapSets)

    var directory by BeatmapSets.setDir
    var onlineId by BeatmapSets.onlineId
    var metadata by BeatmapMetadata referencedOn BeatmapSets.metadataId
    val beatmaps by Beatmap referrersOn Beatmaps.beatmapSet
}

object BeatmapSets : IntIdTable() {
    val setDir = text("dir")
    val onlineId = integer("onlineId").nullable()
    val metadataId = reference("metadataId", BeatmapMetadatas)
}