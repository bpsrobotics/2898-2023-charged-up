package com.bpsrobotics.engine.odometry

import edu.wpi.first.wpilibj.Encoder
import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.geometry.Rotation2d
import edu.wpi.first.wpilibj.interfaces.Gyro
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry

class DifferentialDrivePoseProvider(val gyro: Gyro, val leftEncoder: Encoder, val rightEncoder: Encoder) : PoseProvider {
    private val odometry = DifferentialDriveOdometry(Rotation2d(0.0))

    override var pose: Pose2d = Pose2d()
        private set

    override fun update() {
        pose = odometry.update(
            Rotation2d.fromDegrees(-gyro.angle),
            leftEncoder.distance,
            rightEncoder.distance
        )
    }
}
