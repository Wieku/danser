package me.wieku.framework.utils

import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector4f

class MaskingInfo {
    var rect = Vector4f()
    var maskToLocalCoords = Matrix4f()
    var radius = 0f
    var blendRange = 1f
}