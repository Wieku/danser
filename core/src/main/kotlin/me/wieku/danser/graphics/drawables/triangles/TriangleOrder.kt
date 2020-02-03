package me.wieku.danser.graphics.drawables.triangles

enum class TriangleOrder {
    /**
     * Triangles will be ordered in random manner
     */
    Random,

    /**
     * Smallest triangles will be on top
     */
    SmallestToBiggest,

    /**
     * Biggest triangles will be on top
     */
    BiggestToSmallest,

    /**
     * The newest triangles will be on top
     */
    Latest
}