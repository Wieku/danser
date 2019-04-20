package me.wieku.danser.beatmap

import java.lang.IllegalStateException
import javax.persistence.*

@Entity(name = "Beatmap")
@Table(indexes = [Index(name = "idx", columnList = "id,beatmapFile")])
open class Beatmap(
    var beatmapFile: String = ""
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    protected var id: Int? = null

    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "beatmapSet")
    var beatmapSet: BeatmapSet = BeatmapSet()

    @ManyToMany(mappedBy = "beatmaps", cascade = [CascadeType.ALL])
    val collections: List<BeatmapCollection> = ArrayList()

    @OneToOne(cascade = [CascadeType.ALL])
    var beatmapInfo: BeatmapInfo = BeatmapInfo()

    @OneToOne(cascade = [CascadeType.ALL])
    var beatmapStatistics: BeatmapStatistics = BeatmapStatistics()

    @OneToOne(cascade = [CascadeType.ALL])
    var beatmapDifficulty: BeatmapDifficulty = BeatmapDifficulty()

    @OneToOne(cascade = [CascadeType.ALL])
    var metadata: BeatmapMetadata? = BeatmapMetadata()

    var beatmapMetadata: BeatmapMetadata
        get() = metadata ?: beatmapSet.metadata?:throw NullPointerException("Beatmap and BeatmapSet metadata are null")
        set(value) {
            if (value == beatmapSet.metadata) {
                metadata = null
            }
        }

    private fun validateMetadatas() {
        if (metadata == beatmapSet.metadata) {
            metadata = null
        }
    }

    @PrePersist
    @PreUpdate
    protected fun validatePreInsertUpdate() {
        validateMetadatas()
    }

}