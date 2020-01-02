package me.wieku.framework.math

import org.joml.Vector2f

enum class Scaling {
    /** Scales the source to fit the target while keeping the same aspect ratio. This may cause the source to be smaller than the
     * target in one direction.  */
    Fit,
    /** Scales the source to fill the target while keeping the same aspect ratio. This may cause the source to be larger than the
     * target in one direction.  */
    Fill,
    /** Scales the source to fill the target in the x direction while keeping the same aspect ratio. This may cause the source to be
     * smaller or larger than the target in the y direction.  */
    FillX,
    /** Scales the source to fill the target in the y direction while keeping the same aspect ratio. This may cause the source to be
     * smaller or larger than the target in the x direction.  */
    FillY,
    /** Scales the source to fill the target. This may cause the source to not keep the same aspect ratio.  */
    Stretch,
    /** Scales the source to fill the target in the x direction, without changing the y direction. This may cause the source to not
     * keep the same aspect ratio.  */
    StretchX,
    /** Scales the source to fill the target in the y direction, without changing the x direction. This may cause the source to not
     * keep the same aspect ratio.  */
    StretchY,
    /** The source is not scaled.  */
    None;

    /** Returns the size of the source scaled to the target. Note the same Vector2 instance is always returned and should never be
     * cached.  */
    fun apply(sourceX: Float, sourceY: Float, targetX: Float, targetY: Float): Vector2f {
        when (this) {
            Fit, Fill -> {
                val targetRatio = targetY / targetX
                val sourceRatio = sourceY / sourceX
                val scale = if (this == Fit && targetRatio > sourceRatio || this == Fill && targetRatio < sourceRatio) targetX / sourceX else targetY / sourceY
                temp.x = sourceX * scale
                temp.y = sourceY * scale
            }
            FillX -> {
                val scale = targetX / sourceX
                temp.x = sourceX * scale
                temp.y = sourceY * scale
            }
            FillY -> {
                val scale = targetY / sourceY
                temp.x = sourceX * scale
                temp.y = sourceY * scale
            }
            Stretch -> {
                temp.x = targetX
                temp.y = targetY
            }
            StretchX -> {
                temp.x = targetX
                temp.y = sourceY
            }
            StretchY -> {
                temp.x = sourceX
                temp.y = targetY
            }
            None -> {
                temp.x = sourceX
                temp.y = sourceY
            }
        }
        return temp
    }

    companion object {

        private val temp = Vector2f()
    }
}