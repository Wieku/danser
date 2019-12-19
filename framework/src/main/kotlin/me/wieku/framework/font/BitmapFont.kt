package me.wieku.framework.font

import me.wieku.framework.font.loader.FntFontLoader
import me.wieku.framework.font.loader.IFontLoader
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.graphics.textures.TextureAtlas
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.utils.CPair
import java.lang.IllegalArgumentException
import java.util.regex.Pattern
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

    var pages: /* = HashMap<Int, Texture>()*/TextureAtlas? = null

    var glyphs = HashMap<Int, Glyph>()

    var kerningTable = HashMap<CPair<Int, Int>, Int>()

    var monoXAdvance = 0

    init {
        val loader: IFontLoader = when (file.file.extension) {
            "fnt" -> FntFontLoader()
            else -> throw IllegalArgumentException("Unsupported font file format")
        }

        loader.loadFont(this, file)

        val chr = glyphs.values.firstOrNull { Character.isUpperCase(it.index)/*it.index == '('.toInt()*/ }

        if (chr != null) {
            ascent = chr.height - chr.yOffset
        }

        glyphs.values.forEach {
            if (chr == null) {
                ascent = max(ascent, it.height-max(it.yOffset, 0))
            }
            if (Character.isDigit(it.index)) {
                monoXAdvance = max(monoXAdvance, it.xAdvance)
            }
            descent = max(descent, it.yOffset)
        }

        //ascent = defaultSize - descent

    }

    fun getTextWidth(text: String, isMonospace: Boolean = false): Int {
        var offset = if (text.isNotEmpty()) -Character.charCount(text.codePointAt(0)) else 0
        var advance = 0
        var codepointBefore = if (text.isNotEmpty()) text.codePointAt(0) else 0
        while (true) {
            offset += Character.charCount(codepointBefore)
            if (offset >= text.length) break
            val codepoint = text.codePointAt(offset)

            if (offset > 0 && !isMonospace) {
                advance += kerningTable[CPair(codepointBefore, codepoint)] ?: 0
            }

            val glyph = glyphs[codepoint] ?: continue

            advance += if (!isMonospace || !Character.isDigit(codepoint)) glyph.xAdvance else max(monoXAdvance, glyph.xAdvance)

            codepointBefore = codepoint
        }
        return advance
    }

    /*private companion object {
        val punctuation = Pattern.compile("[\\p{Punct}\\p{IsPunctuation}\\s]")
        fun isPunctuationChar(codepoint: Int): Boolean {
            val toText = java.lang.String.valueOf(Character.toChars(codepoint))
            return punctuation.matcher(toText).matches()
        }
    }*/

}