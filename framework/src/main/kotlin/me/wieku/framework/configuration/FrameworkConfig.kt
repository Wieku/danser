package me.wieku.framework.configuration

import me.wieku.framework.backend.WindowMode
import me.wieku.framework.di.bindable.typed.*
import org.joml.Vector2i

object FrameworkConfig {

    val windowPosition = BindableVector2i(Vector2i(40, 40))

    val windowSize = BindableVector2i(Vector2i(1280, 720))

    val fullScreenResolution = BindableVector2i(Vector2i(1280, 720))

    val windowMode = BindableEnum(WindowMode.Windowed)

    val msaa = BindableInt(4)

    val vSync = BindableBoolean(false)

    val foregroundFPS = BindableInt(240)

    val backgroundFPS = BindableInt(60)

    val updateRate = BindableInt(1000)

    val updateRateBackground = BindableInt(250)

    val generalVolume = BindableFloat(1f)

    val musicVolume = BindableFloat(1f)

    val effectsVolume = BindableFloat(1f)

    @Transient
    val windowTitle = BindableString("Rocket2D")

}