package me.wieku.danser.beatmap

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Table

class BeatmapCollection(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<BeatmapCollection>(Collections)

    var collectionName by Collections.collectionName
    val beatmaps by Beatmap.via(
        CollectionBeatmaps.collectionId,
        CollectionBeatmaps.beatmapId
    )
}

object Collections: IntIdTable() {
    val collectionName = text("name")
}

object CollectionBeatmaps: Table() {
    val collectionId = reference("collectionId", Collections)
    val beatmapId = reference("beatmapId", Beatmaps)
}