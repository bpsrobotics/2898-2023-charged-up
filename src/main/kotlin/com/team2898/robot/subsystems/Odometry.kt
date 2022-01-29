package com.team2898.robot.subsystems

import com.bpsrobotics.engine.odometry.DifferentialDrivePoseProvider
import com.bpsrobotics.engine.odometry.PoseProvider
import com.bpsrobotics.engine.utils.MetersPerSecond
import com.bpsrobotics.engine.utils.NAVX
import com.team2898.robot.AveragePoseProvider
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds
import edu.wpi.first.util.sendable.Sendable
import edu.wpi.first.util.sendable.SendableBuilder
import edu.wpi.first.util.sendable.SendableRegistry

object Odometry : Sendable, PoseProvider by DifferentialDrivePoseProvider(
    Blah.gyro,
    Drivetrain.leftEncoder,
    Drivetrain.rightEncoder) {

    private object Blah {
        val gyro = NAVX()
    }

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
