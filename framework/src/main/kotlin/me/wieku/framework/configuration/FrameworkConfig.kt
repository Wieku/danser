package me.wieku.framework.configuration

import me.wieku.framework.backend.WindowMode
import me.wieku.framework.di.bindable.typed.*
import org.joml.Vector2i

object FrameworkConfig : Config<FrameworkConfig.FrameworkConfigProperties>(FrameworkConfigProperties::class) {
    override val configFile: String = "framework.ini"

    val windowPosition = BindableVector2i(Vector2i(40, 40))

    val windowSize = BindableVector2i(Vector2i(1280, 720))

    val fullScreenResolution = BindableVector2i(Vector2i(1280, 720))

    val windowMode = BindableEnum(WindowMode.Windowed)

    val msaa = BindableInt(4)

    val vSync = BindableBoolean(false)

    val foregroundFPS = BindableInt(0)

    val backgroundFPS = BindableInt(60)

    val updateRate = BindableInt(0)

    val updateRateBackground = BindableInt(250)

    val audioDevice = BindableString("")

    val generalVolume = BindableFloat(1f)

    val musicVolume = BindableFloat(1f)

    val effectsVolume = BindableFloat(1f)

    @Transient
    val windowTitle = BindableString("Rocket2D")

    //It could be done by reflections, but I think it's a cleaner solution
    init {
        addProperty(FrameworkConfigSections.Graphics, FrameworkConfigProperties.WindowPosition, windowPosition)
        addProperty(FrameworkConfigSections.Graphics, FrameworkConfigProperties.WindowSize, windowSize)
        addProperty(
            FrameworkConfigSections.Graphics,
            FrameworkConfigProperties.FullScreenResolution,
            fullScreenResolution
        )
        addProperty(FrameworkConfigSections.Graphics, FrameworkConfigProperties.WindowMode, windowMode)
        addProperty(FrameworkConfigSections.Graphics, FrameworkConfigProperties.MSAA, msaa)
        addProperty(FrameworkConfigSections.Graphics, FrameworkConfigProperties.VSync, vSync)
        addProperty(FrameworkConfigSections.Graphics, FrameworkConfigProperties.ForegroundFPS, foregroundFPS)
        addProperty(FrameworkConfigSections.Graphics, FrameworkConfigProperties.BackgroundFPS, backgroundFPS)
        addProperty(FrameworkConfigSections.Performance, FrameworkConfigProperties.UpdateRate, updateRate)
        addProperty(
            FrameworkConfigSections.Performance,
            FrameworkConfigProperties.UpdateRateBackground,
            updateRateBackground
        )
        addProperty(FrameworkConfigSections.Audio, FrameworkConfigProperties.AudioDevice, audioDevice)
        addProperty(FrameworkConfigSections.Audio, FrameworkConfigProperties.GeneralVolume, generalVolume)
        addProperty(FrameworkConfigSections.Audio, FrameworkConfigProperties.MusicVolume, musicVolume)
        addProperty(FrameworkConfigSections.Audio, FrameworkConfigProperties.EffectsVolume, effectsVolume)
    }

    enum class FrameworkConfigProperties {
        WindowPosition,
        WindowSize,
        FullScreenResolution,
        WindowMode,
        MSAA,
        VSync,
        ForegroundFPS,
        BackgroundFPS,
        UpdateRate,
        UpdateRateBackground,
        AudioDevice,
        GeneralVolume,
        MusicVolume,
        EffectsVolume
    }

    enum class FrameworkConfigSections {
        Graphics,
        Audio,
        Performance
    }

}