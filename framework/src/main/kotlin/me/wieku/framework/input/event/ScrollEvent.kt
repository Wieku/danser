package me.wieku.framework.input.event

import org.joml.Vector2f
import org.joml.Vector2i

class ScrollEvent(cursorPosition: Vector2i, val scrollOffset: Vector2f) : CursorEvent(cursorPosition)