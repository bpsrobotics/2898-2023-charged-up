package com.bpsrobotics.engine

import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.controller.PIDController
import edu.wpi.first.wpilibj.controller.RamseteController
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward
import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.geometry.Translation2d
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds
import edu.wpi.first.wpilibj.trajectory.Trajectory
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveVoltageConstraint

/**
 *  Represents the results from the characterization tool.
 * See [https://docs.wpilib.org/en/stable/docs/software/wpilib-tools/robot-characterization/index.html]
 */
data class DrivetrainCharacterization(val ks: Volts, val kv: Double, val ka: Double, val kp: Double, val kd: Double)

/**
 * Represents the limits how fast the robot is allowed to go, how fast it can accelerate, and the maximum motor voltage.
 */
data class AutoConstraints(val maxVelocity: MetersPerSecond, val maxAcceleration: MetersPerSecondSquared, val maxVoltage: Volts)

/**
 * Contains the parameters for a ramsete controller (see [RamseteController]).
 */
data class RamseteParameters(val b: Double = 2.0, val z: Double = 0.0)

/**
 * Represents the left and right motor voltages.
 */
data class WheelVoltages(val left: Volts, val right: Volts)


/**
 * A representation of a ramsete-controlled drivetrain for following trajectories.  Call [update] once
 * per tick, and supply the outputs to the motors.
 * For more information on how it works, see [https://docs.wpilib.org/en/stable/docs/software/examples-tutorials/trajectory-tutorial/index.html]
 */
class RamseteDrivetrain(
    trackWidth: Meters,
    characterization: DrivetrainCharacterization,
    limits: AutoConstraints,
    ramseteParameters: RamseteParameters
    ) {

    private val kinematics = DifferentialDriveKinematics(trackWidth.value)

    private val feedforward = SimpleMotorFeedforward(characterization.ks.value, characterization.kv, characterization.ka)

    private val voltageConstraint = DifferentialDriveVoltageConstraint(
        feedforward,
        kinematics,
        limits.maxVoltage.value
    )

    private val trajectoryConfig = TrajectoryConfig(limits.maxVelocity.value, limits.maxAcceleration.value)
        .setKinematics(kinematics).addConstraint(voltageConstraint)

    private val controller = RamseteController(ramseteParameters.b, ramseteParameters.z)

    private var trajectory: Trajectory? = null

    private val timer = Timer()

    private var previousSpeeds = DifferentialDriveWheelSpeeds(0.0, 0.0)

    private val leftPid  = PIDController(characterization.kp, 0.0, characterization.kd)  // no Ki because it can lead to runaway
    private val rightPid = PIDController(characterization.kp, 0.0, characterization.kd)

    private var previousTime = 0.0

    fun createTrajectory(startingPose: Pose2d, vararg points: Translation2d, endingPose: Pose2d) {
        trajectory = TrajectoryGenerator.generateTrajectory(
            startingPose,
            points.toMutableList(),
            endingPose,
            trajectoryConfig
        )
    }

    fun update(currentPose: Pose2d, wheelVelocities: DifferentialDriveWheelSpeeds): WheelVoltages {
        val trajectoryNotNull = trajectory ?: return WheelVoltages(Volts(0.0), Volts(0.0))

        val time = timer.get()
        val delta = time - previousTime
        previousTime = time

        // Calculate how fast to turn the wheels to get to the target pose
        val targetVelocities = kinematics.toWheelSpeeds(
            controller.calculate(currentPose, trajectoryNotNull.sample(time))
        )

        // Add feed forward to compensate for base friction
        val leftFeedForward = feedforward.calculate(
            targetVelocities.leftMetersPerSecond,
            (targetVelocities.leftMetersPerSecond - previousSpeeds.leftMetersPerSecond) / delta
        )
        val rightFeedForward = feedforward.calculate(
            targetVelocities.rightMetersPerSecond,
            (targetVelocities.rightMetersPerSecond - previousSpeeds.rightMetersPerSecond) / delta
        )

        previousSpeeds = wheelVelocities

        val leftOutput = leftPid.calculate(wheelVelocities.leftMetersPerSecond) + leftFeedForward
        val rightOutput = rightPid.calculate(wheelVelocities.rightMetersPerSecond) + rightFeedForward

        return WheelVoltages(Volts(leftOutput), Volts(rightOutput))
    }
}
