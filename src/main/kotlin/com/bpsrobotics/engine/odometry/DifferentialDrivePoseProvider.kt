package com.bpsrobotics.engine.odometry

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry
import edu.wpi.first.wpilibj.Encoder
import edu.wpi.first.wpilibj.interfaces.Gyro

/** Implements the PoseProvider interface with differential drive odometry and a gyro to get a pose  */
class DifferentialDrivePoseProvider(private val gyro: Gyro, private val leftEncoder: Encoder?, private val rightEncoder: Encoder?) : PoseProvider {

    /** The differential drive odometry provides the position part of the pose, but not the angle*/
    private val odometry = DifferentialDriveOdometry(gyro.rotation2d)

    override var pose: Pose2d = Pose2d()
        private set // Makes pose read only

    /** Uses the gyro angle and encoder distances to update the pose */
    override fun update() {
        leftEncoder ?: return
        rightEncoder ?: return
        pose = odometry.update(
            gyro.rotation2d,
            leftEncoder.distance,
            rightEncoder.distance
        )
    }

    override fun reset() {
        gyro.reset()
        leftEncoder?.reset()
        rightEncoder?.reset()
        odometry.resetPosition(Pose2d(Translation2d(0.0, 0.0), Rotation2d(0.0)), Rotation2d(0.0))
    }
}
