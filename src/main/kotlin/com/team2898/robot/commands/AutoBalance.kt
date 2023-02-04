package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.`M/s`
import com.bpsrobotics.engine.utils.Sugar.clamp
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry.NavxHolder.navx
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.absoluteValue

class AutoBalance : CommandBase() {
    private val pid = PIDController(0.026, 0.0, 0.0)
    private val dController = PIDController(0.0, 0.0, 0.005)
    private var roll = navx.roll.toDouble()
    private val timer = Timer()
    private var elapsedTime = 0.0

    private var rollRate = 0.0

    // estimated CG position
    private var min = 0.0
    private var max = 5.0

    private var state = findState()

    private val odometry = DifferentialDriveOdometry(Rotation2d.fromRotations(0.0), Drivetrain.leftEncoder.distance, Drivetrain.rightEncoder.distance)
    private val IN_ZONE = 0.0
    private val APPROACHING_KP = 0.0
    override fun execute() {
        val pose = odometry.update(Rotation2d.fromRotations(0.0), Drivetrain.leftEncoder.distance, Drivetrain.rightEncoder.distance)
        val m = -0.0176546 * 0.99999

        // Gets the time since last execute, then resets the timer
        elapsedTime = timer.get()
        timer.reset()
        timer.start()

        // Gathers the change in roll since last execute
        rollRate = (navx.roll - roll) / elapsedTime

        // Gets the current pitch and roll of the robot
        roll = navx.roll.toDouble()


        //TODO: Fine tune these values to improve the balance
        if (rollRate > 3 && state == DrivingState.DRIVINGFORWARDS) {
            min = pose.x
            state = DrivingState.DRIVINGBACKWARDS
        }
        else if (rollRate < -3 && state == DrivingState.DRIVINGBACKWARDS) {
            max = pose.x
            state = DrivingState.DRIVINGBACKWARDS
        }

        val averagePos = (max+min)/2

        state = findState()

        when (state) {
            DrivingState.DRIVINGFORWARDS -> {
                val speed = when (pose.x) {
                    in min..max -> IN_ZONE
                    else -> (min - pose.x) * APPROACHING_KP + IN_ZONE
                }
                Drivetrain.stupidDrive(`M/s`(speed),`M/s`(speed))
            }
            DrivingState.DRIVINGBACKWARDS -> {
                val speed = when (pose.x) {
                    in min..max -> -IN_ZONE
                    else -> (max - pose.x) * -APPROACHING_KP - IN_ZONE
                }
                Drivetrain.stupidDrive(`M/s`(speed),`M/s`(speed))
            }
            DrivingState.DRIVINGTOMIDDLE -> {
                //TODO: Adjust the multiplied amount
                val middlePower = (odometry.poseMeters.x - averagePos) * 0.1
                Drivetrain.stupidDrive(`M/s`(-middlePower),`M/s`(-middlePower))
            }
            DrivingState.BALANCING -> {
                val rollPower = pid.calculate(roll).clamp(-0.05, 0.05) + dController.calculate(roll) + roll * m
                if (rollRate.absoluteValue > 3) {
                    // TODO: Widen estimate range?
                    state = findState()
                    val difference = (max-min) * 0.2
                    min -= difference
                    max += difference
                }
                Drivetrain.stupidDrive(`M/s`(rollPower), `M/s`(rollPower))

            }

            else -> {}
        }

    }

    enum class DrivingState {
        DRIVINGFORWARDS,
        DRIVINGBACKWARDS,
        DRIVINGTOMIDDLE,
        BALANCING
    }

    private fun findState(): DrivingState?  {
        return when {
            max - min < 0.2 -> {
                DrivingState.DRIVINGTOMIDDLE
            }
            rollRate > 3 || roll > 0 -> {
                DrivingState.DRIVINGFORWARDS
            }
            rollRate < -3 || roll < 0 -> {
                DrivingState.DRIVINGBACKWARDS
            }
            (odometry.poseMeters.x) in (min..max) -> {
                DrivingState.BALANCING
            }
            else -> state
        }
    }
    override fun isFinished(): Boolean {
        //TODO: Test and adjust these values to be more accurate
        /** Finishes if both rotations are close to zero and haven't changed quickly */
//        return (pitch > -2.5 || pitch < 2.5) && (roll > -2.5 || roll < 2.5) && (pitchRate < 0.3 && rollRate < 0.3)
        return false
    }
}
