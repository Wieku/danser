package me.wieku.framework.math.color

import org.joml.Vector4f
import kotlin.math.abs

class Color : Vector4f {

    var r: Float
        get() = x
        set(value) {
            x = value
        }

    var g: Float
        get() = y
        set(value) {
            y = value
        }

    var b: Float
        get() = z
        set(value) {
            z = value
        }

    var a: Float
        get() = w
        set(value) {
            w = value
        }

    constructor(color: Int, hasAlpha: Boolean = true) : super() {
        val baseShift = if (hasAlpha) 24 else 16
        val rInt = (color ushr baseShift) and 0xFF
        val gInt = (color ushr (baseShift - 8)) and 0xFF
        val bInt = (color ushr (baseShift - 16)) and 0xFF
        val aInt = if (hasAlpha) color and 0xFF else 0xFF

        r = rInt / 255f
        g = gInt / 255f
        b = bInt / 255f
        a = aInt / 255f
    }

    constructor() : this(0xFFFFFFFF.toInt()) //TODO: Switch to UInt when it becomes not experimental

    constructor(r: Float, g: Float, b: Float, a: Float) : super(r, g, b, a)

    constructor(r: Int, g: Int, b: Int, a: Int) : super(r / 255f, g / 255f, b / 255f, a / 255f)

    constructor(h: Float, s: Float, v: Float) : super() {
        val hp = h * 6
        val c = v * s
        val xx = c * (1.0f - abs((hp % 2.0f) - 1.0f))

        val m = v - c
        var rr = 0.0f
        var gg = 0.0f
        var bb = 0.0f

        when {
            0.0 <= hp && hp < 1.0 -> {
                rr = c
                gg = xx
            }
            1.0 <= hp && hp < 2.0 -> {
                rr = xx
                gg = c
            }
            2.0 <= hp && hp < 3.0 -> {
                gg = c
                bb = xx
            }
            3.0 <= hp && hp < 4.0 -> {
                gg = xx
                bb = c
            }
            4.0 <= hp && hp < 5.0 -> {
                rr = xx
                bb = c
            }
            5.0 <= hp && hp < 6.0 -> {
                rr = c
                bb = xx
            }
        }

        r = m + rr
        g = m + gg
        b = m + bb
    }
}