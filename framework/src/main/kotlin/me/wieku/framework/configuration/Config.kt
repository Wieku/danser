package me.wieku.framework.configuration

import me.wieku.framework.resource.Parsable
import java.io.File
import java.util.*

abstract class Config {

    abstract val configFile: String

    protected val parsableMap = HashMap<Any, Parsable>()

    fun addProperty(name: Any, parsable: Parsable) {
        parsableMap[name] = parsable
    }

    fun openConfig() {
        if (File(configFile).exists()) {
            val properties = Properties()
            properties.load(File(configFile).bufferedReader())

            for (property in parsableMap) {
                if (properties.containsKey(property.key.toString())) {
                    property.value.parseFrom(properties.getProperty(property.key.toString()))
                }
            }

        }
    }

    fun saveConfig() {
        val properties = Properties()

        for (property in parsableMap) {
            properties.setProperty(property.key.toString(), property.value.parseToString())
        }

        properties.store(File(configFile).writer(), "rocket2d configuration file")
    }

}