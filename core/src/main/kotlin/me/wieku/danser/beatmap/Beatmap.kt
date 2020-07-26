package me.wieku.danser.beatmap

import me.wieku.danser.beatmap.parsing.BeatmapParser
import me.wieku.danser.beatmap.timing.BeatmapTiming
import me.wieku.danser.audio.SampleData
import me.wieku.danser.audio.SampleSet
import me.wieku.danser.configuration.DanserConfig
import me.wieku.framework.audio.Track
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import java.io.File
import java.nio.file.Paths
import javax.persistence.*
import kotlin.jvm.Transient

@Entity(name = "Beatmap")
@Table(indexes = [Index(name = "idx", columnList = "id,beatmapFile")])
class Beatmap(
    var beatmapFile: String = ""
) {

    @Transient
    var parsedProperly = true

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
        get() = metadata ?: beatmapSet.metadata ?: throw NullPointerException("Beatmap and BeatmapSet metadata are null")
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

    @PostLoad
    protected fun postLoad() {
        timing.baseSampleData =
            SampleData(SampleSet[beatmapInfo.sampleSet], SampleSet.Normal, 1, 1f)
    }

    @Transient
    val timing = BeatmapTiming()

    @Transient
    private lateinit var track: Track

    fun getTrack(): Track {
        return track
    }

    fun loadTrack(local: Boolean = false) {
        val basePath = if (local) "assets/beatmaps/" else DanserConfig.osuSongsDir.value
        track = Track(
            FileHandle(
                Paths.get(basePath, beatmapSet.directory, beatmapMetadata.audioFile).toString(),
                if(local) FileType.Classpath else FileType.Absolute
            )
        )
        BeatmapParser().parse(
            FileHandle(
                Paths.get(basePath, beatmapSet.directory, beatmapFile).toString(),
                if (local) FileType.Classpath else FileType.Absolute
            ), this, true
        )
    }

}