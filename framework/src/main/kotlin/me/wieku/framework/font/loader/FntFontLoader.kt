package me.wieku.framework.font.loader

import me.wieku.framework.font.BitmapFont
import me.wieku.framework.font.Glyph
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.resource.FileHandle
import me.wieku.framework.utils.CPair
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.regex.Pattern
import kotlin.math.abs

internal class FntFontLoader : IFontLoader {
    override fun loadFont(font: BitmapFont, file: FileHandle) {
        val reader = BufferedReader(InputStreamReader(file.inputStream()))

        val pattern = Pattern.compile("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*\$)|[=]")

        var baseline = 0
        var width = 0
        var height = 0

        while (true) {
            val line: String = reader.readLine() ?: break

            if (line.isBlank()) continue

            val splitted = pattern.split(line)

            when (splitted[0]) {
                "info" -> {
                    for (i in 1 until splitted.size - 1 step 2) {
                        val key = splitted[i]
                        val value = splitted[i + 1]
                        when (key) {
                            "face" -> font.name = value.replace("\"", "")
                            "size" -> font.defaultSize = abs(value.toInt())
                            "padding" -> {
                                val padding = value.split(",")
                                font.padTop = padding[0].toInt()
                                font.padRight = padding[1].toInt()
                                font.padBottom = padding[2].toInt()
                                font.padLeft = padding[3].toInt()
                            }
                        }
                    }
                }
                "common" -> {
                    for (i in 1 until splitted.size - 1 step 2) {
                        val key = splitted[i]
                        val value = splitted[i + 1].toInt()
                        when (key) {
                            "base" -> baseline = value
                            "scaleW" -> width = value
                            "scaleH" -> height = value
                        }
                    }
                }
                "page" -> {
                    var page = 0
                    var name = ""
                    for (i in 1 until splitted.size - 1 step 2) {
                        val key = splitted[i]
                        val value = splitted[i + 1]
                        when (key) {
                            "id" -> page = value.toInt()
                            "file" -> name = value.replace("\"", "")
                        }
                    }
                    font.pages[page] = Texture(FileHandle(file.file.parent + File.separator + name, file.fileType), 4)
                }
                "char" -> {
                    val glyph = Glyph()
                    for (i in 1 until splitted.size - 1 step 2) {
                        val key = splitted[i]
                        val value = splitted[i + 1].toInt()
                        when (key) {
                            "id" -> glyph.index = value
                            "x" -> glyph.x = value
                            "y" -> glyph.y = value
                            "width" -> glyph.width = value
                            "height" -> glyph.height = value
                            "xoffset" -> glyph.xOffset = value
                            "yoffset" -> glyph.yOffset = -(baseline - glyph.height - value)
                            "xadvance" -> glyph.xAdvance = value
                            "page" -> glyph.page = value
                        }
                    }

                    glyph.u1 = glyph.x.toFloat() / width
                    glyph.u2 = (glyph.x + glyph.width).toFloat() / width
                    glyph.v1 = glyph.y.toFloat() / height
                    glyph.v2 = (glyph.y + glyph.height).toFloat() / height

                    font.glyphs[glyph.index] = glyph
                }
                "kerning" -> {
                    var first = 0
                    var second = 0
                    var amount = 0
                    for (i in 1 until splitted.size - 1 step 2) {
                        val key = splitted[i]
                        val value = splitted[i + 1].toInt()
                        when (key) {
                            "first" -> first = value
                            "second" -> second = value
                            "amount" -> amount = value
                        }
                    }

                    font.kerningTable[CPair(first, second)] = amount
                }
            }

        }

        reader.close()
    }
}