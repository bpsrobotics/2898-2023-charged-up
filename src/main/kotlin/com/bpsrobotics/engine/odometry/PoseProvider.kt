package com.bpsrobotics.engine.odometry

import edu.wpi.first.wpilibj.geometry.Pose2d

/** Provides an orientation and position for the robot */
interface PoseProvider {
    /** Provides the pose as a WPIlib Pose2d */
    val pose: Pose2d

    /** Updates the [pose] variable */
    fun update()
}
