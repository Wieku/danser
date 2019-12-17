package me.wieku.danser.beatmap

import me.wieku.danser.beatmap.parsing.BeatmapParser
import me.wieku.danser.database.transactional
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import me.wieku.framework.resource.sha1
import me.wieku.framework.resource.unpack
import java.io.File
import java.lang.Exception
import java.nio.file.Path
import java.util.zip.ZipFile
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence

object BeatmapManager {
    private var entityManagerFactory:EntityManagerFactory
    private var entityManager: EntityManager
    private var parser = BeatmapParser()

    val beatmapSets = ArrayList<BeatmapSet>()
    private var beatmapSetCache: HashMap<String, BeatmapSet>

    init {
        val time = System.currentTimeMillis()
        entityManagerFactory = Persistence.createEntityManagerFactory("default")

        val time2 = System.currentTimeMillis()

        entityManager = entityManagerFactory.createEntityManager()

        val time3 = System.currentTimeMillis()

        Runtime.getRuntime().addShutdownHook(Thread {
            entityManager.close()
            entityManagerFactory.close()
        })

        beatmapSets.addAll(
            entityManager.createQuery(
                "SELECT a FROM ${BeatmapSet::class.java.simpleName} a",
                BeatmapSet::class.java
            ).resultList as ArrayList<BeatmapSet>
        )

        val time4 = System.currentTimeMillis()

        println("First ${time2-time}ms")
        println("Second ${time3-time2}ms")
        println("Third ${time4-time3}ms")

        beatmapSetCache = beatmapSets.map { it.directory to it }.toMap().toMutableMap() as HashMap<String, BeatmapSet>
    }

    fun loadBeatmaps(location: String) {

        File(location).listFiles { it -> it.extension == "osz" }?.forEach {
            it.unpack()
        }

        entityManager.transactional {
            File(location).listFiles { file -> file.isDirectory }?.forEach { file ->
                loadBeatmapBundle(file.toPath())
            }
        }
    }

    private fun loadBeatmapBundle(path: Path) {
        var beatmapSet: BeatmapSet? = beatmapSetCache[path.fileName.toString()]

        val candidates = path.toFile().listFiles { _, name ->
            name.endsWith(".osu")
        }

        if (candidates.isEmpty() && beatmapSet != null) {
            entityManager.remove(beatmapSet)
        }

        if (beatmapSet == null) {
            beatmapSet = BeatmapSet()

            candidates.forEach {
                val beatmap = Beatmap()
                println("Importing: ${it.name}")

                try {
                    parser.parse(FileHandle(it.absolutePath, FileType.Absolute), beatmap)
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    beatmap.parsedProperly = false
                }

                if (beatmap.parsedProperly) {
                    beatmapSet.beatmaps.add(beatmap)
                    println("Imported successfully: ${it.name}")
                } else {
                    println("Failed to import ${it.name}")
                }
            }

            if (beatmapSet.beatmaps.isNotEmpty()) {
                entityManager.persist(beatmapSet)
                beatmapSets.add(beatmapSet)
                beatmapSetCache[beatmapSet.directory] = beatmapSet
            }

        } else {
            val versionsMap = beatmapSet.beatmaps.map { it.beatmapFile to it }.toMap()

            candidates.forEach {
                var beatmap = versionsMap[it.name]
                val wasNull = beatmap == null

                if (beatmap != null && beatmap.beatmapStatistics.lastModified == it.lastModified()) {
                    return
                }

                println("Found new beatmap version: ${it.name}")

                if (beatmap == null) {
                    beatmap = Beatmap()
                }

                try {
                    parser.parse(FileHandle(it.absolutePath, FileType.Absolute), beatmap)
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    beatmap.parsedProperly = false
                }

                if (beatmap.parsedProperly) {
                    println("Imported successfully: ${it.name}")
                    if (wasNull) {
                        beatmapSet.beatmaps.add(beatmap)
                    }
                } else {
                    if (!wasNull) {
                        println("This beatmap is corrupted, removing from database... ${it.name}")
                        beatmapSet.beatmaps.remove(beatmap)

                    } else {
                        println("Failed to import ${it.name}")
                    }
                }
            }

            if (beatmapSet.beatmaps.isEmpty()) {
                println("${beatmapSet.metadata?.artist} - ${beatmapSet.metadata?.title} (${beatmapSet.metadata?.creator}) contains no beatmaps, removing...")

                beatmapSets.remove(beatmapSet)
                beatmapSetCache.remove(beatmapSet.directory)
                entityManager.remove(beatmapSet)
            }
        }
    }

}