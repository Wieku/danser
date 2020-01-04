package me.wieku.framework.configuration

import me.wieku.framework.backend.WindowMode
import me.wieku.framework.di.bindable.typed.*
import org.joml.Vector2i

object FrameworkConfig: Config<FrameworkConfig.FrameworkConfigProperties>(FrameworkConfigProperties::class) {
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
        addProperty(FrameworkConfigProperties.WindowPosition, windowPosition)
        addProperty(FrameworkConfigProperties.WindowSize, windowSize)
        addProperty(FrameworkConfigProperties.FullScreenResolution, fullScreenResolution)
        addProperty(FrameworkConfigProperties.WindowMode, windowMode)
        addProperty(FrameworkConfigProperties.MSAA, msaa)
        addProperty(FrameworkConfigProperties.VSync, vSync)
        addProperty(FrameworkConfigProperties.ForegroundFPS, foregroundFPS)
        addProperty(FrameworkConfigProperties.BackgroundFPS, backgroundFPS)
        addProperty(FrameworkConfigProperties.UpdateRate, updateRate)
        addProperty(FrameworkConfigProperties.UpdateRateBackground, updateRateBackground)
        addProperty(FrameworkConfigProperties.AudioDevice, audioDevice)
        addProperty(FrameworkConfigProperties.GeneralVolume, generalVolume)
        addProperty(FrameworkConfigProperties.MusicVolume, musicVolume)
        addProperty(FrameworkConfigProperties.EffectsVolume, effectsVolume)
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

}