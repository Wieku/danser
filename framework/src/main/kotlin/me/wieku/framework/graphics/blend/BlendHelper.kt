package me.wieku.framework.graphics.blend

import me.wieku.framework.math.color.Color
import org.lwjgl.opengl.GL33.*
import java.util.*

object BlendHelper {

    private val blendStack = ArrayDeque<BlendData>()

    private var currentBlendData = BlendData()

    fun setFunction(
        source: BlendFactor,
        destination: BlendFactor,
        sourceAlpha: BlendFactor = source,
        destinationAlpha: BlendFactor = destination
    ) {
        if (currentBlendData.source == source &&
            currentBlendData.destination == destination &&
            currentBlendData.sourceAlpha == sourceAlpha &&
            currentBlendData.destinationAlpha == destinationAlpha
        ) return

        glBlendFuncSeparate(source.enumId, destination.enumId, sourceAlpha.enumId, destinationAlpha.enumId)

        currentBlendData.source = source
        currentBlendData.sourceAlpha = sourceAlpha
        currentBlendData.destination = destination
        currentBlendData.destinationAlpha = destinationAlpha
    }

    fun setEquation(equation: BlendEquation, equationAlpha: BlendEquation = equation) {
        if (currentBlendData.equation == equation && currentBlendData.equationAlpha == equationAlpha) return

        glBlendEquationSeparate(equation.enumId, equationAlpha.enumId)

        currentBlendData.equation = equation
        currentBlendData.equationAlpha = equationAlpha
    }

    fun setColor(color: Color) {
        if (currentBlendData.color == color) return

        glBlendColor(color.r, color.g, color.b, color.a)

        currentBlendData.color = color.cpy()
    }

    fun enable() {
        if (currentBlendData.enabled) return

        glEnable(GL_BLEND)

        currentBlendData.enabled = true
    }

    fun disable() {
        if (!currentBlendData.enabled) return

        glDisable(GL_BLEND)

        currentBlendData.enabled = false
    }

    fun pushBlend() {
        blendStack.push(currentBlendData.copy())
    }

    fun popBlend() {
        val data = blendStack.pop() ?: BlendData()

        if (data.enabled)
            enable()
        else
            disable()

        setEquation(data.equation, data.equationAlpha)
        setColor(data.color)
        setFunction(data.source, data.destination, data.sourceAlpha, data.destinationAlpha)
    }
}