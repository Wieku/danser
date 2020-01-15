package me.wieku.framework.math.curves

import me.wieku.framework.math.clamped
import me.wieku.framework.math.cpy
import me.wieku.framework.math.vector2fRad
import me.wieku.framework.utils.binarySearchIndex
import org.joml.Vector2f
import java.util.*

class ApproximatedMultiCurve : Curve2d {

    private var lines = ArrayList<Line>()
    private val sections = ArrayList<Float>()
    private var combinedLength = 0f

    private var temp1 = Vector2f()
    private var temp2 = Vector2f()

    constructor(lines: List<Line>) {
        this.lines.addAll(lines)
        this.lines.forEach { combinedLength += it.getLength() }
    }

    constructor(curveType: MultiCurveType, points: List<Vector2f>) {
        var type = curveType

        var points1 = LinkedList<Vector2f>()
        points1.addAll(points)

        if (points1.size < 3) {
            type = MultiCurveType.Linear
        }

        if (type == MultiCurveType.Perfect) {
            val c = ApproximatedCircularArc(0.125f, points1[0], points1[1], points1[2])
            lines.addAll(c.lines)
        }

        if (type == MultiCurveType.Linear) {
            for (i in 1 until points1.size) {
                val c = Line(points1[i - 1], points1[i])
                lines.add(c)
            }
        }

        if (type == MultiCurveType.Bezier) {
            var lastIndex = 0
            for ((i, p) in points1.withIndex()) {
                if ((i == points1.size - 1 && p != points1[i - 1]) || (i < points1.size - 1 && points1[i + 1] == p)) {
                    var pts = points1.slice(lastIndex until i + 1)

                    when {
                        pts.size > 2 -> lines.addAll(ApproximatedBezier(0.5f, pts).lines)
                        pts.size == 2 -> lines.add(Line(pts[0], pts[1]))
                        else -> lines.add(Line(pts[0], pts[0]))
                    }

                    lastIndex = i + 1
                }
            }
        }

        if (type == MultiCurveType.Catmull) {
            if (points1[0] != points1[1]) {
                points1.addFirst(Vector2f(points1.first))
            }

            if (points1[points1.size - 1] != points1[points1.size - 2]) {
                points1.addLast(Vector2f(points1.last))
            }

            for (i in 0 until points1.size - 3) {
                val c = ApproximatedCentripetalCatmullRom(50, points1.slice(i until (i + 4)).toTypedArray())
                lines.addAll(c.lines)
            }
        }

        this.lines.forEach { combinedLength += it.getLength() }
    }

    constructor(curveType: MultiCurveType, points: List<Vector2f>, desiredLength: Float) : this(curveType, points) {

        if (combinedLength > desiredLength) {
            var diff = combinedLength - desiredLength
            combinedLength -= diff
            var i = lines.size - 1
            while(i >= 0 && diff > 0.0) {
                val line = lines[i]
                if (diff >= line.getLength()) {
                    diff -= line.getLength()
                    lines.removeAt(i)
                } else {
                    lines[i] = Line(line.pointAt(0f), line.pointAt((line.getLength() - diff) / line.getLength()))
                    break
                }

                i--
            }

        } else if (desiredLength > combinedLength) {
            val last = lines.last()
            val p2 = last.pointAt(1f)
            val p3 = vector2fRad(last.getEndAngle(), desiredLength-combinedLength).add(p2)
            val c = Line(p2.cpy(), p3)
            lines.add(c)
            combinedLength += c.getLength()
        }

        var length = 0f

        sections.add(length)

        lines.forEach {
            length += it.getLength()
            sections.add(length)
        }
    }

    override fun getStartAngle(): Float = lines.first().getStartAngle()

    override fun getEndAngle(): Float = lines.last().getEndAngle()

    override fun pointAt(t: Float): Vector2f = pointAt(t, Vector2f())

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

    override fun getLength() = combinedLength

}