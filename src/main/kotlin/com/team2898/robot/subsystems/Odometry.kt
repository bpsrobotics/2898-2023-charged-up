package com.team2898.robot.subsystems

import com.bpsrobotics.engine.odometry.PoseProvider
import com.bpsrobotics.engine.utils.Degrees
import com.bpsrobotics.engine.utils.Meters
import com.bpsrobotics.engine.utils.MetersPerSecond
import com.bpsrobotics.engine.utils.NAVX
import com.team2898.robot.Constants.DRIVETRAIN_TRACK_WIDTH
import edu.wpi.first.math.Matrix
import edu.wpi.first.math.Nat
import edu.wpi.first.math.estimator.DifferentialDrivePoseEstimator
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.DifferentialDriveKinematics
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds
import edu.wpi.first.util.sendable.SendableBuilder
import edu.wpi.first.util.sendable.SendableRegistry
import edu.wpi.first.wpilibj.smartdashboard.Field2d
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Odometry : SubsystemBase(), PoseProvider {

    object NavxHolder {
        val navx = NAVX()
    }

    private val otherProvider = DifferentialDrivePoseEstimator(DifferentialDriveKinematics(DRIVETRAIN_TRACK_WIDTH.meterValue()), NavxHolder.navx.rotation2d, 0.0, 0.0, Pose2d())
    val leftVel get() =  MetersPerSecond(Drivetrain.leftEncoder.rate)
    val rightVel get() = MetersPerSecond(Drivetrain.rightEncoder.rate)
    val vels get() = DifferentialDriveWheelSpeeds(leftVel.metersPerSecondValue(), rightVel.metersPerSecondValue())

    override var pose: Pose2d = Pose2d(0.0, 0.0, Rotation2d(0.0))
        private set

    val field = Field2d()

    init {
        val stdDevs = Matrix(Nat.N3(), Nat.N1())
        Vision.listeners.add { visionPose, time ->
            stdDevs.fill(Vision.stdev)
            otherProvider.setVisionMeasurementStdDevs(stdDevs)
            otherProvider.addVisionMeasurement(visionPose, time)
        }
    }

    override fun periodic() {
        update()
    }

    override fun reset(x: Meters, y: Meters, theta: Degrees) {
        val p = Pose2d(x.value, y.value, Rotation2d.fromDegrees(theta.value))
        otherProvider.resetPosition(NavxHolder.navx.rotation2d, Drivetrain.leftEncoder.distance, Drivetrain.rightEncoder.distance, p)
    }

    override fun update() {
        pose = otherProvider.update(NavxHolder.navx.rotation2d, Drivetrain.leftEncoder.distance, Drivetrain.rightEncoder.distance)

        field.robotPose = pose
        SmartDashboard.putData(field)
    }

    override fun initSendable(builder: SendableBuilder) {
        SendableRegistry.setName(this, toString())
        builder.addDoubleProperty("x", { pose.x }, null)
        builder.addDoubleProperty("y", { pose.y }, null)
        builder.addDoubleProperty("rotation", { pose.rotation.radians }, null)
    }
}
