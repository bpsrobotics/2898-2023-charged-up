package com.bpsrobotics.engine.utils.geometry

import edu.wpi.first.math.geometry.Pose2d
import kotlin.math.*
class Rectangle(val coordinate1: Coordinate, val coordinate2: Coordinate) {
    /**Returns true if the point given in within the bounds of the rectangle */
    fun contains(x: Double, y: Double): Boolean {
        return x in coordinate1.x..coordinate2.x && y in coordinate1.y..coordinate2.y
    }
    /**Returns true if the point given in within the bounds of the rectangle */
    operator fun contains(pose: Pose2d): Boolean {
        return contains(pose.x, pose.y)
    }
}
