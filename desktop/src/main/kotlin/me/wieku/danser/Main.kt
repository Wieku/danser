package me.wieku.danser

import me.wieku.danser.beatmap.*
import me.wieku.danser.beatmap.parsing.BeatmapParser
import me.wieku.danser.build.Build
import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.audio.BassSystem
import me.wieku.framework.audio.Track
import me.wieku.framework.graphics.buffers.Framebuffer
import me.wieku.framework.graphics.sprite.Sprite
import me.wieku.framework.graphics.sprite.SpriteBatch
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.math.Easing
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType
import me.wieku.framework.utils.FpsLimiter
import org.joml.Vector2f
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import java.io.File
import kotlin.math.absoluteValue
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    println("Version " + Build.Version)

    glfwInit()
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, 1)
    var handle = glfwCreateWindow(600, 600, "testdanser: " + Build.Version, 0, 0)
    glfwMakeContextCurrent(handle)
    glfwSwapInterval(0)
    GL.createCapabilities()

    println(glGetError())

    var offt = FloatArray(200)
    var batch = SpriteBatch()

    val color = Vector4f(0.2f, 0.2f, 0.5f, 0.6f)

    var texture = Texture(1, 1, data = intArrayOf(0xffffffff.toInt()))

    val sprite =
        Sprite(Texture(FileHandle("assets/coinbig.png", FileType.Classpath)).region, 1f, 1f, Vector2f(0.25f, 0.25f))
    sprite.position.set(-1f, -1f)
    sprite.addTransform(Transform(TransformType.Origin, 0f, 25000f, 0.25f, 0.25f, 0.5f, 0.5f, Easing.OutElastic))
    sprite.addTransform(Transform(TransformType.MoveX, 0f, 5000f, -1f, 1f, Easing.InOutCirc))
    sprite.addTransform(Transform(TransformType.MoveY, 5000f, 10000f, -1f, 1f, Easing.InOutCirc))
    sprite.addTransform(Transform(TransformType.MoveX, 10000f, 15000f, 1f, -1f, Easing.InOutCirc))
    sprite.addTransform(Transform(TransformType.MoveY, 15000f, 20000f, 1f, -1f, Easing.InOutCirc))
    sprite.addTransform(Transform(TransformType.Move, 20000f, 25000f, -1f, -1f, 0f, 0f, Easing.OutElastic))
    BassSystem.initSystem()

    BeatmapManager.loadBeatmaps(System.getenv("localappdata")+"\\osu!\\Songs")

    var beatmap = BeatmapManager.beatmapSets.filter{it.metadata!!.title.contains("redfoo", true)}[0].beatmaps[0]



    val track = Track(
        FileHandle(
            System.getenv("localappdata")+"/osu!/Songs/"+beatmap.beatmapSet.directory + File.separator + beatmap.beatmapMetadata.audioFile,
            FileType.Absolute
        )
    )//Track(FileHandle("assets/audio.mp3", FileType.Classpath))
    track.play(0.1f)

    var power = 0f
    var time = 0f

    val fbf = Framebuffer(600, 600)
    var fsprite = Sprite(fbf.getTexture()!!.region, 2f, 2f)

    Thread {
        while (!glfwWindowShouldClose(handle)) {
            track.update()
            for (i in 0 until offt.size) {
                offt[i] = Math.max(track.fftData[i], offt[i] - 0.001f * 16)
            }

            power = Math.max(track.beat, power - 0.001f * 16)

            sprite.rotation -= 0.0005f * 16
            fsprite.rotation -= 0.0005f * 16

            fsprite.scale.set((Math.sin(Math.PI / 4) / Math.sin(Math.PI / 4 + sprite.rotation.absoluteValue.rem(Math.PI.toFloat() / 2))).toFloat())

            if (fsprite.rotation <= -2 * Math.PI) {
                sprite.rotation += 2 * Math.PI.toFloat()
                fsprite.rotation += 2 * Math.PI.toFloat()
            }

            sprite.update(time)
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

        batch.begin()

        sprite.scale.set(1f + power)

        batch.draw(sprite)

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
