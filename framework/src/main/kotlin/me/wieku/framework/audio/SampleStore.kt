package me.wieku.framework.audio

import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.ResourceStore

class SampleStore: ResourceStore<Sample>() {
    override val resourceBasePath: String = "assets/sounds/"

    override fun loadResource(file: FileHandle): Sample {
        return Sample(file)
    }
}