package me.wieku.danser

import me.wieku.danser.build.Build
import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.audio.BassSystem
import me.wieku.framework.audio.Track
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

    println(glGetError())

    var offt = FloatArray(200)
    var batch = SpriteBatch()

    val color = Vector4f(0.2f, 0.2f, 0.5f, 0.6f)

    var texture = Texture(1, 1)
    texture.bind(1)
    texture.setData(0, 0, 1, 1, byteArrayOf(255.toByte(), 255.toByte(), 255.toByte(), 255.toByte()))

    val sprite = Sprite(Texture(FileHandle("assets/testimg.jpg", FileType.Classpath)).region, 1f, 1f, Vector2f(0.25f, 0.25f))
    sprite.position.set(-1f, -1f)
    sprite.addTransform(Transform(TransformType.Origin, 0f, 25000f, 0.25f, 0.25f, 0.5f, 0.5f, Easing.OutElastic))
    sprite.addTransform(Transform(TransformType.MoveX, 0f, 5000f, -1f, 1f, Easing.InOutCirc))
    sprite.addTransform(Transform(TransformType.MoveY, 5000f, 10000f, -1f, 1f, Easing.InOutCirc))
    sprite.addTransform(Transform(TransformType.MoveX, 10000f, 15000f, 1f, -1f, Easing.InOutCirc))
    sprite.addTransform(Transform(TransformType.MoveY, 15000f, 20000f, 1f, -1f, Easing.InOutCirc))
    sprite.addTransform(Transform(TransformType.Move, 20000f, 25000f, -1f, -1f, 0f, 0f, Easing.OutElastic))
    BassSystem.initSystem()
    val track = Track(FileHandle("assets/audio.mp3", FileType.Classpath))
    track.play(0.1f)

    var power = 0f
    var time = 0f

    Thread {
        while (!glfwWindowShouldClose(handle)) {
            track.update()
            for (i in 0 until offt.size) {
                offt[i] = Math.max(track.fftData[i], offt[i] - 0.001f * 16)
            }

            power = Math.max(track.beat, power - 0.001f * 16)

            sprite.rotation -= 0.0005f * 16

            if (sprite.rotation <= -2*Math.PI) {
                sprite.rotation += 2*Math.PI.toFloat()
            }

            sprite.update(time)

            time += 16f
            Thread.sleep(16)
        }
    }.start()

    val limiter = FpsLimiter(240)

    while (!glfwWindowShouldClose(handle)) {

        GL11.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT)

        batch.begin()

        sprite.scale.set(1f+power)

        batch.draw(sprite)

        for (i in 0 until offt.size) {
            batch.draw(texture, ((i.toFloat()+0.5f) * 2) / offt.size - 1, offt[i]-1, 2f/offt.size, offt[i]*2, color)
        }

        batch.end()

        glfwPollEvents()
        glfwSwapBuffers(handle)
        limiter.sync()
    }

    glfwDestroyWindow(handle)

}