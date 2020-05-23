package me.wieku.danser.beatmap

import me.wieku.danser.beatmap.parsing.BeatmapParser
import me.wieku.danser.database.transactional
import me.wieku.framework.logging.Logging
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

    private val logger = Logging.getLogger("database")

    private lateinit var entityManagerFactory: EntityManagerFactory
    private lateinit var entityManager: EntityManager
    private var parser = BeatmapParser()

    val beatmapSets = ArrayList<BeatmapSet>()
    private lateinit var beatmapSetCache: HashMap<String, BeatmapSet>

    var listener: ((Int, Int, String) -> Unit)? = null
    var messageListener: ((String) -> Unit)? = null

    fun start() {
        listener?.invoke(0, 2, "Beatmap Manager is starting...")
        logger.info("Beatmap Manager is starting...")

        entityManagerFactory = Persistence.createEntityManagerFactory("default")

        entityManager = entityManagerFactory.createEntityManager()

        Runtime.getRuntime().addShutdownHook(Thread {
            entityManager.close()
            entityManagerFactory.close()
        })

        listener?.invoke(1, 2, "Beatmap Manager started! Loading cached beatmaps...")
        logger.info("Beatmap Manager started! Loading cached beatmaps...")

        beatmapSets.addAll(
            entityManager.createQuery(
                "SELECT a FROM ${BeatmapSet::class.java.simpleName} a",
                BeatmapSet::class.java
            ).resultList as ArrayList<BeatmapSet>
        )

        beatmapSetCache = beatmapSets.map { it.directory to it }.toMap().toMutableMap() as HashMap<String, BeatmapSet>

        logger.info("Cached beatmaps loaded!")
        listener?.invoke(2, 2, "Cached beatmaps loaded!")
    }

    fun loadBeatmaps(location: String) {

        logger.info("Scanning \"$location\"...")

        val oszCandidates = File(location).listFiles { it -> it.extension == "osz" }

        if (oszCandidates != null && oszCandidates.isNotEmpty()) {
            val num = oszCandidates.size
            oszCandidates.forEachIndexed { index, file ->
                listener?.invoke(index, num, "Unpacking ${file.name}")
                FileHandle(file.absolutePath, FileType.Absolute).unpack()
            }
            listener?.invoke(num, num, "Files unpacked!")
        }

        entityManager.transactional {
            val candidates = File(location).listFiles { file -> file.isDirectory }
            if (candidates != null && candidates.isNotEmpty()) {
                candidates.forEachIndexed { index, file ->
                    listener?.invoke(index, candidates.size, "Scanning ${file.name}")
                    loadBeatmapBundle(file.toPath())
                }
                listener?.invoke(candidates.size, candidates.size, "Scanning finished!")
            }
        }

        listener?.invoke(1, 1, "Loading finished!")
    }

    private fun loadBeatmapBundle(path: Path) {
        var beatmapSet: BeatmapSet? = beatmapSetCache[path.fileName.toString()]

        val candidates = path.toFile().listFiles { _, name ->
            name.endsWith(".osu")
        }

        if ((candidates == null || candidates.isEmpty()) && beatmapSet != null) {
            entityManager.remove(beatmapSet)
        }

        if (beatmapSet == null) {
            beatmapSet = BeatmapSet()

            candidates?.forEach {
                val beatmap = Beatmap()
                logger.info("Importing: ${it.name}")
                messageListener?.invoke("Importing: ${it.name}")
                try {
                    parser.parse(FileHandle(it.absolutePath, FileType.Absolute), beatmap)
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    beatmap.parsedProperly = false
                }

                if (beatmap.parsedProperly) {
                    beatmapSet.beatmaps.add(beatmap)
                    logger.info("Imported successfully: ${it.name}")
                } else {
                    logger.error("Failed to import ${it.name}")
                }
            }

            if (beatmapSet.beatmaps.isNotEmpty()) {
                entityManager.persist(beatmapSet)
                beatmapSets.add(beatmapSet)
                beatmapSetCache[beatmapSet.directory] = beatmapSet
            }

        } else {
            val versionsMap = beatmapSet.beatmaps.map { it.beatmapFile to it }.toMap()

            candidates?.forEach {
                var beatmap = versionsMap[it.name]
                val wasNull = beatmap == null

                messageListener?.invoke("Checking: ${it.name}")

                if (beatmap != null && beatmap.beatmapStatistics.lastModified == it.lastModified()) {
                    return
                }

                logger.info("Found new beatmap version: ${it.name}")

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
                    logger.info("Imported successfully: ${it.name}")
                    if (wasNull) {
                        beatmapSet.beatmaps.add(beatmap)
                    }
                } else {
                    if (!wasNull) {
                        logger.info("This beatmap is corrupted, removing from database... ${it.name}")
                        beatmapSet.beatmaps.remove(beatmap)

                    } else {
                        logger.error("Failed to import ${it.name}")
                    }
                }
            }

            if (beatmapSet.beatmaps.isEmpty()) {
                logger.info("${beatmapSet.metadata?.artist} - ${beatmapSet.metadata?.title} (${beatmapSet.metadata?.creator}) contains no beatmaps, removing...")

                beatmapSets.remove(beatmapSet)
                beatmapSetCache.remove(beatmapSet.directory)
                entityManager.remove(beatmapSet)
            }
        }
    }

}