package me.wieku.danser.configuration

import me.wieku.framework.configuration.Config
import me.wieku.framework.di.bindable.typed.*
import java.nio.file.Paths

object DanserConfig: Config<DanserConfig.DanserConfigProperties>(DanserConfigProperties::class) {
    override val configFile: String = "danser.ini"

    val osuSongsDir = BindableString(Paths.get(System.getenv("localappdata"), "osu!", "Songs").toString())

    init {
        addProperty(DanserConfigSections.Main, DanserConfigProperties.OsuSongsDir, osuSongsDir)
    }

    enum class DanserConfigProperties {
        OsuSongsDir
    }

    enum class DanserConfigSections {
        Main
    }

}