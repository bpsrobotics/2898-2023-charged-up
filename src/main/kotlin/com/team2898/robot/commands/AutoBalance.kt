package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.Constants
import com.team2898.robot.subsystems.Odometry
import com.bpsrobotics.engine.utils.NAVX
import com.bpsrobotics.engine.utils.Sugar.clamp
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry.pose
import com.team2898.robot.subsystems.Vision
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry
import edu.wpi.first.wpilibj.Timer
import kotlin.math.absoluteValue
import kotlin.math.atan
import kotlin.math.log
import kotlin.math.sqrt

class AutoBalance : CommandBase() {
    private val pid = PIDController(0.026, 0.0, 0.0)
    private val dController = PIDController(0.0, 0.0, 0.005)
    private val navx = NAVX()
    private var pitch = navx.pitch.toDouble()
    private var roll = navx.roll.toDouble()
    private val timer = Timer()
    private var elapsedTime = 0.0

    private var pitchRate = 0.0
    private var rollRate = 0.0

    private var min = 0.0
    private var max = 0.0
    private var drivingForward = true
    private var drivingBack = false
    var state = findState()
    val odometry = DifferentialDriveOdometry(Rotation2d.fromRotations(0.0), Drivetrain.leftEncoder.distance, Drivetrain.rightEncoder.distance)
    var speed = 0.0

    override fun initialize() {
        navx.zeroYaw()
    }
    override fun execute() {
        odometry.update(Rotation2d.fromRotations(0.0), Drivetrain.leftEncoder.distance, Drivetrain.rightEncoder.distance)
        val m = -0.0176546 * 0.99999
//        val b = 0.0176546

        //Gets the time since last execute, then resets the timer
        elapsedTime = timer.get()
        timer.reset()
        timer.start()

        //Gathers the change in the pitch and the change in roll since last execute
//        pitchRate = (navx.pitch - pitch) / elapsedTime
        rollRate = (navx.roll - roll) / elapsedTime

        //Gets the current pitch and roll of the robot
//        pitch = navx.pitch.toDouble()
        roll = navx.roll.toDouble()

        println(roll)

        /*
        //Gets how much power is needed to re-align the bot
//        val pitchPower = pid.calculate(pitch)
        val rollPower = pid.calculate(roll).clamp(-0.05, 0.05) + dController.calculate(roll) + roll * m

        println("trying to go $rollPower m/s")
//        if (rollRate > 10) {
        Drivetrain.stupidDrive(`M/s` (rollPower), `M/s`(rollPower))
//        } else if (rollRate <= 10) {
//            Drivetrain.stupidDrive(`M/s` (rollPower/2), `M/s`(rollPower/2))
//        }

         */



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

        when (state) {
            DrivingState.DRIVINGFORWARDS -> {
                if (pose.x < averagePos) {speed = averagePos - pose.x}
                else if (pose.x > averagePos) {speed = pose.x - averagePos}
                Drivetrain.stupidDrive(`M/s`(speed),`M/s`(speed))
            }
            DrivingState.DRIVINGBACKWARDS -> {
                if (pose.x < averagePos) {speed = averagePos - pose.x}
                else if (pose.x > averagePos) {speed = pose.x - averagePos}
                Drivetrain.stupidDrive(`M/s`(-speed),`M/s`(-speed))
            }
            DrivingState.FINDINGMIDDLE -> {
                val rollPower = pid.calculate(roll).clamp(-0.05, 0.05) + dController.calculate(roll) + roll * m
                if (rollRate > 10) {
                    Drivetrain.stupidDrive(`M/s` (rollPower), `M/s`(rollPower))
                } else if (rollRate <= 10) {
                    Drivetrain.stupidDrive(`M/s` (rollPower/2), `M/s`(rollPower/2))
                }
            }

            DrivingState.NOTMOVING -> {}
        }

    }

    enum class DrivingState() {
        DRIVINGFORWARDS(),
        DRIVINGBACKWARDS(),
        FINDINGMIDDLE(),
        NOTMOVING()
    }

    fun findState(): DrivingState {
        return if ((Drivetrain.leftEncoder.rate+Drivetrain.rightEncoder.rate) > 0) {
            DrivingState.DRIVINGFORWARDS
        } else if ((Drivetrain.leftEncoder.rate+Drivetrain.rightEncoder.rate) > 0) {
            DrivingState.DRIVINGBACKWARDS
        } else DrivingState.NOTMOVING
    }
    override fun isFinished(): Boolean {
        //TODO: Test and adjust these values to be more accurate
        /** Finishes if both rotations are close to zero and haven't changed quickly */
//        return (pitch > -2.5 || pitch < 2.5) && (roll > -2.5 || roll < 2.5) && (pitchRate < 0.3 && rollRate < 0.3)
        return false
    }
}
