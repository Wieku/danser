package me.wieku.framework.configuration

import me.wieku.framework.resource.Parsable
import java.io.File
import java.util.*
import java.util.Enumeration
import kotlin.reflect.KClass

//We need to pass a class of an enum, because we can't retrieve its value using parameter only
abstract class Config<T>(private val enumClass: KClass<T>) where T: Enum<T> {

    abstract val configFile: String

    protected val parsableMap = HashMap<T, Parsable>()

    fun addProperty(name: T, parsable: Parsable) {
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
        val properties = object: Properties() {
            override fun keys(): Enumeration<Any> {
                val keyList = Vector<Any>()
                keyList.addAll(super.keys().asSequence())
                keyList.sortWith(Comparator { o1, o2 ->
                    return@Comparator java.lang.Enum.valueOf(enumClass.java, o1 as String).compareTo(java.lang.Enum.valueOf(enumClass.java, o2 as String))
                })
                return keyList.elements()
            }
        }

        for (property in parsableMap) {
            properties.setProperty(property.key.toString(), property.value.parseToString())
        }

        properties.store(File(configFile).writer(), "rocket2d configuration file")
    }

}