package me.wieku.framework.input.event

import me.wieku.framework.input.MouseButton
import org.joml.Vector2i

class MouseDownEvent(cursorPosition: Vector2i, val button: MouseButton) : CursorEvent(cursorPosition)