package me.wieku.danser.beatmap

import me.wieku.danser.database.eager
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

class Beatmap(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Beatmap>(Beatmaps)

    var beatmapSet by BeatmapSet referencedOn Beatmaps.beatmapSet//.eager(this)
    var beatmapInfo by BeatmapInfo referencedOn Beatmaps.beatmapInfo
    var beatmapStatistics by BeatmapStatistics referencedOn Beatmaps.beatmapStatistics
    var beatmapDifficulty by BeatmapDifficulty referencedOn Beatmaps.beatmapDifficulty

    var beatmapMetadata by BeatmapMetadata referencedOn Beatmaps.beatmapMetadata
}

object Beatmaps : IntIdTable() {
    val beatmapSet = reference("beatmapSetId", BeatmapSets)
    val beatmapInfo = reference("beatmapInfoId", BeatmapInfos)
    val beatmapMetadata = reference("beatmapMetadataId", BeatmapMetadatas)
    val beatmapStatistics = reference("beatmapStatisticsId", BeatmapStatisticss)
    val beatmapDifficulty = reference("beatmapDifficultyId", BeatmapDifficulties)
}