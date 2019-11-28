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

fun main(args: Array<String>) {
    System.load("C:\\Users\\Wieku\\Google Drive\\danser\\danser/renderdoc.dll")
    println("Version " + Build.Version)

    val koin = startKoin { modules(danserModule) }

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

    val color = Vector4f(1f, 1f, 1f, 0.6f)

    BassSystem.initSystem()

    BeatmapManager.loadBeatmaps(System.getenv("localappdata") + "\\osu!\\Songs")

    var beatmap = BeatmapManager.beatmapSets.filter { /*it.metadata!!.title.contains("Windfall", true) &&*/ it.beatmaps.filter { bmap -> bmap.beatmapInfo.version == "Intense Ecstasy" }.isNotEmpty() }[0].beatmaps.filter { bmap -> bmap.beatmapInfo.version == "Intense Ecstasy" }[0]

    bindable.value = beatmap

    danserModule.single { bindable }
    loadKoinModules(danserModule)

    beatmap.loadTrack()

    beatmap.getTrack().play(0.1f)
    beatmap.getTrack().setPosition(beatmap.beatmapMetadata.previewTime.toFloat()/1000-5)

    val flashes = SideFlashes()
        flashes.fillMode = Scaling.Stretch

    val coin = DanserCoin()
        coin.scale = Vector2f(0.6f)
        coin.fillMode = Scaling.Fit

    val vis = Visualizer()
        vis.fillMode = Scaling.Fit
        vis.scale = Vector2f(0.58f)



    var power = 0f
    var time = 0f
    var sectime = 0f

    var fbf = Framebuffer(1920, 1080)
    fbf.addRenderbuffer(FramebufferTarget.DEPTH)
    var fsprite = Sprite {
        this.texture = fbf.getTexture()!!.region
        size = Vector2f(1920f, 1080f)
        position = Vector2f(1920f/2, 1080f/2)
    }


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
    mainContainer.addChild(vis)
    mainContainer.addChild(coin)

    /*{
        texture = fbf.getTexture()!!.region,

    }*/

    /*var triangles = Triangles()
    triangles.size = Vector2f(800f, 800f)
    triangles.position = Vector2f(400f, 400f)
    triangles.invalidate()*/


    var wWidth = 1920
    var wHeight = 1080

    var camera = Camera()
    camera.setViewportF(0, 0, 1920, 1080, true)
    camera.update()

    glfwSetWindowSizeCallback(handle) { h, width, height ->
        //println("test")
        fbf.dispose()
        wWidth = width
        wHeight = height
        camera.setViewportF(0, 0, wWidth, wHeight, true)
        camera.update()
        fbf = Framebuffer(width, height)
        fbf.addRenderbuffer(FramebufferTarget.DEPTH)
        fsprite.texture = fbf.getTexture()!!.region
        fsprite.position = Vector2f(width.toFloat()/2, height.toFloat()/2)
        fsprite.invalidate()
        fsprite.update()

        mainContainer.size = Vector2f(width.toFloat(), height.toFloat())
        mainContainer.invalidate()
        mainContainer.update()

        /*vis.position = Vector2f(width.toFloat()/2, height.toFloat()/2)
        vis.invalidate()
        vis.update()

        coin.position = Vector2f(width.toFloat()/2, height.toFloat()/2)
        coin.invalidate()
        coin.update()*/

        /*triangles.position = Vector2f(width.toFloat()/2, height.toFloat()/2)
        triangles.size = Vector2f(width.toFloat(), height.toFloat())
        triangles.invalidate()
        triangles.update()*/

        //println("test1")
    }




    Thread {
        while (!glfwWindowShouldClose(handle)) {
            beatmap.getTrack().update()
            Thread.sleep(40)
        }
    }.start()

    val limiter = FpsLimiter(240)

    glEnable(GL_MULTISAMPLE)

    while (!glfwWindowShouldClose(handle)) {
        glViewport(0, 0, wWidth, wHeight)
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT)
        //fbf.bind()

        batch.camera = camera
        batch.begin()

        /*triangles.update()
        triangles.draw(batch)*/

        /*vis.update()
        vis.draw(batch)

        coin.update()
        coin.draw(batch)*/

        mainContainer.update()
        mainContainer.draw(batch)

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
