package me.wieku.danser

import me.wieku.danser.build.Build
import me.wieku.framework.configuration.FrameworkConfig
import me.wieku.framework.backend.DesktopContext

import java.io.File

fun main() {
    //If we have renderdoc library in user directory, load it
    if (File("renderdoc.dll").exists()) {
        System.load(System.getProperty("user.dir")+"/renderdoc.dll")
    }

    println("Version " + Build.Version)

    FrameworkConfig.windowTitle.value = "danser " + Build.Version

    val context = DesktopContext()
    context.start(Danser())

}
