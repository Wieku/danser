package me.wieku.framework.graphics.drawables.sprite

import me.wieku.framework.font.BitmapFont
import me.wieku.framework.font.FontStore
import me.wieku.framework.graphics.drawables.containers.ColorContainer
import me.wieku.framework.graphics.textures.Texture
import me.wieku.framework.graphics.textures.TextureFormat
import me.wieku.framework.graphics.textures.TextureRegion
import me.wieku.framework.math.Origin
import me.wieku.framework.utils.CPair
import org.joml.Vector2f
import org.joml.Vector4f
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.regex.Pattern
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max


open class TextSprite() : Sprite(), KoinComponent {

    lateinit var font: BitmapFont
    private val fontStore: FontStore by inject()

    constructor(font: BitmapFont, inContext: TextSprite.() -> Unit) : this() {
        this.font = font
        inContext()
    }

    constructor(font: String, inContext: TextSprite.() -> Unit) : this() {
        this.font = fontStore.getResource(font)
        inContext()
    }

    private val internalSprite = Sprite()
    private var internalTextureRegion: TextureRegion? = null

    var scaleToSize = false

    var drawDigitsMonospace = false

    var fontSize = 64f
        get() = if (scaleToSize) drawSize.y else field
        set(value) {
            field = value
            invalidate()
        }

    private val fontScale: Float
        get() = fontSize / font.defaultSize

    var drawFromBottom = false

    var drawShadow = false
    var shadowOffset = Vector2f(0f)
    private var shadowDrawOffset = Vector2f(0f)
    var shadowColor: Vector4f = Vector4f(0f, 0f, 0f, 0.2f)

    override fun dispose() {}

    override fun updateDrawable() {
        size.set(font.getTextWidth(text, drawDigitsMonospace).toFloat(), font.ascent.toFloat()+font.descent.toFloat())
        if (!scaleToSize) {
            size.mul(fontScale)
        }
        super.updateDrawable()
    }

    var text = ""
        set(value) {
            invalidate()
            field = value
        }

    override fun draw(batch: SpriteBatch) {
        var offset = if (text.isNotEmpty()) -Character.charCount(text.codePointAt(0)) else 0
        var advance = drawOrigin.x - (drawOrigin.x / drawSize.x * font.getTextWidth(text, drawDigitsMonospace).toFloat() * fontScale).toInt()
        var codepointBefore = if (text.isNotEmpty()) text.codePointAt(0) else 0
        while (true) {
            offset += Character.charCount(codepointBefore)
            if (offset >= text.length) break
            val codepoint = text.codePointAt(offset)

            if (offset > 0 && !(drawDigitsMonospace && (Character.isDigit(codepointBefore) || Character.isDigit(codepoint)))) {
                advance += font.kerningTable[CPair(codepointBefore, codepoint)] ?: 0
            }

            val glyph = font.glyphs[codepoint] ?: continue
            internalSprite.drawPosition.set(
                drawPosition.x + (advance + glyph.xOffset) * fontScale,
                drawPosition.y + (font.ascent + (if (drawFromBottom) font.descent else 0) - glyph.height + glyph.yOffset) * fontScale
            )
            internalSprite.drawSize.set(glyph.width.toFloat(), glyph.height.toFloat()).mul(fontScale)

            internalSprite.drawOrigin.set(internalSprite.drawSize).mul(1/2f)

            if (internalTextureRegion == null) {
                internalTextureRegion =
                    TextureRegion(font.pages!!, glyph.u1, glyph.u2, glyph.v1, glyph.v2, glyph.page)
            } else {
                internalTextureRegion!!.baseTexture = font.pages!!
                internalTextureRegion!!.U1 = glyph.u1
                internalTextureRegion!!.U2 = glyph.u2
                internalTextureRegion!!.V1 = glyph.v1
                internalTextureRegion!!.V2 = glyph.v2
                internalTextureRegion!!.layer = glyph.page
            }

            internalSprite.texture = internalTextureRegion
            internalSprite.shearX = shearX
            internalSprite.shearY = shearY
            internalSprite.rotation = rotation

            if (drawShadow) {
                shadowDrawOffset.set(shadowOffset).mul(fontSize)
                internalSprite.drawPosition.add(shadowDrawOffset)
                internalSprite.drawOrigin.sub(shadowDrawOffset)
                internalSprite.drawColor.set(shadowColor)
                internalSprite.drawColor.w *= drawColor.w
                batch.draw(internalSprite)
                internalSprite.drawPosition.sub(shadowDrawOffset)
                internalSprite.drawOrigin.add(shadowDrawOffset)
            }

            internalSprite.drawColor.set(drawColor)
            batch.draw(internalSprite)

            advance += if (!drawDigitsMonospace || !Character.isDigit(codepoint)) glyph.xAdvance else font.monoXAdvance

            codepointBefore = codepoint
        }
    }

    /*private companion object {
        val punctuation = Pattern.compile("[\\p{Punct}\\p{IsPunctuation}\\s]")
        fun isPunctuationChar(codepoint: Int): Boolean {
            val toText = java.lang.String.valueOf(Character.toChars(codepoint))
            return punctuation.matcher(toText).matches()
        }
    }*/

}