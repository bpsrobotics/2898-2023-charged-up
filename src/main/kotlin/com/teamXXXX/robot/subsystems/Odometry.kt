package com.teamXXXX.robot.subsystems

import com.bpsrobotics.engine.odometry.DifferentialDrivePoseProvider
import com.bpsrobotics.engine.odometry.PoseProvider
import com.bpsrobotics.engine.utils.MetersPerSecond
import com.bpsrobotics.engine.utils.NAVX
import edu.wpi.first.wpilibj.Sendable
import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder
import edu.wpi.first.wpilibj.smartdashboard.SendableRegistry

object Odometry : Sendable, PoseProvider by DifferentialDrivePoseProvider(NAVX(), Drivetrain.leftEncoder, Drivetrain.rightEncoder) {

    val leftVel get() =  MetersPerSecond(Drivetrain.leftEncoder.rate)
    val rightVel get() = MetersPerSecond(Drivetrain.rightEncoder.rate)
    val vels get() = DifferentialDriveWheelSpeeds(leftVel.metersPerSecondValue(), rightVel.metersPerSecondValue())

    /** Initializes the pose to send to the SmartDashboard */
    override fun initSendable(builder: SendableBuilder?) {
        SendableRegistry.setName(this, "odometry")
        builder?.addDoubleProperty("x", { pose.x }, null)
        builder?.addDoubleProperty("y", { pose.y }, null)
        builder?.addDoubleProperty("degrees", { pose.rotation.degrees }, null)
    }
}
