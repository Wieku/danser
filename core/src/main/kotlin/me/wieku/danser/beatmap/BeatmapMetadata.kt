package me.wieku.danser.beatmap

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import java.util.*

class BeatmapMetadata(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BeatmapMetadata>(BeatmapMetadatas)

    val title by BeatmapMetadatas.title
    val titleUnicode by BeatmapMetadatas.titleUnicode
    val artist by BeatmapMetadatas.artist
    val artistUnicode by BeatmapMetadatas.artistUnicode
    val creator by BeatmapMetadatas.creator
    val source by BeatmapMetadatas.ssource
    val tags by BeatmapMetadatas.tags
    val audioFile by BeatmapMetadatas.audio
    val previewTime by BeatmapMetadatas.preview
    val backgroundFile by BeatmapMetadatas.background

    override fun equals(other: Any?): Boolean {
        if (other !is BeatmapMetadata) {
            return false
        }

        return other.title == this.title && Objects.equals(
            other.titleUnicode,
            this.titleUnicode
        ) && other.artist == this.artist && Objects.equals(
            other.artistUnicode,
            this.artistUnicode
        ) && other.creator == this.creator && Objects.equals(other.source, this.source) && Objects.equals(
            other.tags,
            this.tags
        ) && other.audioFile == this.audioFile && other.previewTime == this.previewTime && Objects.equals(
            other.backgroundFile,
            this.backgroundFile
        )
    }
}

object BeatmapMetadatas : IntIdTable() {
    val title = text("title")
    val titleUnicode = text("titleUnicode").nullable()
    val artist = text("artist")
    val artistUnicode = text("artistUnicode").nullable()
    val creator = text("creator")
    val ssource = text("source").nullable()
    val tags = text("tags").nullable()
    val audio = text("audio")
    val preview = integer("preview")
    val background = text("background").nullable()
}