package me.wieku.framework.math

object Easings {

    /* ========================
	Using equations from: https://github.com/fogleman/ease/blob/master/ease.go
   ========================*/

    val Linear = fun(t: Float): Float {
        return t
    }

    val InQuad = fun(t: Float): Float {
        return t * t
    }

    val OutQuad = fun(t: Float): Float {
        return -t * (t - 2)
    }

    val InOutQuad = fun(t: Float): Float {
        if (t < 0.5f) {
            return 2 * t * t
        } else {
            val t1 = 2 * t - 1
            return -0.5f * (t1 * (t1 - 2) - 1)
        }
    }

    val InCubic = fun(t: Float): Float {
        return t * t * t
    }

    val OutCubic = fun(t: Float): Float {
        val t1 = t - 1
        return t1 * t1 * t1 + 1
    }

    val InOutCubic = fun(t: Float): Float {
        var t1 = t * 2
        if (t1 < 1) {
            return 0.5f * t1 * t1 * t1
        } else {
            t1 -= 2
            return 0.5f * (t1 * t1 * t1 + 2)
        }
    }

    val InQuart = fun(t: Float): Float {
        return t * t * t * t
    }

    val OutQuart = fun(t: Float): Float {
        var t1 = t - 1
        return -(t1 * t1 * t1 * t1 - 1)
    }

    val InOutQuart = fun(t: Float): Float {
        var t1 = t * 2
        if (t1 < 1) {
            return 0.5f * t1 * t1 * t1 * t1
        } else {
            t1 -= 2
            return -0.5f * (t1 * t1 * t1 * t1 - 2)
        }
    }

    val InQuint = fun(t: Float): Float {
        return t * t * t * t * t
    }

    val OutQuint = fun(t: Float): Float {
        val t1 = t - 1
        return t1 * t1 * t1 * t1 * t1 + 1
    }

    val InOutQuint = fun(t: Float): Float {
        var t1 = t * 2
        if (t1 < 1) {
            return 0.5f * t1 * t1 * t1 * t1 * t1
        } else {
            t1 -= 2
            return 0.5f * (t1 * t1 * t1 * t1 * t1 + 2)
        }
    }

    val InSine = fun(t: Float): Float {
        return -1 * Math.cos(t.toDouble() * Math.PI / 2).toFloat() + 1
    }

    val OutSine = fun(t: Float): Float {
        return Math.sin(t.toDouble() * Math.PI / 2).toFloat()
    }

    val InOutSine = fun(t: Float): Float {
        return -0.5f * (Math.cos(Math.PI * t.toDouble()).toFloat() - 1)
    }

    val InExpo = fun(t: Float): Float {
        if (t == 0f) {
            return 0f
        } else {
            return Math.pow(2.0, (10 * (t - 1)).toDouble()).toFloat()
        }
    }

    val OutExpo = fun(t: Float): Float {
        if (t == 1f) {
            return 1f
        } else {
            return 1 - Math.pow(2.0, (-10 * t).toDouble()).toFloat()
        }
    }

    val InOutExpo = fun(t: Float): Float {
        if (t == 0f) {
            return 0f
        } else if (t == 1f) {
            return 1f
        } else {
            if (t < 0.5f) {
                return (0.5f * Math.pow(2.0, ((20 * t) - 10).toDouble())).toFloat()
            } else {
                return (1 - 0.5f * Math.pow(2.0, ((-20 * t) + 10).toDouble())).toFloat()
            }
        }
    }

    val InCirc = fun(t: Float): Float {
        return -1 * (Math.sqrt((1 - t * t).toDouble()).toFloat() - 1)
    }

    val OutCirc = fun(t: Float): Float {
        var t1 = t - 1
        return Math.sqrt(1 - (t1 * t1).toDouble()).toFloat()
    }

    val InOutCirc = fun(t: Float): Float {
        var t1 = t * 2
        if (t1 < 1) {
            return -0.5f * (Math.sqrt((1 - t1 * t1).toDouble()).toFloat() - 1)
        } else {
            t1 -= 2
            return (0.5f * (Math.sqrt((1 - t1 * t1).toDouble()).toFloat() + 1))
        }
    }

    val InElastic = fun(t: Float): Float {
        return InElasticfuntion(0.5f)(t)
    }

    val OutElastic = fun(t: Float): Float {
        return OutElasticfuntion(0.5f, 1f)(t)
    }

    val OutHalfElastic = fun(t: Float): Float {
        return OutElasticfuntion(0.5f, 0.5f)(t)
    }

    val OutQuartElastic = fun(t: Float): Float {
        return OutElasticfuntion(0.5f, 0.25f)(t)
    }

    val InOutElastic = fun(t: Float): Float {
        return InOutElasticfuntion(0.5f)(t)
    }

    val InElasticfuntion = fun(period: Float): (Float) -> Float {
        var p = period
        return fun(t: Float): Float {
            var t1 = t - 1
            return -1 * (Math.pow(
                2.0,
                (10 * t1).toDouble()
            ).toFloat() * Math.sin((t1 - p / 4) * (2 * Math.PI) / p).toFloat())
        }
    }

    val OutElasticfuntion = fun(period: Float, mod: Float): (Float) -> Float {
        var p = period
        return fun(t: Float): Float {
            return Math.pow(
                2.0,
                (-10 * t).toDouble()
            ).toFloat() * Math.sin((mod * t - p / 4) * (2 * Math.PI / p)).toFloat() + 1
        }
    }

    val InOutElasticfuntion = fun(period: Float): (Float) -> Float {
        var p = period
        return fun(t: Float): Float {
            var t1 = t * 2
            if (t1 < 1f) {
                t1 -= 1
                return (-0.5f * (Math.pow(
                    2.0,
                    (10 * t1).toDouble()
                ) * Math.sin((t1 - p / 4) * 2 * Math.PI / p))).toFloat()
            } else {
                t1 -= 1
                return (Math.pow(
                    2.0,
                    (-10 * t1).toDouble()
                ) * Math.sin((t1 - p / 4) * 2 * Math.PI / p) * 0.5f + 1).toFloat()
            }
        }
    }

    val InBack = fun(t: Float): Float {
        var s = 1.70158f
        return t * t * ((s + 1) * t - s)
    }

    val OutBack = fun(t: Float): Float {
        var s = 1.70158f
        var t1 = t - 1
        return t1 * t1 * ((s + 1) * t1 + s) + 1
    }

    val InOutBack = fun(t: Float): Float {
        var s = 1.70158f
        var t1 = t * 22
        if (t1 < 1) {
            s *= 1.525f
            return 0.5f * (t1 * t1 * ((s + 1) * t1 - s))
        } else {
            t1 -= 2
            s *= 1.525f
            return 0.5f * (t1 * t1 * ((s + 1) * t1 + s) + 2)
        }
    }

    val InBounce = fun(t: Float): Float {
        return 1 - OutBounce(1 - t)
    }

    val OutBounce = fun(t: Float): Float {
        if (t < 4 / 11.0f) {
            return (121 * t * t) / 16.0f
        } else if (t < 8 / 11.0f) {
            return (363 / 40.0f * t * t) - (99 / 10.0f * t) + 17 / 5.0f
        } else if (t < 9 / 10.0f) {
            return (4356 / 361.0f * t * t) - (35442 / 1805.0f * t) + 16061 / 1805.0f
        } else {
            return (54 / 5.0f * t * t) - (513 / 25.0f * t) + 268 / 25.0f
        }
    }

    val InOutBounce = fun(t: Float): Float {
        if (t < 0.5f) {
            return InBounce(2 * t) * 0.5f
        } else {
            return OutBounce(2 * t - 1) * 0.5f + 0.5f
        }
    }

    val InSquare = fun(t: Float): Float {
        if (t < 1) {
            return 0f
        } else {
            return 1f
        }
    }

    val OutSquare = fun(t: Float): Float {
        if (t > 0) {
            return 1f
        } else {
            return 0f
        }
    }

    val InOutSquare = fun(t: Float): Float {
        if (t < 0.5f) {
            return 0f
        } else {
            return 1f
        }
    }

}