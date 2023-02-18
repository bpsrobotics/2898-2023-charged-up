package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.`M/s`
import com.bpsrobotics.engine.utils.MetersPerSecond
import com.bpsrobotics.engine.utils.Sugar.clamp
import com.bpsrobotics.engine.utils.Sugar.degreesToRadians
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry
import com.team2898.robot.subsystems.Odometry.NavxHolder.navx
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.absoluteValue

/** Robot automatically balances on the charge station
 * @author Ori, Max Leibowitz, Anthony
 * */
class AutoBalance : CommandBase() {
    private val pid = PIDController(0.026, 0.0, 0.0)
    private val dController = PIDController(0.0, 0.0, 0.005)
    private var pitch = navx.pitch.toDouble() - 16.5
    private val timer = Timer()

    private var pitchRate = 0.0
    private var isCentered = true

    // estimated CG position
    private var min = -300.0
    private var max = 300.0

    private var state = findState()

    //private val odometry = DifferentialDriveOdometry(Rotation2d.fromRotations(0.0), Drivetrain.leftEncoder.distance, Drivetrain.rightEncoder.distance)
    private val odometry = Odometry
    private var IN_ZONE = 0.0
    private var APPROACHING_KP = 0.0
    override fun initialize() {
        SmartDashboard.putNumber("IN ZONE SPEED", 0.3)
        SmartDashboard.putNumber("APPROACHING KP", 0.0)
    }
    override fun execute() {
        IN_ZONE = SmartDashboard.getNumber("IN ZONE SPEED",0.0)
        APPROACHING_KP = SmartDashboard.getNumber("APPROACHING KP",0.0)
//        IN_ZONE = 0.3

        if (IN_ZONE > 1.0) IN_ZONE = 0.1
        if (APPROACHING_KP > 1.0) APPROACHING_KP = 0.1

        SmartDashboard.putString("Current State", state.name)
        SmartDashboard.putNumber("Current Min", min)
        SmartDashboard.putNumber("Current Max", max)
        SmartDashboard.putNumber("Pitchrate", pitchRate)
        //val pose = odometry.update(Rotation2d.fromRotations(0.0), Drivetrain.leftEncoder.distance, Drivetrain.rightEncoder.distance)
        val pose = odometry.pose
        val m = -0.0176546 * 0.99999

        // Gets the time since last execute, then resets the timer
        val elapsedTime = timer.get()
        timer.reset()
        timer.start()

        // Gathers the change in pitch since last execute
        pitchRate = ((navx.pitch - 16.5) - pitch) / elapsedTime

        // Gets the current pitch and pitch of the robot
        pitch = navx.pitch.toDouble() - 16.5


        //TODO: Fine tune these values to improve the balance
        if (pitchRate > 50 && state == DrivingState.DRIVINGFORWARDS) {
            min = pose.x
            state = DrivingState.DRIVINGBACKWARDS
        }
        else if (pitchRate < -50 && state == DrivingState.DRIVINGBACKWARDS) {
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
//                Drivetrain.stupidDrive(`M/s`(speed),`M/s`(speed))
                drive(speed, speed)
            }
            DrivingState.DRIVINGBACKWARDS -> {
                val speed = when (pose.x) {
                    in min..max -> -IN_ZONE
                    else -> (max - pose.x) * -APPROACHING_KP - IN_ZONE
                }
//                Drivetrain.stupidDrive(`M/s`(speed),`M/s`(speed))
                drive(speed, speed)
            }
            DrivingState.DRIVINGTOMIDDLE -> {
                //TODO: Adjust the multiplied amount
                val middlePower = (Odometry.pose.x - averagePos) * 0.1
//                Drivetrain.stupidDrive(`M/s`(-middlePower),`M/s`(-middlePower))
                drive(-middlePower, -middlePower)
            }
            DrivingState.BALANCING -> {
                val pitchPower = pid.calculate(pitch).clamp(-0.05, 0.05) + dController.calculate(pitch) + pitch * m
                if (pitchRate.absoluteValue > 3) {
                    // TODO: Widen estimate range?
                    state = findState()
                    val difference = (max-min) * 0.2
                    min -= difference
                    max += difference
                }
//                Drivetrain.stupidDrive(`M/s`(pitchPower), `M/s`(pitchPower))
                drive(pitchPower, pitchPower)

            }

            else -> {}
        }

    }

    fun drive(left: Double, right: Double){
        val kSin = 0.0
//        var ff = sin(navx.pitch.toDouble().degreesToRadians()) * kSin
        val ff = 0.0
        Drivetrain.stupidDrive(MetersPerSecond(left + ff), MetersPerSecond(left + ff))

    }

    enum class DrivingState {
        /** Robot drives forwards to find maximum */
        DRIVINGFORWARDS,
        /** Robot drives backwards to find the minimum */
        DRIVINGBACKWARDS,
        DRIVINGTOMIDDLE,
        /** Robot attempts to stay still to keep in position between to maximum and minimum */
        BALANCING
    }
    /**
     * @return The state the robot should be in
     * @author Ori
     * */
    private fun findState(): DrivingState {
        return when {
            max - min < 0.2 -> {
                DrivingState.DRIVINGTOMIDDLE
            }
            pitchRate > 20 && pitch > 5 -> {
                DrivingState.DRIVINGFORWARDS
            }
            pitchRate < -20 && pitch < -5 -> {
                DrivingState.DRIVINGBACKWARDS
            }
            (Odometry.pose.x) in (min..max) && isCentered -> {
                DrivingState.BALANCING
            }
            else -> state ?: DrivingState.DRIVINGFORWARDS
        }
    }
    /** Finishes if both rotations are close to zero and haven't changed quickly */
    override fun isFinished(): Boolean {
        //TODO: Test and adjust these values to be more accurate
//        return (pitch > -2.5 || pitch < 2.5) && (pitch > -2.5 || pitch < 2.5) && (pitchRate < 0.3 && pitchRate < 0.3)
        return false
    }
}
