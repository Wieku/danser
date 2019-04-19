package me.wieku.danser.beatmap

import javax.persistence.*

@Entity(name = "BeatmapCollection")
open class BeatmapCollection(
    var collectionName: String = ""
) {

    @Id
    @GeneratedValue
    @Column(unique = true)
    protected var id: Int? = null

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "BeatmapCollections",
        joinColumns = [JoinColumn(referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(referencedColumnName = "id")]
    )
    val beatmaps: List<Beatmap> = ArrayList()

}