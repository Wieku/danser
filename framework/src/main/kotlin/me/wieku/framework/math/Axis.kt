package me.wieku.framework.math

import me.wieku.framework.utils.EnumWithId
import org.joml.Vector2f

enum class Axis(override val enumId: Int, val offset: Vector2f): EnumWithId {
    None(0, Vector2f(0f, 0f)),
    X(1, Vector2f(1f, 0f)),
    Y(2, Vector2f(0f, 1f)),
    XY(3, Vector2f(1f, 1f));

    companion object: EnumWithId.Companion<Axis>(None)
}