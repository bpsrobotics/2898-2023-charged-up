package com.bpsrobotics.engine.odometry

import edu.wpi.first.wpilibj.Encoder
import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.geometry.Rotation2d
import edu.wpi.first.wpilibj.interfaces.Gyro
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry

/** Implements the PoseProvider interface with differential drive odometry and a gyro to get a pose  */
class DifferentialDrivePoseProvider(private val gyro: Gyro, private val leftEncoder: Encoder, private val rightEncoder: Encoder) : PoseProvider {

    /** The differential drive odometry provides the position part of the pose, but not the angle*/
    private val odometry = DifferentialDriveOdometry(gyro.rotation2d)

    override var pose: Pose2d = Pose2d()
        private set // Makes pose read only

    /** Uses the gyro angle and encoder distances to update the pose */
    override fun update() {
        pose = odometry.update(
            gyro.rotation2d,
            leftEncoder.distance,
            rightEncoder.distance
        )
    }
}
