package me.wieku.framework.math.curves

import me.wieku.framework.math.clamped
import me.wieku.framework.math.curves.approximation.CurveApproximator
import me.wieku.framework.utils.binarySearchIndex
import org.joml.Vector2f

open class ApproximatedCurve(approximator: CurveApproximator): Curve2d {

    private val lines = ArrayList<Line>()

    private var combinedLength = 0f

    private val sections = ArrayList<Float>()

    init {
        lines.addAll(approximator.approximate())

        sections.add(combinedLength)

        lines.forEach {
            combinedLength += it.getLength()
            sections.add(combinedLength)
        }
    }

    override fun getStartAngle(): Float = lines.first().getStartAngle()

    override fun getEndAngle(): Float = lines.last().getEndAngle()

    override fun pointAt(t: Float, dest: Vector2f): Vector2f {
        val desiredWidth = combinedLength * t.clamped(0f, 1f)

        val index = sections.binarySearchIndex(0, sections.size - 1) {
            if (sections[it] <= desiredWidth) {
                if (sections[it + 1] > desiredWidth) 0 else -1
            } else 1
        }

        val line = lines[index]
        val section = sections[index]
        val nextSection = sections[index + 1]

        return line.pointAt((desiredWidth - section) / (nextSection - section), dest)
    }

    override fun getLength(): Float = combinedLength


}