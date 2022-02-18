package com.bpsrobotics.engine.controls

import com.bpsrobotics.engine.odometry.PoseProvider
import com.bpsrobotics.engine.utils.*
import edu.wpi.first.math.controller.RamseteController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.math.kinematics.DifferentialDriveKinematics
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds
import edu.wpi.first.math.trajectory.Trajectory

/**
 * @param trackWidth The width of the drivetrain, wheel-to-wheel
 * @param pose The pose provider used to determine the robot's position and orientation
 * @param leftController The PID (or something else) controller for the left side
 * @param rightController The PID (or something else) controller for the right side
 * @param leftFF The feedforward object for the left side
 * @param rightFF The feedforward object for the right side
 * @param b Increasing b makes it stick to the path more aggressively, defaults should work for most drivetrains
 * @param z Dampens the path sticking, defaults should work for most drivetrains
 */
class Ramsete(
    private val trackWidth: Meters,
    private val pose: PoseProvider,
    private val leftController: Controller,
    private val rightController: Controller,
    private val leftFF: SimpleMotorFeedforward,
    private val rightFF: SimpleMotorFeedforward,
    b: Double = 2.0, // TODO: Figure out how these two constants affect robot performance
    z: Double = 0.7,
) {
    private val ramsete = RamseteController(b, z)
    private val kinematics = DifferentialDriveKinematics(trackWidth.value)

    private fun velocities(trajectory: Trajectory, time: Seconds): DifferentialDriveWheelSpeeds {
        val goal = trajectory.sample(time.value)
        val adjustedSpeeds = ramsete.calculate(pose.pose, goal)
        return kinematics.toWheelSpeeds(adjustedSpeeds)
    }

    data class WheelVoltages(val left: Volts, val right: Volts)

    fun voltages(trajectory: Trajectory, time: Seconds, wheelVelocities: DifferentialDriveWheelSpeeds): WheelVoltages {
        val velocities = velocities(trajectory, time)

        val leftFeedforward = leftFF.calculate(velocities.leftMetersPerSecond).volts
        val rightFeedforward = rightFF.calculate(velocities.rightMetersPerSecond).volts

        return WheelVoltages(
            leftController.calculate(wheelVelocities.leftMetersPerSecond, velocities.leftMetersPerSecond).volts + leftFeedforward,
            rightController.calculate(wheelVelocities.rightMetersPerSecond, velocities.rightMetersPerSecond).volts + rightFeedforward
        )
    }
}
