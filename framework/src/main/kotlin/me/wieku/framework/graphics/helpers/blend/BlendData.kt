package me.wieku.framework.graphics.helpers.blend

import me.wieku.framework.math.color.Color

internal data class BlendData (
    var enabled: Boolean = false,
    var source: BlendFactor = BlendFactor.One,
    var destination: BlendFactor = BlendFactor.Zero,
    var sourceAlpha: BlendFactor = source,
    var destinationAlpha: BlendFactor = destination,
    var equation: BlendEquation = BlendEquation.Add,
    var equationAlpha: BlendEquation = equation,
    var color: Color = Color(0f)
) {
    fun copy() = BlendData(enabled, source, destination, sourceAlpha, destinationAlpha, equation, equationAlpha, color.cpy())
}