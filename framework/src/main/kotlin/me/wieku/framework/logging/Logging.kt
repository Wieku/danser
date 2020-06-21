package me.wieku.framework.logging

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder

object Logging {

    private const val consolePattern = "%d{yyyy-MM-dd' 'HH:mm:ss.SSS} [%t] [%c] [%p]: %m%n"
    private const val filePattern = "%d{yyyy-MM-dd' 'HH:mm:ss.SSS} [%t] [%p]: %m%n"
    private const val logRoot = "./logs/"

    private val builder = ConfigurationBuilderFactory.newConfigurationBuilder()

    private val consoleLayout: LayoutComponentBuilder
    private val fileLayout: LayoutComponentBuilder

    private val loggers = ArrayList<String>()

    init {
        consoleLayout = builder.newLayout("PatternLayout")
        consoleLayout.addAttribute("pattern", consolePattern)
        consoleLayout.addAttribute("charset", "UTF-8")

        fileLayout = builder.newLayout("PatternLayout")
        fileLayout.addAttribute("pattern", filePattern)
        fileLayout.addAttribute("charset", "UTF-8")


        val consoleAppender = builder.newAppender("stdout", "Console")
        consoleAppender.add(consoleLayout)
        builder.add(consoleAppender)
    }

    fun getLogger(name: String): Logger {
        val capitalized = name.toLowerCase().capitalize()
        if (!loggers.contains(capitalized)) {
            createLogger(capitalized)
            loggers.add(capitalized)
        }

        return LogManager.getLogger(capitalized)!!
    }

    private fun createLogger(_name: String) {
        val name = _name.toLowerCase().capitalize()

        val file = builder.newAppender(name, "File")
        file.addAttribute("fileName", "${logRoot}${name.toLowerCase()}.log")
        file.addAttribute("append", "false")
        file.add(fileLayout)

        builder.add(file)

        val logger = builder.newLogger(name, Level.ALL)
        logger.add(builder.newAppenderRef("stdout"))
        logger.add(builder.newAppenderRef(name))
        logger.addAttribute("additivity", false)

        builder.add(logger)

        Configurator.reconfigure(builder.build())
    }

}