package com.bpsrobotics.engine.utils.geometry

import edu.wpi.first.math.geometry.Pose2d
import kotlin.math.*
class Rectangle(val x1: Double, val y1: Double, val x2: Double, val y2: Double) {
    /**Returns true if the point given in within the bounds of the rectangle */
    fun contains(x: Double, y: Double): Boolean {
        return x in x1..x2 && y in y1..y2
    }
    /**Returns true if the point given in within the bounds of the rectangle */
    operator fun contains(pose: Pose2d): Boolean {
        return contains(pose.x, pose.y)
    }
}
