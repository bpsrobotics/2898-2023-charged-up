package com.bpsrobotics.engine.utils

import edu.wpi.first.math.geometry.Pose2d

class Rectangle(val x1: Double, val y1: Double, val x2: Double, val y2: Double) {
    fun contains(x: Double, y: Double): Boolean {
        return x in x1..x2 && y in y1..y2
    }

    operator fun contains(pose: Pose2d): Boolean {
        return contains(pose.x, pose.y)
    }
    // check if pose2d is inside rectangle have two corner points have a double
}
