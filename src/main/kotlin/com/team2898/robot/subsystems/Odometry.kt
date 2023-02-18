package com.team2898.robot.subsystems

import com.bpsrobotics.engine.async.AsyncLooper
import com.bpsrobotics.engine.odometry.DifferentialDrivePoseProvider
import com.bpsrobotics.engine.odometry.PoseProvider
import com.bpsrobotics.engine.utils.MetersPerSecond
import com.bpsrobotics.engine.utils.Millis
import com.bpsrobotics.engine.utils.NAVX
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds
import edu.wpi.first.util.sendable.SendableBuilder
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Odometry : SubsystemBase(), PoseProvider by DifferentialDrivePoseProvider(
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

    override fun initSendable(builder: SendableBuilder) {
        builder.addFloatProperty("pitch", NavxHolder.navx::getPitch) {}
//        builder.addDoubleProperty("x", NavxHolder.navx::getRawGyroX) {}
//        builder.addDoubleProperty("y", NavxHolder.navx::getRawGyroY) {}
//        builder.addDoubleProperty("rot", pose.rotation.degrees) {}
    }

    override fun periodic() {
        val p = pose
        SmartDashboard.putNumber("x", p.x)
        SmartDashboard.putNumber("y", p.y)
        SmartDashboard.putNumber("rotation", p.rotation.degrees)
    }
}
