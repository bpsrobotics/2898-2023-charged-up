package com.bpsrobotics.engine.odometry

import edu.wpi.first.wpilibj.geometry.Pose2d

interface PoseProvider {
    val pose: Pose2d

    fun update()
}
