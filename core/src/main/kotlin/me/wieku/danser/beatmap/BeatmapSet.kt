package me.wieku.danser.beatmap

import javax.persistence.*

@Entity(name = "BeatmapSet")
@Table(indexes = [Index(name = "idx1", columnList = "id,directory")])
open class BeatmapSet(
    //@Column(unique = true)
    var directory: String = "",
    var onlineId: Int? = null
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    protected var id: Int? = null

    @OneToOne(cascade = [CascadeType.ALL])
    var metadata: BeatmapMetadata? = null

    @OneToMany(mappedBy = "beatmapSet", cascade = [CascadeType.ALL])
    val beatmaps: List<Beatmap> = ArrayList()

    @PrePersist
    protected fun cleanBeatmaps() {
        beatmaps.forEach {
            if (directory.isEmpty()) {
                directory = it.beatmapSet.directory
            }

            if (onlineId == null) {
                onlineId = it.beatmapSet.onlineId
            }

            if (metadata == null) {
                metadata = it.metadata
            }

            if(metadata == it.metadata) {
                it.metadata = null
            }

            it.beatmapSet = this
        }
    }

}