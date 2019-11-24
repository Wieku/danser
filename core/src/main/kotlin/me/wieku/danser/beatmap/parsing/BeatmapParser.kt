package me.wieku.danser.beatmap.parsing

import me.wieku.danser.beatmap.Beatmap
import me.wieku.danser.beatmap.Break
import me.wieku.danser.beatmap.timing.SampleData
import me.wieku.danser.beatmap.timing.SampleSet
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.md5
import me.wieku.framework.resource.sha1
import java.util.*

class BeatmapParser {

    private val CURRENT_VERSION = 14

    var beatmap: Beatmap? = null
    var timeOffset = 0

    fun parse(file: FileHandle, beatmap: Beatmap) {
        var scanner = Scanner(file.fileURL.openStream(), "UTF-8")

        if (!scanner.hasNext()) {
            beatmap.parsedProperly = false
            return
        }

        val fileVersion = scanner.nextLine().substringAfter("osu file format v").toInt()

        //Some strange osu things (see https://github.com/ppy/osu/blob/master/osu.Game/Beatmaps/Formats/LegacyBeatmapDecoder.cs#L42)
        if (fileVersion < 5) {
            timeOffset = 24
        }

        beatmap.beatmapFile = file.filePath!!.fileName.toString()
        beatmap.beatmapInfo.md5 = file.file.md5()
        beatmap.beatmapInfo.sha1 = file.file.sha1()
        beatmap.beatmapStatistics.lastModified = file.file.lastModified()

        if (beatmap.beatmapStatistics.timeAdded == 0L) {
            beatmap.beatmapStatistics.timeAdded = System.currentTimeMillis()
        }

        beatmap.beatmapInfo.fileVersion = fileVersion
        beatmap.beatmapInfo.breaksText = ""
        beatmap.beatmapSet.directory = file.filePath!!.parent.fileName.toString()

        this.beatmap = beatmap

        var section = Section.Unknown
        while (scanner.hasNextLine()) {
            var line = scanner.nextLine()

            if (line.isBlank() || line.startsWith("//") || line.startsWith(" ") || line.startsWith("_")) {
                continue
            }

            if (line.startsWith("[") && line.endsWith("]")) {
                section = Section[line.substring(1, line.length - 1)]
                continue
            }

            line = line.replaceStartWithTabs().trim()

            val splittedLine = line.split(section.separator).map { it.trim() }

            when (section) {
                Section.General -> parseGeneral(splittedLine)
                Section.Metadata -> parseMetadata(splittedLine)
                Section.Difficulty -> parseDifficulty(splittedLine)
                Section.Events -> parseEvent(splittedLine)
                Section.TimingPoints -> parseTimingPoint(splittedLine)
            }

        }

    }

    private fun parseGeneral(line: List<String>) {
        when (line[0]) {
            "AudioFilename" -> beatmap?.beatmapMetadata?.audioFile = line[1]
            "AudioLeadIn" -> beatmap?.beatmapInfo?.audioLeadIn = getOffset(line[1].toInt())
            "PreviewTime" -> beatmap?.beatmapMetadata?.previewTime = getOffset(line[1].toInt())
            "Countdown" -> beatmap?.beatmapInfo?.countdown = line[1].toInt() == 1
            "SampleSet" -> {
                beatmap?.beatmapInfo?.sampleSet = line[1]
                beatmap?.timing?.baseSampleData = SampleData(SampleSet[line[1]], SampleSet.Inherited, 1, 1f)
            }
            "StackLeniency" -> beatmap?.beatmapInfo?.stackLeniency = line[1].toFloat()
            "Mode" -> beatmap?.beatmapInfo?.mode = line[1].toInt()
            "LetterboxInBreaks" -> beatmap?.beatmapInfo?.letterboxInBreaks = line[1].toInt() == 1
            "WidescreenStoryboard" -> beatmap?.beatmapInfo?.widescreenStoryboard = line[1].toInt() == 1
        }
    }

    private fun parseMetadata(line: List<String>) {
        when (line[0]) {
            "Title" -> beatmap?.beatmapMetadata?.title = line[1]
            "TitleUnicode" -> beatmap?.beatmapMetadata?.titleUnicode = line[1]
            "Artist" -> beatmap?.beatmapMetadata?.artist = line[1]
            "ArtistUnicode" -> beatmap?.beatmapMetadata?.artistUnicode = line[1]
            "Creator" -> beatmap?.beatmapMetadata?.creator = line[1]
            "Version" -> beatmap?.beatmapInfo?.version = line[1]
            "Source" -> beatmap?.beatmapMetadata?.source = line[1]
            "Tags" -> beatmap?.beatmapMetadata?.tags = line[1]
            "BeatmapID" -> beatmap?.beatmapInfo?.onlineId = line[1].toInt()
            "BeatmapSetID" -> beatmap?.beatmapSet?.onlineId = line[1].toInt()
        }
    }

    private fun parseDifficulty(line: List<String>) {
        when (line[0]) {
            "HPDrainRate" -> beatmap?.beatmapDifficulty?.hpDrain = line[1].toFloat()
            "CircleSize" -> beatmap?.beatmapDifficulty?.cs = line[1].toFloat()
            "OverallDifficulty" -> beatmap?.beatmapDifficulty?.od = line[1].toFloat()
            "ApproachRate" -> beatmap?.beatmapDifficulty?.ar = line[1].toFloat()
            "SliderMultiplier" -> beatmap?.beatmapDifficulty?.sliderMultiplier = line[1].toFloat()
            "SliderTickRate" -> beatmap?.beatmapDifficulty?.sliderTickRate = line[1].toFloat()
        }
    }

    private fun parseEvent(line: List<String>) {
        val event = Events[line[0]]
        when (event) {
            Events.Background -> beatmap?.beatmapMetadata?.backgroundFile = line[1]
            Events.Video -> beatmap?.beatmapMetadata?.videoFile = line[1]
            Events.Break -> beatmap?.beatmapInfo!!.breaks += Break(line[1].toLong(), line[2].toLong())
        }
    }

    private fun parseTimingPoint(line: List<String>) {
        val time = line[0].toFloat().toLong()
        val bpm = line[1].toFloat()

        if (line.size > 3) {
            val sampleVolume = if (line.size > 5) line[5].toFloat() / 100 else 1.0f
            val kiai = if (line.size > 7) line[7].toInt() == 1 else false

            beatmap!!.timing.addTimingPoint(
                time,
                bpm,
                SampleData(SampleSet[line[3]], SampleSet.Inherited, line[4].toInt(), sampleVolume),
                kiai
            )
        } else {
            beatmap!!.timing.addTimingPoint(time, bpm, null, false)
        }
    }

    private fun getOffset(time: Int): Int {
        return time + timeOffset
    }

    private fun getOffset(time: Float): Float {
        return time + timeOffset.toFloat()
    }

    private fun String.replaceStartWithTabs(): String {
        val index = indexOfFirst { !it.isWhitespace() }.let { if (it == -1) length else it }
        return "_".repeat(index) + substring(index)
    }
}