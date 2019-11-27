package me.wieku.framework.math

import me.wieku.framework.utils.EnumWithId
import org.joml.Vector2f

enum class Origin(override val enumId: Int, val offset: Vector2f): EnumWithId {
    TopLeft(0, Vector2f(-1f, -1f)),
    Centre(1, Vector2f(0f, 0f)),
    CentreLeft(2, Vector2f(-1f, 0f)),
    TopRight(3, Vector2f(1f, -1f)),
    BottomCentre(4, Vector2f(0f, 1f)),
    TopCentre(5, Vector2f(0f, -1f)),
    Custom(6, Vector2f(0f, 0f)),
    CentreRight(7, Vector2f(1f, 0f)),
    BottomLeft(8, Vector2f(-1f, 1f)),
    BottomRight(9, Vector2f(1f, 1f));

    companion object: EnumWithId.Companion<Origin>(Centre)
}