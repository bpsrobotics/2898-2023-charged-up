package com.team2898.robot.subsystems

import com.bpsrobotics.engine.async.AsyncLooper
import com.bpsrobotics.engine.odometry.DifferentialDrivePoseProvider
import com.bpsrobotics.engine.odometry.PoseProvider
import com.bpsrobotics.engine.utils.MetersPerSecond
import com.bpsrobotics.engine.utils.Millis
import com.bpsrobotics.engine.utils.NAVX
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard

object Odometry : PoseProvider by DifferentialDrivePoseProvider(
    NavxHolder.navx,
    Drivetrain.leftEncoder,
    Drivetrain.rightEncoder) {

    object NavxHolder {
        val navx = NAVX()
    }

    val otherProvider = DifferentialDrivePoseProvider(
        NavxHolder.navx,
        Drivetrain.leftEncoder,
        Drivetrain.rightEncoder)

    init {
        SmartDashboard.putData("innerprovider", otherProvider)

        AsyncLooper.loop(Millis(1000L / 50), "innerprovider looper") {
            otherProvider.update()
        }
    }

    val leftVel get() =  MetersPerSecond(Drivetrain.leftEncoder.rate)
    val rightVel get() = MetersPerSecond(Drivetrain.rightEncoder.rate)
    val vels get() = DifferentialDriveWheelSpeeds(leftVel.metersPerSecondValue(), rightVel.metersPerSecondValue())
}
