package me.wieku.danser.ui.common

import me.wieku.framework.backend.Game
import me.wieku.framework.graphics.drawables.containers.YogaContainer
import me.wieku.framework.utils.FpsCounter
import org.lwjgl.util.yoga.Yoga

class FPSStatistics(private val game: Game) : YogaContainer() {

    private val format = "%dfps (%.2fms)"

    private val inputRow: StatisticsRow
    private val updateRow: StatisticsRow
    private val drawRow: StatisticsRow

    private val inputCounter = FpsCounter()
    private val updateCounter = FpsCounter()
    private val drawCounter = FpsCounter()

    private var deltaSum = 0f

    constructor(game: Game, inContext: FPSStatistics.() -> Unit) : this(game) {
        inContext()
    }

    init {
        isRoot = true
        yogaDirection = Yoga.YGDirectionLTR
        yogaFlexDirection = Yoga.YGFlexDirectionColumn
        yogaAlignItems = Yoga.YGAlignFlexEnd
        addChild(
            StatisticsRow {
                type = "Input"
            }.also { inputRow = it },
            StatisticsRow {
                type = "Update"
            }.also { updateRow = it },
            StatisticsRow {
                type = "Draw"
            }.also { drawRow = it }
        )
    }

    override fun update() {
        deltaSum += game.updateClock.time.frameTime

        if (deltaSum >= 50f) {
            inputCounter.putSample(game.inputClock.time.frameTime)
            inputRow.data = format.format(inputCounter.fps.toInt(), inputCounter.frameTime)

            updateCounter.putSample(game.updateClock.time.frameTime)
            updateRow.data = format.format(updateCounter.fps.toInt(), updateCounter.frameTime)

            drawCounter.putSample(game.graphicsClock.time.frameTime)
            drawRow.data = format.format(drawCounter.fps.toInt(), drawCounter.frameTime)

            invalidate()

            deltaSum -= 50f
        }

        super.update()
    }

}