package me.wieku.danser.beatmap

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

class Beatmap(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Beatmap>(Beatmaps)

    var beatmapSet by BeatmapSet referencedOn Beatmaps.beatmapSet
    var beatmapInfo by BeatmapInfo referencedOn Beatmaps.beatmapInfo
    var beatmapStatistics by BeatmapStatistics referencedOn Beatmaps.beatmapStatistics
    var beatmapDifficulty by BeatmapDifficulty referencedOn Beatmaps.beatmapDifficulty

    private var metadata by BeatmapMetadata optionalReferencedOn Beatmaps.beatmapMetadata

    var beatmapMetadata: BeatmapMetadata = metadata ?: beatmapSet.metadata
        set(value) {
            if (value == beatmapSet.metadata) {
                metadata?.delete()
                field = beatmapSet.metadata
            } else {
                metadata = value
                field = value
            }
        }
}

object Beatmaps : IntIdTable() {
    val beatmapSet = reference("beatmapSetId", BeatmapSets)
    val beatmapInfo = reference("beatmapInfoId", BeatmapInfos)
    val beatmapMetadata = reference("beatmapMetadataId", BeatmapMetadatas).nullable()
    val beatmapStatistics = reference("beatmapStatisticsId", BeatmapStatisticss)
    val beatmapDifficulty = reference("beatmapDifficultyId", BeatmapDifficulties)
}