package com.bpsrobotics.engine.utils.geometry

import edu.wpi.first.math.geometry.Pose2d
import kotlin.math.*
class Rectangle(val coordinate1: Coordinate, val coordinate2: Coordinate) {
    constructor(x1: Double, y1: Double, x2: Double, y2: Double) : this(Coordinate(x1, y1), Coordinate(x2, y2))

    val x1 get() = coordinate1.x
    val y1 get() = coordinate1.y
    val x2 get() = coordinate2.x
    val y2 get() = coordinate2.y

    /**Returns true if the point given in within the bounds of the rectangle */
    fun contains(x: Double, y: Double): Boolean {
        return x in coordinate1.x..coordinate2.x && y in coordinate1.y..coordinate2.y
    }
    /**Returns true if the point given in within the bounds of the rectangle */
    operator fun contains(pose: Pose2d): Boolean {
        return contains(pose.x, pose.y)
    }
}
