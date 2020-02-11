package me.wieku.framework.graphics.textures

class TextureRegion(
    var baseTexture: ITexture,
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

    override fun equals(other: Any?): Boolean {
        if (other !is TextureRegion) return false
        if (other === this) return true
        return U1 == other.U1 && U2 == other.U2 && V1 == other.V1 && V2 == other.V2 &&
                layer == other.layer && baseTexture == other.baseTexture
    }
}