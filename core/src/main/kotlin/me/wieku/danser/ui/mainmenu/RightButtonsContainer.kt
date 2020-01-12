package me.wieku.danser.ui.mainmenu

import me.wieku.danser.ui.screens.SongSelect
import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.graphics.drawables.containers.ColorContainer
import me.wieku.framework.graphics.drawables.containers.YogaContainer
import me.wieku.framework.gui.screen.ScreenCache
import me.wieku.framework.math.Easing
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import org.joml.Vector2f
import org.joml.Vector4f
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.lwjgl.util.yoga.Yoga

class RightButtonsContainer : YogaContainer(), KoinComponent {

    private val screenStack: ScreenCache by inject()

    private var songSelect: SongSelect? = null

    init {
        anchor = Origin.None
        origin = Origin.CentreLeft
        fillMode = Scaling.StretchY
        scale = Vector2f(1f, 0.12f)
        isRoot = true
        yogaFlexDirection = Yoga.YGFlexDirectionRow
        yogaDirection = Yoga.YGDirectionLTR
        addChild(
            ColorContainer {
                fillMode = Scaling.Stretch
                scale = Vector2f(1f, 0f)
                color = Vector4f(0.2f, 0.2f, 0.2f, 0f)
            },
            MenuButton("danse!", "\uf144", "FontAwesome-Regular", Vector4f(65f, 17f, 158f, 255f).mul(1 / 255f), true) {
                action = {
                    if (songSelect == null) {
                        songSelect = SongSelect()
                    }
                    screenStack.push(songSelect!!)
                }
            },
            MenuButton(
                "download",
                "\uf358",
                "FontAwesome-Regular",
                Vector4f(97f * 0.9f, 212f * 0.9f, 21f * 0.9f, 255f).mul(1 / 255f)
            ),
            MenuButton("settings", "\uf013", "FontAwesome-Solid", Vector4f(232f, 176f, 0f, 255f).mul(1 / 255f)),
            MenuButton("exit", "\uf057", "FontAwesome-Regular", Vector4f(255f, 23f, 224f, 255f).mul(1 / 255f))
        )
    }

    fun show(clicked: Boolean) {
        children.forEach {
            when (it) {
                is ColorContainer -> {
                    it.addTransform(
                        Transform(
                            TransformType.ScaleVector,
                            clock.currentTime + if (clicked) 100f else 0f,
                            clock.currentTime + 400f,
                            Vector2f(1f, if (clicked) 0f else 1f),
                            Vector2f(1f, if (clicked) 1f else 0f),
                            Easing.InOutQuad
                        )
                    )
                    it.addTransform(
                        Transform(
                            TransformType.Fade,
                            clock.currentTime + if (clicked) 100f else 0f,
                            clock.currentTime + 400f,
                            if (clicked) 0f else 1f,
                            if (clicked) 1f else 0f,
                            Easing.InOutQuad
                        )
                    )
                }
                is MenuButton -> {
                    it.show(clicked)
                }
            }
        }
    }

}

/*
MenuButton("play", "\uf144", "FontAwesome-Regular", Vector4f(0.7f, 0f, 0f, 1f), true),
            MenuButton("download", "\uf358", "FontAwesome-Regular", Vector4f(0f, 0.7f, 0.7f, 1f)),
            MenuButton("settings", "\uf013", "FontAwesome-Solid", Vector4f(0.7f, 0.7f, 0f, 1f)),
            MenuButton("exit", "\uf057", "FontAwesome-Regular", Vector4f(0.7f, 0f, 0.7f, 1f))
 */