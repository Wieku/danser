package me.wieku.framework.configuration

import me.wieku.framework.resource.Parsable
import org.ini4j.Wini
import java.io.File
import java.util.*
import kotlin.reflect.KClass

//We need to pass a class of an enum, because we can't retrieve its value using parameter only
abstract class Config<T>(private val enumClass: KClass<T>) where T: Enum<T> {

    abstract val configFile: String

    protected val parsableMap = TreeMap<T, Parsable>()
    protected val sections = HashMap<T, Any>()

    fun addProperty(name: T, parsable: Parsable) {
        parsableMap[name] = parsable
        sections[name] = "Main"
    }

    fun addProperty(section: Any, name: T, parsable: Parsable) {
        parsableMap[name] = parsable
        sections[name] = section
    }

    fun openConfig() {
        if (File(configFile).exists()) {
            val isV2 = File(configFile).bufferedReader().use { it.readLine() }.endsWith("v2")

            if (isV2) {
                val wini = Wini(File(configFile).bufferedReader())

                for (sectionName in wini.keys) {
                    val section = wini[sectionName] ?: continue

                    for(key in section.keys) {
                        val property = parsableMap[java.lang.Enum.valueOf(enumClass.java, key)] ?: continue
                        property.parseFrom(section[key]!!)
                    }
                }

            } else {
                val properties = Properties()
                properties.load(File(configFile).bufferedReader())

                for (property in parsableMap) {
                    if (properties.containsKey(property.key.toString())) {
                        property.value.parseFrom(properties.getProperty(property.key.toString()))
                    }
                }
            }
        }
    }

    fun saveConfig() {
        val wini = Wini()

        for (property in parsableMap) {
            wini.put(sections[property.key].toString(), property.key.toString(), property.value.parseToString())
        }

        wini.comment = "rocket2d configuration file v2\n"
        wini.comment += "#Last edited: ${Date()}"

        wini.store(File(configFile).writer())
    }

}