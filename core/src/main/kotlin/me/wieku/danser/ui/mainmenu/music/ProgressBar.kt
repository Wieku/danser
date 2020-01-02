package me.wieku.danser.ui.mainmenu.music

import me.wieku.danser.beatmap.Beatmap
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.graphics.drawables.containers.ColorContainer
import me.wieku.framework.graphics.drawables.containers.YogaContainer
import me.wieku.framework.input.event.ClickEvent
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import org.joml.Vector2f
import org.joml.Vector4f
import org.koin.core.KoinComponent
import org.koin.core.inject

class ProgressBar: YogaContainer(), KoinComponent {

    private val beatmapBindable: Bindable<Beatmap?> by inject()

    private val progress: ColorContainer

    init {

        yogaSizePercent = Vector2f(100f, 20f)

        addChild(
            ColorContainer {
                fillMode = Scaling.Stretch
                color = Vector4f(249f/255, 168f/255, 37f/255, 1f)
            },
            ColorContainer {
                fillMode = Scaling.Stretch
                anchor = Origin.TopLeft
                origin = Origin.TopLeft
                color = Vector4f(255f/255, 217f/255, 90f/255, 1f)
            }.also { progress = it }
        )

    }

    override fun update() {

        if (beatmapBindable.value != null) {
            val track = beatmapBindable.value!!.getTrack()
            progress.scale.x = track.getPosition()/track.getLength()
        } else {
            progress.scale.x = 0f
        }

        progress.invalidate()

        super.update()
    }

    override fun onClick(e: ClickEvent): Boolean {
        if (beatmapBindable.value != null) {
            val progress = (e.cursorPosition.x-drawPosition.x)/drawSize.x
            val track = beatmapBindable.value!!.getTrack()
            track.setPosition(progress * track.getLength())
        }
        return super.onClick(e)
    }

}