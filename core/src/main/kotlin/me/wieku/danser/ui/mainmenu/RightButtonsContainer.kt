package me.wieku.danser.ui.mainmenu

import me.wieku.framework.graphics.drawables.containers.ColorContainer
import me.wieku.framework.graphics.drawables.containers.YogaContainer
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import org.joml.Vector2f
import org.joml.Vector4f
import org.lwjgl.util.yoga.Yoga

class RightButtonsContainer: YogaContainer() {

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
                color = Vector4f(0.2f, 0.2f, 0.2f, 1f)
            },
            MenuButton("play", "\uf144", "FontAwesome-Regular", Vector4f(65f, 17f, 158f, 255f).mul(1/255f), true),
            MenuButton("download", "\uf358", "FontAwesome-Regular", Vector4f(97f*0.9f, 212f*0.9f, 21f*0.9f, 255f).mul(1/255f)),
            MenuButton("settings", "\uf013", "FontAwesome-Solid", Vector4f( 232f, 176f, 0f, 255f).mul(1/255f)),
            MenuButton("exit", "\uf057", "FontAwesome-Regular", Vector4f(255f, 23f, 224f, 255f).mul(1/255f))
        )
    }

}

/*
MenuButton("play", "\uf144", "FontAwesome-Regular", Vector4f(0.7f, 0f, 0f, 1f), true),
            MenuButton("download", "\uf358", "FontAwesome-Regular", Vector4f(0f, 0.7f, 0.7f, 1f)),
            MenuButton("settings", "\uf013", "FontAwesome-Solid", Vector4f(0.7f, 0.7f, 0f, 1f)),
            MenuButton("exit", "\uf057", "FontAwesome-Regular", Vector4f(0.7f, 0f, 0.7f, 1f))
 */