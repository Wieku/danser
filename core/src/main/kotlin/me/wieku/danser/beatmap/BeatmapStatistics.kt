package me.wieku.danser.beatmap

import org.hibernate.annotations.CreationTimestamp
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity(name = "BeatmapStatistics")
open class BeatmapStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected var id: Int? = null

    var lastModified = 0L
    var timeAdded = 0L

    var playCount = 0
    var lastPlayed = 0L
    var starRating = 0f
    var isFavorite = false
}