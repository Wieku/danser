package me.wieku.framework.resource

import java.lang.IllegalArgumentException

abstract class ResourceStore<T> {

    open val resourceBasePath = "assets/"

    protected val resourceMap = HashMap<String, T>()

    fun getResource(name: String): T {
        return resourceMap[name] ?: throw IllegalArgumentException("Resource with name \"$name\" doesn't exist in registry")
    }

    fun getResourceOrLoad(name: String, location: FileType = FileType.Classpath): T {
        if (!resourceMap.containsKey(name)) {
            addResource(name, location)
        }
        return resourceMap[name] ?: throw IllegalArgumentException("Resource with name \"$name\" doesn't exist in registry")
    }

    fun addResource(name: String, location: FileType = FileType.Classpath) {
        resourceMap[name] = loadResource(FileHandle(resourceBasePath+name, location))
    }

    fun addResource(name: String, file: FileHandle) {
        resourceMap[name] = loadResource(file)
    }

    protected abstract fun loadResource(file: FileHandle): T

}