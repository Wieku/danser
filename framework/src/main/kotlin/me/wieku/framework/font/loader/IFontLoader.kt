package me.wieku.framework.font.loader

import me.wieku.framework.font.BitmapFont
import me.wieku.framework.resource.FileHandle

internal interface IFontLoader {
    fun loadFont(font: BitmapFont, file: FileHandle)
}