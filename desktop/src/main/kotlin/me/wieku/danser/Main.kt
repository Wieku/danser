package me.wieku.danser

import me.wieku.danser.beatmap.*
import me.wieku.danser.build.Build
import me.wieku.danser.graphics.drawables.DanserCoin
import me.wieku.framework.audio.BassSystem
import me.wieku.framework.audio.Track
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.graphics.buffers.Framebuffer
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.SpriteBatch
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import me.wieku.framework.utils.FpsLimiter
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import java.io.File
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    println("Version " + Build.Version)

    glfwInit()
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, 1)
    var handle = glfwCreateWindow(800, 800, "testdanser: " + Build.Version, 0, 0)
    glfwMakeContextCurrent(handle)
    glfwSwapInterval(0)
    GL.createCapabilities()

    var offt = FloatArray(200)
    var batch = SpriteBatch()

    val color = Vector4f(0.2f, 0.2f, 0.5f, 0.6f)

    var texture = Texture(1, 1, data = intArrayOf(0xffffffff.toInt()))

    BassSystem.initSystem()

    BeatmapManager.loadBeatmaps(System.getenv("localappdata") + "\\osu!\\Songs")

    var beatmap = BeatmapManager.beatmapSets.filter { it.metadata!!.title.contains("redfoo", true) }[0].beatmaps[0]

    beatmap.loadTrack()

    beatmap.getTrack().play(0.1f)
    val bindable = Bindable(beatmap)
    val coin = DanserCoin(bindable, batch)

    var power = 0f
    var time = 0f

    val fbf = Framebuffer(800, 800)
    var fsprite = Sprite(fbf.getTexture()!!.region, 2f, 2f)

    Thread {
        while (!glfwWindowShouldClose(handle)) {
            beatmap.getTrack().update()
            for (i in 0 until offt.size) {
                offt[i] = Math.max(beatmap.getTrack().fftData[i], offt[i] - 0.001f * 16)
            }

            power = Math.max(beatmap.getTrack().beat, power - 0.001f * 16)

            fsprite.rotation -= 0.0005f * 16


            if (fsprite.rotation <= -2 * Math.PI) {
                fsprite.rotation += 2 * Math.PI.toFloat()
            }

            //sprite.update(time)
            fsprite.update(time)

            time += 16f
            Thread.sleep(16)
        }
    }.start()

    val limiter = FpsLimiter(240)

    while (!glfwWindowShouldClose(handle)) {

        GL11.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT)
        fbf.bind()
        coin.draw()
        batch.begin()

        //sprite.scale.set(1f + power)

        //batch.draw(sprite)

        for (i in 0 until offt.size) {
            batch.draw(
                texture,
                ((i.toFloat() + 0.5f) * 2) / offt.size - 1,
                offt[i] - 1,
                2f / offt.size,
                offt[i] * 2,
                color
            )
        }

        batch.end()

        fbf.unbind()

        batch.begin()
        batch.draw(fsprite)
        batch.end()

        glfwPollEvents()
        glfwSwapBuffers(handle)
        limiter.sync()
    }

    glfwDestroyWindow(handle)
    exitProcess(0)
}
