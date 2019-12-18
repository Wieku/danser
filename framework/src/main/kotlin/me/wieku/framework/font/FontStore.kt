package me.wieku.framework.font

import me.wieku.framework.resource.FileHandle
import me.wieku.framework.resource.ResourceStore

class FontStore: ResourceStore<BitmapFont>() {
    override fun loadResource(file: FileHandle): BitmapFont {
        return BitmapFont(file)
    }
}