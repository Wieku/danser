package me.wieku.framework.graphics.textures

class TextureRegion(
    var baseTexture: Texture,
    var U1: Float,
    var U2: Float,
    var V1: Float,
    var V2: Float,
    var layer: Int
) {


    fun getWidth(): Float = (U2 - U1) * baseTexture.width

    fun getHeight(): Float = (V2 - V1) * baseTexture.height

    fun getArea(): Float = getWidth() * getHeight()

    fun getTexture() = baseTexture
}