package me.wieku.danser

import me.wieku.danser.beatmap.*
import me.wieku.danser.build.Build
import me.wieku.danser.graphics.drawables.DanserCoin
import me.wieku.danser.graphics.drawables.SideFlashes
import me.wieku.danser.graphics.drawables.Triangles
import me.wieku.danser.graphics.drawables.Visualizer
import me.wieku.framework.audio.BassSystem
import me.wieku.framework.audio.Track
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.graphics.buffers.Framebuffer
import me.wieku.framework.graphics.buffers.FramebufferTarget
import me.wieku.framework.graphics.containers.Container
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import me.wieku.framework.math.vector2fRad
import me.wieku.framework.math.view.Camera
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import me.wieku.framework.time.FramedClock
import me.wieku.framework.time.IFramedClock
import me.wieku.framework.utils.FpsLimiter
import org.joml.Vector2f
import org.joml.Vector4f
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.GL_MULTISAMPLE
import java.awt.Frame
import java.io.File
import kotlin.math.pow
import kotlin.system.exitProcess

val bindable = Bindable<Beatmap>()

val danserModule = module {

}

fun main() {
    //If we have renderdoc library in user directory, load it
    if (File("renderdoc.dll").exists()) {
        System.load(System.getProperty("user.dir")+"/renderdoc.dll")
    }

    println("Version " + Build.Version)

    startKoin {}

    glfwInit()
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, 1)
    glfwWindowHint(GLFW_SAMPLES, 4)
    var handle = glfwCreateWindow(1920, 1080, "testdanser: " + Build.Version, 0, 0)
    glfwMakeContextCurrent(handle)
    glfwSwapInterval(0)
    GL.createCapabilities()


    var batch = SpriteBatch()

    BassSystem.initSystem()

    BeatmapManager.loadBeatmaps(System.getenv("localappdata") + "\\osu!\\Songs")

    var beatmap = BeatmapManager.beatmapSets.filter { /*it.metadata!!.title.contains("Windfall", true) &&*/ it.beatmaps.filter { bmap -> bmap.beatmapInfo.version == "Intense Ecstasy" }.isNotEmpty() }[0].beatmaps.filter { bmap -> bmap.beatmapInfo.version == "Intense Ecstasy" }[0]

    bindable.value = beatmap

    val clock = FramedClock()

    //Hack to avoid autoformatting removing "as IFramedClock"
    val iClock = clock as IFramedClock

    danserModule.single { bindable }
    danserModule.single { iClock }
    loadKoinModules(danserModule)

    beatmap.loadTrack()

    beatmap.getTrack().play(0.1f)
    beatmap.getTrack().setPosition(beatmap.beatmapMetadata.previewTime.toFloat()/1000-5)

    val flashes = SideFlashes()
        flashes.fillMode = Scaling.Stretch

    val coin = DanserCoin()
        coin.scale = Vector2f(0.6f)
        coin.fillMode = Scaling.Fit

    val mainContainer = Container {
        size = Vector2f(1920f, 1080f)
        origin = Origin.TopLeft
    }

    println(System.getenv("localappdata") + "/osu!/Songs/" + beatmap.beatmapSet.directory + File.separator + beatmap.beatmapMetadata.backgroundFile)

    val bgSprite = Sprite {
        texture = Texture(
            FileHandle(
                System.getenv("localappdata") + "/osu!/Songs/" + beatmap.beatmapSet.directory + File.separator + beatmap.beatmapMetadata.backgroundFile,
                FileType.Absolute
            ),
            4
        ).region
        size = Vector2f(texture!!.getWidth(), texture!!.getHeight())
        fillMode = Scaling.Fill
        this.color.w = 0.2f
        anchor = Origin.Centre
    }

    mainContainer.addChild(bgSprite)
    mainContainer.addChild(flashes)
    mainContainer.addChild(coin)


    var wWidth = 1920
    var wHeight = 1080

    var camera = Camera()
    camera.setViewportF(0, 0, 1920, 1080, true)
    camera.update()

    Thread {
        while (!glfwWindowShouldClose(handle)) {
            beatmap.getTrack().update()
            Thread.sleep(10)
        }
    }.start()

    val limiter = FpsLimiter(0)

    glEnable(GL_MULTISAMPLE)

    val draw: ()->Unit = {
        clock.updateClock()
        glViewport(0, 0, wWidth, wHeight)
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT)

        batch.camera = camera
        batch.begin()

        mainContainer.update()
        mainContainer.draw(batch)

        batch.end()

        glfwPollEvents()
        glfwSwapBuffers(handle)
        limiter.sync()
    }

    glfwSetWindowSizeCallback(handle) { h, width, height ->
        wWidth = width
        wHeight = height
        camera.setViewportF(0, 0, wWidth, wHeight, true)
        camera.update()

        mainContainer.size = Vector2f(width.toFloat(), height.toFloat())
        mainContainer.invalidate()
        mainContainer.update()

        draw()
    }

    while (!glfwWindowShouldClose(handle)) {
        draw()
    }

    glfwDestroyWindow(handle)
    exitProcess(0)
}
