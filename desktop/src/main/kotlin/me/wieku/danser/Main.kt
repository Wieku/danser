package me.wieku.danser

import me.wieku.danser.build.Build
import me.wieku.framework.configuration.FrameworkConfig
import me.wieku.framework.backend.DesktopContext
import me.wieku.framework.graphics.pixmap.Pixmap
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.FileType

import java.io.File

fun main(args: Array<String>) {
    //If we have renderdoc library in user directory, load it
    if (args.contains("-renderdoc") && File("renderdoc.dll").exists()) {
        System.load(System.getProperty("user.dir")+"/renderdoc.dll")
    }

    FrameworkConfig.windowTitle.value = "danser " + Build.Version

    val context = DesktopContext()
    context.icon = Pixmap(FileHandle("assets/textures/menu/coinsmall.png", FileType.Classpath))

    context.start(Danser())
}
