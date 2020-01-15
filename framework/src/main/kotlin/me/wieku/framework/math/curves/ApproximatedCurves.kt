package me.wieku.framework.math.curves

import me.wieku.framework.math.curves.approximation.BezierApproximator
import me.wieku.framework.math.curves.approximation.CentripetalCatmullRomApproximator
import me.wieku.framework.math.curves.approximation.CircularArcApproximator
import org.joml.Vector2f

class ApproximatedBezier(tolerance: Float, points: List<Vector2f>) :
    ApproximatedCurve(BezierApproximator(tolerance, points))

class ApproximatedCircularArc(detail: Float, point1: Vector2f, point2: Vector2f, point3: Vector2f) :
    ApproximatedCurve(CircularArcApproximator(detail, CircularArc(point1, point2, point3)))

class ApproximatedCentripetalCatmullRom(detail: Int, points: Array<Vector2f>) :
    ApproximatedCurve(CentripetalCatmullRomApproximator(detail, CentripetalCatmullRom(points)))