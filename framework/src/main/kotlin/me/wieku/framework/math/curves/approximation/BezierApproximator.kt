package me.wieku.framework.math.curves.approximation

import me.wieku.framework.math.cpy
import me.wieku.framework.math.curves.Line
import org.joml.Vector2f
import java.util.*
import kotlin.collections.ArrayList

class BezierApproximator(tolerance: Float, private val points: List<Vector2f>) : CurveApproximator {

    private val toleranceSq = tolerance * tolerance

    private val count = points.size
    private val subdivisionBuffer1 = arrayOfNulls<Vector2f>(points.size)
    private val subdivisionBuffer2 = arrayOfNulls<Vector2f>(points.size * 2 - 1)

    override fun approximate(): Array<Line> {
        val output = ArrayList<Vector2f>()

        if (count == 0) {
            return emptyArray()
        }

        val toFlatten = ArrayDeque<Array<Vector2f?>>()
        val freeBuffers = ArrayDeque<Array<Vector2f?>>()

        toFlatten.push(points.toTypedArray())

        val leftChild = subdivisionBuffer2

        while (toFlatten.size > 0) {
            val parent = toFlatten.pop()
            if (isFlatEnough(parent)) {
                approximate(parent, output)
                freeBuffers.push(parent)
                continue
            }

            val rightChild = when {
                freeBuffers.size > 0 -> freeBuffers.pop()
                else -> arrayOfNulls(count)
            }

            subdivide(parent, leftChild, rightChild)

            for (i in 0 until count) {
                parent[i] = leftChild[i]
            }

            toFlatten.push(rightChild)
            toFlatten.push(parent)
        }

        output.add(points.last())

        return Array(output.size - 1) { p ->
            Line(
                output[p],
                output[p + 1]
            )
        }
    }

    private fun isFlatEnough(controlPoints: Array<Vector2f?>): Boolean {
        val tmp1 = Vector2f()
        val tmp2 = Vector2f()
        for (i in 1 until controlPoints.size - 1) {
            if (tmp1.set(controlPoints[i - 1]).sub(tmp2.set(controlPoints[i]).mul(2f)).add(controlPoints[i + 1]).lengthSquared() > toleranceSq) {
                return false
            }
        }

        return true
    }

    private fun subdivide(controlPoints: Array<Vector2f?>, l: Array<Vector2f?>, r: Array<Vector2f?>) {
        val midpoints = subdivisionBuffer1

        for (i in 0 until count) {
            midpoints[i] = controlPoints[i]
        }

        for (i in 0 until count) {
            l[i] = midpoints[0]
            r[count - i - 1] = midpoints[count - i - 1]

            for (j in 0 until count - i - 1) {
                midpoints[j] = midpoints[j]!!.cpy().add(midpoints[j + 1]).mul(0.5f)
            }
        }
    }

    private fun approximate(controlPoints: Array<Vector2f?>, output: ArrayList<Vector2f>) {
        val l = subdivisionBuffer2
        val r = subdivisionBuffer1

        subdivide(controlPoints, l, r)

        for (i in 0 until count - 1) {
            l[count + i] = r[i + 1]
        }

        output.add(controlPoints[0]!!)

        for (i in 1 until count - 1) {
            val index = 2 * i
            val p = l[index - 1]!!.cpy().add(l[index]!!.cpy().mul(2.0f)).add(l[index + 1]).mul(0.25f)
            output.add(p)
        }
    }
}