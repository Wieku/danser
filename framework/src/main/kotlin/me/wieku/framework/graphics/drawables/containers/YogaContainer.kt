package me.wieku.framework.graphics.drawables.containers

import me.wieku.framework.graphics.drawables.Drawable
import org.lwjgl.util.yoga.Yoga.*

import me.wieku.framework.math.Origin
import me.wieku.framework.utils.synchronized
import org.joml.Vector2f
import org.joml.Vector4f
import org.lwjgl.util.yoga.YGNode
import org.lwjgl.util.yoga.YGValue
import kotlin.math.round

open class YogaContainer() : Container() {

    private val yogaNode = YGNodeNewWithConfig(config)
    //private val yogaNodeWrapped = YGNode.create(yogaNode)
    private val yogaChildrenCount
        get() = children.count { it is YogaContainer }

    var isRoot = false

    //region Yoga Variables

    var yogaDirection
        get() = YGNodeStyleGetDirection(yogaNode)
        set(value) {
            invalidate()
            YGNodeStyleSetDirection(yogaNode, value)
        }

    var yogaFlexDirection
        get() = YGNodeStyleGetFlexDirection(yogaNode)
        set(value) {
            invalidate()
            YGNodeStyleSetFlexDirection(yogaNode, value)
        }

    var yogaJustifyContent
        get() = YGNodeStyleGetJustifyContent(yogaNode)
        set(value) {
            invalidate()
            YGNodeStyleSetJustifyContent(yogaNode, value)
        }

    var yogaAlignContent
        get() = YGNodeStyleGetAlignContent(yogaNode)
        set(value) {
            invalidate()
            YGNodeStyleSetAlignContent(yogaNode, value)
        }

    var yogaAlignItems
        get() = YGNodeStyleGetAlignItems(yogaNode)
        set(value) {
            invalidate()
            YGNodeStyleSetAlignItems(yogaNode, value)
        }

    var yogaAlignSelf
        get() = YGNodeStyleGetAlignSelf(yogaNode)
        set(value) {
            invalidate()
            YGNodeStyleSetAlignSelf(yogaNode, value)
        }

    var yogaPositionType
        get() = YGNodeStyleGetPositionType(yogaNode)
        set(value) {
            invalidate()
            YGNodeStyleSetPositionType(yogaNode, value)
        }

    var yogaFlexWrap
        get() = YGNodeStyleGetFlexWrap(yogaNode)
        set(value) {
            invalidate()
            YGNodeStyleSetFlexWrap(yogaNode, value)
        }

    var yogaOverflow
        get() = YGNodeStyleGetOverflow(yogaNode)
        set(value) {
            invalidate()
            YGNodeStyleSetOverflow(yogaNode, value)
        }

    var yogaDisplay
        get() = YGNodeStyleGetDisplay(yogaNode)
        set(value) {
            invalidate()
            YGNodeStyleSetDisplay(yogaNode, value)
        }

    var yogaFlex
        get() = YGNodeStyleGetFlex(yogaNode)
        set(value) {
            invalidate()
            YGNodeStyleSetFlex(yogaNode, value)
        }

    var yogaFlexGrow
        get() = YGNodeStyleGetFlexGrow(yogaNode)
        set(value) {
            invalidate()
            YGNodeStyleSetFlexGrow(yogaNode, value)
        }

    var yogaFlexShrink
        get() = YGNodeStyleGetFlexShrink(yogaNode)
        set(value) {
            invalidate()
            YGNodeStyleSetFlexShrink(yogaNode, value)
        }

    var yogaFlexBasis: Float
        get() {
            val ygVal = YGValue.create()
            val value = YGNodeStyleGetFlexBasis(yogaNode, ygVal).value()
            ygVal.free()
            return value
        }
        set(value) {
            invalidate()
            YGNodeStyleSetFlexBasis(yogaNode, value)
        }

    fun setYogaFlexBasisPercent(value: Float) {
        invalidate()
        YGNodeStyleSetFlexBasisPercent(yogaNode, value)
    }

    fun setYogaFlexBasisAuto() {
        invalidate()
        YGNodeStyleSetFlexBasisAuto(yogaNode)
    }

    var yogaPosition: Vector4f
        get() {
            val ygVal = YGValue.create()
            val value = Vector4f()
            value.x = YGNodeStyleGetPosition(yogaNode, YGEdgeLeft, ygVal).value()
            value.y = YGNodeStyleGetPosition(yogaNode, YGEdgeTop, ygVal).value()
            value.z = YGNodeStyleGetPosition(yogaNode, YGEdgeRight, ygVal).value()
            value.w = YGNodeStyleGetPosition(yogaNode, YGEdgeBottom, ygVal).value()
            ygVal.free()
            return value
        }
        set(value) {
            invalidate()
            YGNodeStyleSetPosition(yogaNode, YGEdgeLeft, value.x)
            YGNodeStyleSetPosition(yogaNode, YGEdgeTop, value.y)
            YGNodeStyleSetPosition(yogaNode, YGEdgeRight, value.z)
            YGNodeStyleSetPosition(yogaNode, YGEdgeBottom, value.w)
        }

    var yogaPositionPercent: Vector4f
        get() = yogaPosition
        set(value) {
            invalidate()
            YGNodeStyleSetPositionPercent(yogaNode, YGEdgeLeft, value.x)
            YGNodeStyleSetPositionPercent(yogaNode, YGEdgeTop, value.y)
            YGNodeStyleSetPositionPercent(yogaNode, YGEdgeRight, value.z)
            YGNodeStyleSetPositionPercent(yogaNode, YGEdgeBottom, value.w)
        }

    var yogaMargin: Vector4f
        get() {
            val ygVal = YGValue.create()
            val value = Vector4f()
            value.x = YGNodeStyleGetMargin(yogaNode, YGEdgeLeft, ygVal).value()
            value.y = YGNodeStyleGetMargin(yogaNode, YGEdgeTop, ygVal).value()
            value.z = YGNodeStyleGetMargin(yogaNode, YGEdgeRight, ygVal).value()
            value.w = YGNodeStyleGetMargin(yogaNode, YGEdgeBottom, ygVal).value()
            ygVal.free()
            return value
        }
        set(value) {
            invalidate()
            YGNodeStyleSetMargin(yogaNode, YGEdgeLeft, value.x)
            YGNodeStyleSetMargin(yogaNode, YGEdgeTop, value.y)
            YGNodeStyleSetMargin(yogaNode, YGEdgeRight, value.z)
            YGNodeStyleSetMargin(yogaNode, YGEdgeBottom, value.w)
        }

    var yogaMarginPercent: Vector4f
        get() = yogaMargin
        set(value) {
            invalidate()
            YGNodeStyleSetMarginPercent(yogaNode, YGEdgeLeft, value.x)
            YGNodeStyleSetMarginPercent(yogaNode, YGEdgeTop, value.y)
            YGNodeStyleSetMarginPercent(yogaNode, YGEdgeRight, value.z)
            YGNodeStyleSetMarginPercent(yogaNode, YGEdgeBottom, value.w)
        }

    var yogaMarginAuto: Vector4f
        get() = yogaMargin
        set(value) {
            invalidate()
            if (value.x > 0f) YGNodeStyleSetMarginAuto(yogaNode, YGEdgeLeft)
            if (value.y > 0f) YGNodeStyleSetMarginAuto(yogaNode, YGEdgeTop)
            if (value.z > 0f) YGNodeStyleSetMarginAuto(yogaNode, YGEdgeRight)
            if (value.w > 0f) YGNodeStyleSetMarginAuto(yogaNode, YGEdgeBottom)
        }

    var yogaPadding: Vector4f
        get() {
            val ygVal = YGValue.create()
            val value = Vector4f()
            value.x = YGNodeStyleGetPadding(yogaNode, YGEdgeLeft, ygVal).value()
            value.y = YGNodeStyleGetPadding(yogaNode, YGEdgeTop, ygVal).value()
            value.z = YGNodeStyleGetPadding(yogaNode, YGEdgeRight, ygVal).value()
            value.w = YGNodeStyleGetPadding(yogaNode, YGEdgeBottom, ygVal).value()
            ygVal.free()
            return value
        }
        set(value) {
            invalidate()
            YGNodeStyleSetPadding(yogaNode, YGEdgeLeft, value.x)
            YGNodeStyleSetPadding(yogaNode, YGEdgeTop, value.y)
            YGNodeStyleSetPadding(yogaNode, YGEdgeRight, value.z)
            YGNodeStyleSetPadding(yogaNode, YGEdgeBottom, value.w)
            usePercentPadding = false
        }

    private var usePercentPadding = false

    var yogaPaddingPercent: Vector4f = Vector4f()
        set(value) {
            usePercentPadding = true
            invalidate()
            field = value
            /*YGNodeStyleSetPaddingPercent(yogaNode, YGEdgeLeft, value.x)
            YGNodeStyleSetPaddingPercent(yogaNode, YGEdgeTop, value.y)
            YGNodeStyleSetPaddingPercent(yogaNode, YGEdgeRight, value.z)
            YGNodeStyleSetPaddingPercent(yogaNode, YGEdgeBottom, value.w)*/
        }

    var yogaBorder: Vector4f
        get() {
            val ygVal = YGValue.create()
            val value = Vector4f()
            value.x = YGNodeStyleGetBorder(yogaNode, YGEdgeLeft)
            value.y = YGNodeStyleGetBorder(yogaNode, YGEdgeTop)
            value.z = YGNodeStyleGetBorder(yogaNode, YGEdgeRight)
            value.w = YGNodeStyleGetBorder(yogaNode, YGEdgeBottom)
            ygVal.free()
            return value
        }
        set(value) {
            invalidate()
            YGNodeStyleSetBorder(yogaNode, YGEdgeLeft, value.x)
            YGNodeStyleSetBorder(yogaNode, YGEdgeTop, value.y)
            YGNodeStyleSetBorder(yogaNode, YGEdgeRight, value.z)
            YGNodeStyleSetBorder(yogaNode, YGEdgeBottom, value.w)
        }

    var yogaWidth: Float
        get() {
            val ygVal = YGValue.create()
            val value = YGNodeStyleGetWidth(yogaNode, ygVal).value()
            ygVal.free()
            return value
        }
        set(value) {
            invalidate()
            YGNodeStyleSetWidth(yogaNode, value)
        }

    var yogaWidthPercent: Float
        get() = yogaWidth
        set(value) {
            invalidate()
            YGNodeStyleSetWidthPercent(yogaNode, value)
        }

    fun setYogaWidthAuto() {
        invalidate()
        YGNodeStyleSetWidthAuto(yogaNode)
    }

    var yogaHeight: Float
        get() {
            val ygVal = YGValue.create()
            val value = YGNodeStyleGetHeight(yogaNode, ygVal).value()
            ygVal.free()
            return value
        }
        set(value) {
            invalidate()
            YGNodeStyleSetHeight(yogaNode, value)
        }

    var yogaHeightPercent: Float
        get() = yogaHeight
        set(value) {
            invalidate()
            YGNodeStyleSetHeightPercent(yogaNode, value)
        }

    fun setYogaHeightAuto() {
        invalidate()
        YGNodeStyleSetHeightAuto(yogaNode)
    }

    var yogaSize: Vector2f
        get() = Vector2f(yogaWidth, yogaHeight)
        set(value) {
            yogaWidth = value.x
            yogaHeight = value.y
        }

    var yogaSizePercent: Vector2f
        get() = yogaSize
        set(value) {
            yogaWidthPercent = value.x
            yogaHeightPercent = value.y
        }

    fun setYogaSizeAuto() {
        setYogaWidthAuto()
        setYogaHeightAuto()
    }

    var yogaMinWidth: Float
        get() {
            val ygVal = YGValue.create()
            val value = YGNodeStyleGetMinWidth(yogaNode, ygVal).value()
            ygVal.free()
            return value
        }
        set(value) {
            invalidate()
            YGNodeStyleSetMinWidth(yogaNode, value)
        }

    var yogaMinWidthPercent: Float
        get() = yogaMinWidth
        set(value) {
            invalidate()
            YGNodeStyleSetMinWidthPercent(yogaNode, value)
        }

    var yogaMinHeight: Float
        get() {
            val ygVal = YGValue.create()
            val value = YGNodeStyleGetMinHeight(yogaNode, ygVal).value()
            ygVal.free()
            return value
        }
        set(value) {
            invalidate()
            YGNodeStyleSetMinHeight(yogaNode, value)
        }

    var yogaMinHeightPercent: Float
        get() = yogaMinHeight
        set(value) {
            invalidate()
            YGNodeStyleSetMinHeightPercent(yogaNode, value)
        }

    var yogaMinSize: Vector2f
        get() = Vector2f(yogaMinWidth, yogaMinHeight)
        set(value) {
            yogaMinWidth = value.x
            yogaMinHeight = value.y
        }

    var yogaMinSizePercent: Vector2f
        get() = yogaMinSize
        set(value) {
            yogaMinWidthPercent = value.x
            yogaMinHeightPercent = value.y
        }

    var yogaMaxWidth: Float
        get() {
            val ygVal = YGValue.create()
            val value = YGNodeStyleGetMaxWidth(yogaNode, ygVal).value()
            ygVal.free()
            return value
        }
        set(value) {
            invalidate()
            YGNodeStyleSetMaxWidth(yogaNode, value)
        }

    var yogaMaxWidthPercent: Float
        get() = yogaMaxWidth
        set(value) {
            invalidate()
            YGNodeStyleSetMaxWidthPercent(yogaNode, value)
        }

    var yogaMaxHeight: Float
        get() {
            val ygVal = YGValue.create()
            val value = YGNodeStyleGetMaxHeight(yogaNode, ygVal).value()
            ygVal.free()
            return value
        }
        set(value) {
            invalidate()
            YGNodeStyleSetMaxHeight(yogaNode, value)
        }

    var yogaMaxHeightPercent: Float
        get() = yogaMaxHeight
        set(value) {
            invalidate()
            YGNodeStyleSetMaxHeightPercent(yogaNode, value)
        }

    var yogaMaxSize: Vector2f
        get() = Vector2f(yogaMaxWidth, yogaMaxHeight)
        set(value) {
            yogaMaxWidth = value.x
            yogaMaxHeight = value.y
        }

    var yogaMaxSizePercent: Vector2f
        get() = yogaMaxSize
        set(value) {
            yogaMaxWidthPercent = value.x
            yogaMaxHeightPercent = value.y
        }


    var yogaAspectRatio
        get() = YGNodeStyleGetAspectRatio(yogaNode)
        set(value) {
            invalidate()
            YGNodeStyleSetAspectRatio(yogaNode, value)
        }

    //endregion

    constructor(inContext: YogaContainer.() -> Unit) : this() {
        inContext()
    }

    override fun addChild(vararg drawable: Drawable) {
        children.synchronized {
            drawable.forEach {
                it.parent = this@YogaContainer
                if (it is YogaContainer) {
                    if (!it.isRoot)
                        YGNodeInsertChild(yogaNode, it.yogaNode, yogaChildrenCount)
                }
                children.add(it)
            }
        }

    }

    override fun insertChild(drawable: Drawable, index: Int) {
        children.synchronized {
            add(index, drawable)
        }
        drawable.parent = this
        if (drawable is YogaContainer) {
            if (!drawable.isRoot)
                YGNodeInsertChild(yogaNode, drawable.yogaNode, index)
        }
    }

    override fun removeChild(drawable: Drawable) {
        children.synchronized {
            remove(drawable)
        }

        drawable.parent = null
        if (drawable is YogaContainer) {
            if (!drawable.isRoot)
                YGNodeRemoveChild(yogaNode, drawable.yogaNode)
        }
    }

    override fun updateDrawable() {
        val sizeBefore = Vector2f(drawSize)

        if (isRoot) {
            super.updateDrawable()

            drawSize.set(round(drawSize.x), round(drawSize.y))
            drawPosition.set(round(drawPosition.x), round(drawPosition.y))
            drawOrigin.set(round(drawOrigin.x), round(drawOrigin.y))

            yogaPosition = Vector4f(drawPosition.x, drawPosition.y, 0f, 0f)
            yogaSize = Vector2f(drawSize.x, drawSize.y)

            YGNodeCalculateLayout(yogaNode, drawSize.x, drawSize.y, YGFlexDirectionRow)
        } else {
            val layout = YGNode.create(yogaNode).layout()

            drawPosition.set(layout.positions(YGEdgeLeft), layout.positions(YGEdgeTop))

            if (parent != null) {
                drawPosition.add(parent!!.drawPosition)
            }

            drawSize.set(layout.dimensions(YGDimensionWidth), layout.dimensions(YGDimensionHeight))
            drawOrigin.set(if (origin == Origin.Custom) customOrigin else origin.offset).mul(drawSize)
        }

        if (sizeBefore != drawSize && usePercentPadding) {
            val sizeBase = if (drawSize.x > drawSize.y) drawSize.y else drawSize.x
            YGNodeStyleSetPadding(yogaNode, YGEdgeLeft, sizeBase * yogaPaddingPercent.x / 100f)
            YGNodeStyleSetPadding(yogaNode, YGEdgeTop, sizeBase * yogaPaddingPercent.y / 100f)
            YGNodeStyleSetPadding(yogaNode, YGEdgeRight, sizeBase * yogaPaddingPercent.z / 100f)
            YGNodeStyleSetPadding(yogaNode, YGEdgeBottom, sizeBase * yogaPaddingPercent.w / 100f)
            recalculateLayout()
        }
    }

    private fun recalculateLayout() {
        if (isRoot) {
            YGNodeCalculateLayout(yogaNode, drawSize.x, drawSize.y, yogaFlexDirection)
        } else {
            (parent as? YogaContainer)?.recalculateLayout()
        }
    }

    override fun dispose() {
        YGNodeFree(yogaNode)
        children.forEach { it.dispose() }
    }

    private companion object {
        private val config = YGConfigNew()

        init {
            YGConfigSetUseWebDefaults(config, true)
            //YGConfigSetUseLegacyStretchBehaviour(config, true)
        }
    }

}