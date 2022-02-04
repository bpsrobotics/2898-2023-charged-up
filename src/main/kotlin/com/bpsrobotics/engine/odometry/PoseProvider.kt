package com.bpsrobotics.engine.odometry

import com.bpsrobotics.engine.utils.Degrees
import com.bpsrobotics.engine.utils.Meters
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.util.sendable.Sendable
import edu.wpi.first.util.sendable.SendableBuilder
import edu.wpi.first.util.sendable.SendableRegistry

/** Provides an orientation and position for the robot */
interface PoseProvider : Sendable {
    /** Provides the pose as a WPIlib Pose2d */
    val pose: Pose2d

    /** Updates the [pose] variable */
    fun update()

    fun reset(x: Meters, y: Meters, theta: Degrees)

    override fun initSendable(builder: SendableBuilder) {
        SendableRegistry.setName(this, toString())
        builder.addDoubleProperty("x", { pose.x }, null)
        builder.addDoubleProperty("y", { pose.y }, null)
        builder.addDoubleProperty("rotation", { pose.rotation.degrees }, null)
    }
}
