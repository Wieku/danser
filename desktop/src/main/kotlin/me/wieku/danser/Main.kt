package me.wieku.danser

import me.wieku.danser.beatmap.*
import me.wieku.danser.build.Build
import me.wieku.danser.graphics.drawables.DanserCoin
import me.wieku.danser.graphics.drawables.Visualizer
import me.wieku.framework.audio.BassSystem
import me.wieku.framework.audio.Track
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.graphics.buffers.Framebuffer
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.math.Origin
import me.wieku.framework.math.vector2fRad
import me.wieku.framework.math.view.Camera
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
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
import java.awt.Frame
import java.io.File
import kotlin.math.pow
import kotlin.system.exitProcess

val bindable = Bindable<Beatmap>()

val danserModule = module {

}

fun main(args: Array<String>) {
    println("Version " + Build.Version)

    val koin = startKoin { modules(danserModule) }

    glfwInit()
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, 1)
    var handle = glfwCreateWindow(800, 800, "testdanser: " + Build.Version, 0, 0)
    glfwMakeContextCurrent(handle)
    glfwSwapInterval(0)
    GL.createCapabilities()


    var batch = SpriteBatch()

    val color = Vector4f(1f, 1f, 1f, 0.6f)

    BassSystem.initSystem()

    BeatmapManager.loadBeatmaps(System.getenv("localappdata") + "\\osu!\\Songs")

    var beatmap = BeatmapManager.beatmapSets.filter { /*it.metadata!!.title.contains("Windfall", true) &&*/ it.beatmaps.filter { bmap -> bmap.beatmapInfo.version == "Intense Ecstasy" }.isNotEmpty() }[0].beatmaps.filter { bmap -> bmap.beatmapInfo.version == "Intense Ecstasy" }[0]

    bindable.value = beatmap

    danserModule.single { bindable }
    loadKoinModules(danserModule)

    beatmap.loadTrack()

    beatmap.getTrack().play(0.1f)
    beatmap.getTrack().setPosition(beatmap.beatmapMetadata.previewTime.toFloat()/1000)
    val bindable = Bindable(beatmap)
    val coin = DanserCoin()
    val vis = Visualizer()

    var power = 0f
    var time = 0f
    var sectime = 0f

    var fbf = Framebuffer(800, 800)
    var fsprite = Sprite {
        this.texture = fbf.getTexture()!!.region
        size = Vector2f(800f, 800f)
        position = Vector2f(400f, 400f)
    }/*{
        texture = fbf.getTexture()!!.region,

    }*/

    var wWidth = 800
    var wHeight = 800

    var camera = Camera()
    camera.setViewportF(0, 0, 800, 800, true)
    camera.update()

    glfwSetWindowSizeCallback(handle) { h, width, height ->
        //println("test")
        fbf.dispose()
        wWidth = width
        wHeight = height
        camera.setViewportF(0, 0, wWidth, wHeight, true)
        camera.update()
        fbf = Framebuffer(width, height)
        fsprite.texture = fbf.getTexture()!!.region
        fsprite.position = Vector2f(width.toFloat()/2, height.toFloat()/2)
        fsprite.invalidate()
        fsprite.update()

        vis.position = Vector2f(width.toFloat()/2, height.toFloat()/2)
        vis.invalidate()
        vis.update()

        coin.position = Vector2f(width.toFloat()/2, height.toFloat()/2)
        coin.invalidate()
        coin.update()
        //println("test1")
    }




    Thread {
        while (!glfwWindowShouldClose(handle)) {
            beatmap.getTrack().update()
            Thread.sleep(16)
        }
    }.start()

    val limiter = FpsLimiter(240)

    while (!glfwWindowShouldClose(handle)) {
        glViewport(0, 0, wWidth, wHeight)
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT)
        //fbf.bind()

        batch.camera = camera
        batch.begin()

        vis.update()
        vis.draw(batch)

        coin.update()
        coin.draw(batch)

        batch.end()
        //fbf.unbind()

        //batch.begin()
        //batch.draw(fsprite)
        //batch.end()

        glfwPollEvents()
        glfwSwapBuffers(handle)
        limiter.sync()
    }

    glfwDestroyWindow(handle)
    exitProcess(0)
}
