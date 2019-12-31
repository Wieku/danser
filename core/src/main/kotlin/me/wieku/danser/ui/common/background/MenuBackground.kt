package me.wieku.danser.ui.common.background

import me.wieku.danser.beatmap.Beatmap
import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.di.bindable.BindableListener
import me.wieku.framework.graphics.drawables.containers.BlurredContainer
import me.wieku.framework.graphics.drawables.containers.ParallaxContainer
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import org.joml.Vector2f
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File

class MenuBackground(): ParallaxContainer(), KoinComponent {

    //TODO: texture unloading

    val defaultBackground: Sprite

    private lateinit var wrapper: BackgroundWrapper

    var fileTextureHandle: FileHandle? = null

    var oldBackground: Sprite? = null
    var currentBackground: Sprite? = null

    val beatmapBindable: Bindable<Beatmap?> by inject()

    constructor(inContext: MenuBackground.() -> Unit) : this() {
        inContext()
    }

    init {

        defaultBackground = Sprite("menu/backgrounds/background-1.png") {
            fillMode = Scaling.Fill
            anchor = Origin.Centre
        }

        currentBackground = defaultBackground

        addChild(
            BlurredContainer {
                fillMode = Scaling.Stretch
                blurAmount = 0.3f
                anchor = Origin.Custom
                customAnchor = Vector2f(0.5f, 0.5f)
                color.w = 0.9f
                addChild(
                    BackgroundWrapper().also { wrapper = it }
                )
            }
        )

        wrapper.addChild(defaultBackground)

        beatmapBindable.addListener(object : BindableListener<Beatmap?> {
            override fun valueChanged(bindable: Bindable<Beatmap?>) {
                val beatmap = bindable.value!!
                if (beatmap.beatmapInfo.version != "Anto & Nuvolina's Extra") {
                    oldBackground = currentBackground
                    oldBackground!!.addTransform(
                        Transform(
                            TransformType.Fade,
                            clock.currentTime,
                            clock.currentTime + 500f,
                            1f,
                            0f
                        )
                    )
                    oldBackground!!.drawForever = false

                    val handle = FileHandle(System.getenv("localappdata") + "/osu!/Songs/" + beatmap.beatmapSet.directory + File.separator + beatmap.beatmapMetadata.backgroundFile, FileType.Absolute)

                    if (handle.file.exists()) {
                        fileTextureHandle = handle
                        currentBackground = Sprite {
                            fillMode = Scaling.Fill
                            anchor = Origin.Centre
                        }
                        currentBackground!!.addTransform(
                            Transform(
                                TransformType.Fade,
                                clock.currentTime,
                                clock.currentTime + 500f,
                                0f,
                                1f
                            )
                        )
                        wrapper.addChild(currentBackground!!)
                    }
                }
            }
        })

    }

    override fun draw(batch: SpriteBatch) {
        if (currentBackground!!.texture == null && fileTextureHandle != null) {
            currentBackground!!.texture = Texture(fileTextureHandle!!).region
            currentBackground!!.size = Vector2f(currentBackground!!.texture!!.getWidth(), currentBackground!!.texture!!.getHeight())
        }
        super.draw(batch)
    }

}