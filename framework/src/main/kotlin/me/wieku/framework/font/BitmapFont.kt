package me.wieku.framework.font

import me.wieku.framework.font.loader.FntFontLoader
import me.wieku.framework.font.loader.IFontLoader
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.utils.CPair
import java.lang.IllegalArgumentException
import kotlin.math.max

class BitmapFont(file: FileHandle) {

    var name = ""

    var defaultSize = 0

    var padTop = 0
    var padBottom = 0
    var padLeft = 0
    var padRight = 0

    var ascent = 0
    var descent = 0

    var pages = HashMap<Int, Texture>()

    var glyphs = HashMap<Int, Glyph>()

    var kerningTable = HashMap<CPair<Int, Int>, Int>()

    init {
        val loader: IFontLoader = when (file.file.extension) {
            "fnt" -> FntFontLoader()
            else -> throw IllegalArgumentException("Unsupported font file format")
        }

        loader.loadFont(this, file)

        ascent = glyphs.values.firstOrNull { Character.isUpperCase(it.index) }?.height ?: 0

        glyphs.values.forEach {
            if (ascent == 0) {
                ascent = max(ascent, it.height)
            }
            descent = max(descent, it.yOffset)
        }

    }

    fun getTextWidth(text: String): Int {
        var offset = if (text.isNotEmpty()) -Character.charCount(text.codePointAt(0)) else 0
        var advance = 0
        var codepointBefore = if (text.isNotEmpty()) text.codePointAt(0) else 0
        while (true) {
            offset += Character.charCount(codepointBefore)
            if (offset >= text.length) break
            val codepoint = text.codePointAt(offset)

            if (offset > 0) {
                advance += kerningTable[CPair(codepointBefore, codepoint)] ?: 0
            }

            val glyph = glyphs[codepoint] ?: continue

            advance += glyph.xAdvance

            codepointBefore = codepoint
        }
        return advance
    }

}