package com.teamXXXX.robot.subsystems

import com.bpsrobotics.engine.odometry.DifferentialDrivePoseProvider
import com.bpsrobotics.engine.odometry.PoseProvider
import com.bpsrobotics.engine.utils.NAVX
import com.kauailabs.navx.frc.AHRS
import edu.wpi.first.wpilibj.ADXRS450_Gyro
import edu.wpi.first.wpilibj.Sendable
import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard

object Odometry : Sendable{
    val gyro = NAVX()
    val odometry: PoseProvider = DifferentialDrivePoseProvider(gyro, Drivetrain.leftEncoder, Drivetrain.rightEncoder)

    fun pose(): Pose2d {
        return odometry.pose
    }

    /** Initializes the pose to send to the SmartDashboard */
    override fun initSendable(builder: SendableBuilder?) {
        builder?.addDoubleArrayProperty("pose-2d", {
            val pose = pose()
            doubleArrayOf(pose.x, pose.y, pose.rotation.degrees)
        }, null)
    }
}
