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

    constructor(d: Float) : super(d)

    constructor() : this(1f)

    constructor(color: Int, hasAlpha: Boolean = true) : this() {
        setInt(color, hasAlpha)
    }

    constructor(r: Float, g: Float, b: Float, a: Float) : super(r, g, b, a)

    constructor(whiteness: Float, alpha: Float) : this(whiteness, whiteness, whiteness, alpha)

    constructor(r: Int, g: Int, b: Int, a: Int) : this(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f)

    constructor(h: Float, s: Float, v: Float) : this() {
        setHSV(h, s, v)
    }

    fun mixedWith(second: Color, t: Float): Color = set(mix(this, second, t)) as Color

    fun getHSV() = rgbToHsv(r, g, b)

    fun shifted(h: Float, s: Float = 0.0f, v: Float = 0.0f): Color {
        var (h1, s1, v1) = rgbToHsv(this.r, this.g, this.b)

        h1 = (h1 + h) % 1.0f
        if (h1 < 0.0f) h1 += 1.0f

        s1 = (s1 + s) % 1.0f
        if (s1 < 0.0f) s1 += 1.0f

        v1 = (v1 + v) % 1.0f
        if (v1 < 0.0f) v1 += 1.0f

        return setHSV(h1, s1, v1)
    }

    fun setHSV(h: Float, s: Float, v: Float): Color {
        val (r, g, b) = hsvToRgb(h, s, v)

        this.r = r
        this.g = g
        this.b = b

        return this
    }

    fun setInt(color: Int, hasAlpha: Boolean = true): Color {
        val baseShift = if (hasAlpha) 24 else 16
        val rInt = (color ushr baseShift) and 0xFF
        val gInt = (color ushr (baseShift - 8)) and 0xFF
        val bInt = (color ushr (baseShift - 16)) and 0xFF
        val aInt = if (hasAlpha) color and 0xFF else 0xFF

        r = rInt / 255.0f
        g = gInt / 255.0f
        b = bInt / 255.0f
        a = aInt / 255.0f

        return this
    }

    companion object {

        fun hsvToRgb(h: Float, s: Float, v: Float): Triple<Float, Float, Float> {
            val hp = h * 6
            val c = v * s
            val xx = c * (1.0f - abs((hp % 2.0f) - 1.0f))

            val m = v - c
            var rr = 0.0f
            var gg = 0.0f
            var bb = 0.0f

            when {
                0.0f <= hp && hp < 1.0f -> {
                    rr = c
                    gg = xx
                }
                1.0f <= hp && hp < 2.0f -> {
                    rr = xx
                    gg = c
                }
                2.0f <= hp && hp < 3.0f -> {
                    gg = c
                    bb = xx
                }
                3.0f <= hp && hp < 4.0f -> {
                    gg = xx
                    bb = c
                }
                4.0f <= hp && hp < 5.0f -> {
                    rr = xx
                    bb = c
                }
                5.0f <= hp && hp < 6.0f -> {
                    rr = c
                    bb = xx
                }
            }

            return Triple(m + rr, m + gg, m + bb)
        }

        fun rgbToHsv(r: Float, g: Float, b: Float): Triple<Float, Float, Float> {
            val min = minOf(r, g, b)
            val v = maxOf(r, g, b)
            val c = v - min

            val s = if (v != 0.0f) c / v else 0.0f

            var h = 0.0f
            if (min != v) {
                if (v == r) {
                    h = ((g - b) / c) % 6.0f
                }
                if (v == g) {
                    h = (b - r) / c + 2.0f
                }
                if (v == b) {
                    h = (r - g) / c + 4.0f
                }

                h /= 6.0f
                if (h < 0.0) {
                    h += 1.0f
                }
            }

            return Triple(h, s, v)
        }

        fun mix(color1: Color, color2: Color, t: Float): Color {
            return mix(color1, color2, t, Color())
        }

        fun mix(color1: Color, color2: Color, t: Float, destination: Color): Color {
            return destination.set(color2).sub(color1).mul(t.coerceIn(0.0f, 1.0f)).add(color1) as Color
        }

    }
}