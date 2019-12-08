package me.wieku.framework.graphics.drawables.sprite

import me.wieku.framework.font.BitmapFont
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.graphics.textures.TextureFormat
import me.wieku.framework.graphics.textures.TextureRegion
import me.wieku.framework.utils.CPair


open class TextSprite(var font: BitmapFont) : Sprite() {

    constructor(font: BitmapFont, inContext: TextSprite.() -> Unit) : this(font) {
        inContext()
    }

    private val internalSprite = Sprite()
    private var internalTextureRegion: TextureRegion? = null

    var fontSize = 64f
        set(value) {
            field = value
            size.set(font.getTextWidth(text).toFloat(), font.ascent.toFloat()).mul(fontScale)
            invalidate()
        }

    private val fontScale: Float
        get() = fontSize / font.defaultSize

    override fun dispose() {}

    var text = ""
        set(value) {
            size.set(font.getTextWidth(value).toFloat(), font.ascent.toFloat()).mul(fontScale)
            invalidate()
            field = value
        }

    override fun draw(batch: SpriteBatch) {
        var offset = if (text.isNotEmpty()) -Character.charCount(text.codePointAt(0)) else 0
        var advance = 0
        var codepointBefore = if (text.isNotEmpty()) text.codePointAt(0) else 0
        while (true) {
            offset += Character.charCount(codepointBefore)
            if (offset >= text.length) break
            val codepoint = text.codePointAt(offset)

            if (offset > 0) {
                advance += font.kerningTable[CPair(codepointBefore, codepoint)] ?: 0
            }

            val glyph = font.glyphs[codepoint] ?: continue
            internalSprite.drawPosition.set(
                drawPosition.x + (advance + glyph.xOffset) * fontScale,
                drawPosition.y + (font.ascent - glyph.height + glyph.yOffset) * fontScale
            )
            internalSprite.drawSize.set(glyph.width.toFloat(), glyph.height.toFloat()).mul(fontScale)

            if (internalTextureRegion == null) {
                internalTextureRegion =
                    TextureRegion(font.pages[glyph.page]!!, glyph.u1, glyph.u2, glyph.v1, glyph.v2, 0)
            } else {
                internalTextureRegion!!.baseTexture = font.pages[glyph.page]!!
                internalTextureRegion!!.U1 = glyph.u1
                internalTextureRegion!!.U2 = glyph.u2
                internalTextureRegion!!.V1 = glyph.v1
                internalTextureRegion!!.V2 = glyph.v2
            }

            internalSprite.texture = internalTextureRegion
            internalSprite.drawColor.set(drawColor)

            batch.draw(internalSprite)

            advance += glyph.xAdvance

            codepointBefore = codepoint
        }
    }

}