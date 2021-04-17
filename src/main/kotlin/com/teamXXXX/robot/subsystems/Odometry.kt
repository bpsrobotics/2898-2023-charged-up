package com.teamXXXX.robot.subsystems

import com.bpsrobotics.engine.odometry.DifferentialDrivePoseProvider
import com.bpsrobotics.engine.odometry.PoseProvider
import edu.wpi.first.wpilibj.ADXRS450_Gyro
import edu.wpi.first.wpilibj.geometry.Pose2d

object Odometry {
    val gyro = ADXRS450_Gyro()  // TODO: figure out how to use navx instead of whatever this is
    val odometry: PoseProvider = DifferentialDrivePoseProvider(gyro, Drivetrain.leftEncoder, Drivetrain.rightEncoder)

    fun pose(): Pose2d {
        return odometry.pose
    }
}
