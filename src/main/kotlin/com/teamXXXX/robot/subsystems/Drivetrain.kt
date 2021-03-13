package com.teamXXXX.robot.subsystems

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import com.teamXXXX.robot.Constants.DRIVETRAIN_CONTINUOUS_CURRENT_LIMIT
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import edu.wpi.first.wpilibj2.command.SubsystemBase
import com.teamXXXX.robot.Constants.DRIVETRAIN_LEFT_MAIN
import com.teamXXXX.robot.Constants.DRIVETRAIN_LEFT_SECONDARY
import com.teamXXXX.robot.Constants.DRIVETRAIN_PEAK_CURRENT_LIMIT
import com.teamXXXX.robot.Constants.DRIVETRAIN_PEAK_CURRENT_LIMIT_DURATION
import com.teamXXXX.robot.Constants.DRIVETRAIN_RIGHT_MAIN
import com.teamXXXX.robot.Constants.DRIVETRAIN_RIGHT_SECONDARY

object Drivetrain : SubsystemBase() {

    private val leftMain = WPI_TalonSRX(DRIVETRAIN_LEFT_MAIN)
    private val leftSecondary = WPI_TalonSRX(DRIVETRAIN_LEFT_SECONDARY)
    private val rightMain = WPI_TalonSRX(DRIVETRAIN_RIGHT_MAIN)
    private val rightSecondary = WPI_TalonSRX(DRIVETRAIN_RIGHT_SECONDARY)

    /** Computes left and right throttle from driver controller turn and throttle inputs. */
    private val differentialDrive = DifferentialDrive(leftMain, rightMain)

    /** Initializes motor configurations. */
    init {
        applyToMotors {
            configFactoryDefault()
            // Configure current limits to prevent motors stalling and overheating/breaking something or browning out the robot
            configContinuousCurrentLimit(DRIVETRAIN_CONTINUOUS_CURRENT_LIMIT)
            // Have a higher peak current limit for accelerating and starting, but it's only allowed for a short amount of time
            configPeakCurrentLimit(DRIVETRAIN_PEAK_CURRENT_LIMIT, DRIVETRAIN_PEAK_CURRENT_LIMIT_DURATION)
        }

        leftSecondary.follow(leftMain)
        rightSecondary.follow(rightMain)
    }

    /** Outputs [left] to the left motor, and [right] to the right motor. */
    fun rawDrive(left: Double, right: Double) {
        differentialDrive.tankDrive(left, right)
    }

    /**
     * Computes driver turn and throttle inputs and sets the motors.
     * @param turn The amount to turn, between -1 and 1.
     * @param throttle The amount to move, between -1 and 1.
     * @param quickTurn If true, the drivetrain will turn on a dime instead of also driving forwards.
     */
    fun cheesyDrive(turn: Double, throttle: Double, quickTurn: Boolean) {
        differentialDrive.curvatureDrive(throttle, turn, quickTurn)
    }

    /** Runs the provided [block] of code on each motor. */
    private fun applyToMotors(block: WPI_TalonSRX.() -> Unit) {
        for (motor in listOf(leftMain, leftSecondary, rightMain, rightSecondary)) {
            motor.apply(block)
        }
    }
}
