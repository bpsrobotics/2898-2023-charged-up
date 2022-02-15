package com.bpsrobotics.engine.utils

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Transform2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.trajectory.Trajectory
import kotlin.math.PI

object TrajectoryUtils {
    /**
     * Takes a trajectory made for one alliance and inverts it to be run as the other alliance.
     *
     * Designed for the 2022 RAPID REACT Field.
     */
    fun invertTrajectory(path: Trajectory): Trajectory {
        val centerField = Pose2d(8.2296, 4.1148, Rotation2d())
        val positionTransform = Transform2d(
            centerField.minus(path.initialPose).translation,
            Rotation2d()
        )
        val rotationTransform = Transform2d(
            Translation2d(),
            Rotation2d(PI)
        )

        var newPath = path.transformBy(positionTransform)
        newPath = newPath.transformBy(positionTransform)
        newPath = newPath.transformBy(rotationTransform)

        return newPath
    }

    /**
     * Returns the end pose of the trajectory.
     */
    val Trajectory.endPose: Pose2d get() = this.states[this.states.size - 1].poseMeters
}