package me.wieku.danser.ui.mainmenu

import me.wieku.danser.graphics.drawables.triangles.TriangleDirection
import me.wieku.danser.graphics.drawables.triangles.Triangles
import me.wieku.danser.ui.screens.SongSelect
import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.graphics.drawables.containers.ColorContainer
import me.wieku.framework.graphics.drawables.containers.YogaContainer
import me.wieku.framework.gui.screen.ScreenCache
import me.wieku.framework.math.Easing
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import me.wieku.framework.math.color.Color
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
            MenuButton("danse!", "\uf144", "FontAwesome-Regular", Color(65, 17, 158, 255), true) {
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
                Color(87, 191, 19, 255)
            ),
            MenuButton("settings", "\uf013", "FontAwesome-Solid", Color(232, 176, 0, 255)),
            MenuButton("exit", "\uf057", "FontAwesome-Regular", Color(255, 23, 224, 255))
        )
    }

    fun show(clicked: Boolean) {
        children.forEach {
            (it as MenuButton).show(clicked)
        }
    }

}