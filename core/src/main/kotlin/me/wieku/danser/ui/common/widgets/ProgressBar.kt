package me.wieku.danser.ui.common.widgets

import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.graphics.drawables.containers.ColorContainer
import me.wieku.framework.graphics.drawables.containers.RoundedEdgeContainer
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import me.wieku.framework.math.color.Color
import org.joml.Vector2f

class ProgressBar() : RoundedEdgeContainer() {

    private var progressContainer: RoundedEdgeContainer

    constructor(inContext: ProgressBar.() -> Unit) : this() {
        inContext()
    }

    var progressSpeed: Double = 100.0

    var progress: Float = 0f
        set(value) {
            progressContainer.addTransform(
                Transform(
                    TransformType.ScaleVector,
                    clock.currentTime,
                    clock.currentTime + progressSpeed,
                    progressContainer.scale,
                    Vector2f(value, 1f)
                )
            )

            field = value
        }

    init {
        radius = 1f
        addChild(
            ColorContainer {
                color = Color(0.2f, 0.6f)
                fillMode = Scaling.Stretch
            },
            RoundedEdgeContainer {
                fillMode = Scaling.Stretch
                scale = Vector2f(0f, 1f)
                anchor = Origin.CentreLeft
                origin = Origin.CentreLeft

                addChild(
                    ColorContainer {
                        color = Color(1f)
                        fillMode = Scaling.Stretch
                    }
                )
            }.also { progressContainer = it }
        )
    }

    override fun update() {
        progressContainer.radius = radius
        super.update()
    }

}