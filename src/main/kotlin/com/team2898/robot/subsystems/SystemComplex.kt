@file:Suppress("MemberVisibilityCanBePrivate", "ControlFlowWithEmptyBody")
// MemberVisibilityCanBePrivate warnings are designed to be visible from other classes, ControlFlowWithEmptyBody is used to be more human-readable
package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.Interpolation
import com.bpsrobotics.engine.utils.Meters
import com.bpsrobotics.engine.utils.RPM
import com.bpsrobotics.engine.utils.Seconds
import com.team2898.robot.Constants
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.SubsystemBase
import kotlin.math.abs

object SystemComplex : SubsystemBase() {
    private var LastShotInitTime: Seconds = Seconds(-100.0)
    val firstBall get() = Feed.ballDetector1.distanceCentimeters < 2.0
    val secondBall get() = Feed.ballDetector2.distanceCentimeters < 2.0
    val shooting get() = Feed.ballDetectorShooter.distanceCentimeters < 2.0
    val distance: Meters = Vision.distance
    var intakeCommand: Boolean = false // Modify in other systems
    var forceUse: Boolean = false
    var shootCommand: Boolean = false

    enum class RobotStates {
        N1N2,
        N1B2,
        B1N2,
        B1B2
    }

    val RobotState: RobotStates
        get() =
            run {if(firstBall) {
                if (secondBall) {
                    return@run RobotStates.B1B2
                } else {
                    return@run RobotStates.B1N2
                }
            }else{
                if (secondBall) {
                    return@run RobotStates.N1B2
                } else {
                    return@run RobotStates.N1N2
                }
            }
            }

    enum class IntakeStates {
        OPEN,
        ACTIVE,
        CLOSED
    }

    var intakeState = IntakeStates.CLOSED
    val ballCount
        get() = run {
            var ct = 0
            if (firstBall) {
                ct += 1
            }
            if (secondBall) {
                ct += 1
            }
            ct
        }

    fun shoot(distance: Meters) {
        LastShotInitTime = Seconds(Timer.getFPGATimestamp())
        val targetMotorSpeeds = Interpolation.interpolate(distance)
        Shooter.setRPM(targetMotorSpeeds.first, targetMotorSpeeds.second)
        val currentMotorSpeeds = Shooter.getRPM()
        if (
            abs(targetMotorSpeeds.first.value - currentMotorSpeeds.first.value) < Constants.SHOOTER_THRESHOLD &&
            abs(targetMotorSpeeds.second.value - currentMotorSpeeds.second.value) < Constants.SHOOTER_THRESHOLD
        ) {
            forceShoot()
        } else {
            Feed.changeState(Feed.Mode.IDLE)
        }
    }

    fun forceShoot() {
        LastShotInitTime = Seconds(Timer.getFPGATimestamp())
        Feed.changeState(Feed.Mode.SHOOT)
    }

    fun intakeBall() {
        if (ballCount <= 2) {
            forceIntake()
        }
    }

    fun forceIntake() {

    }

    fun putToShuffleboard() {
        SmartDashboard.putNumber("Number of Balls", ballCount.toDouble())
        SmartDashboard.putNumber("Accuracy", Interpolation.getAccuracy(distance))
        SmartDashboard.putString(
            "Intake State", when (intakeState) {
                IntakeStates.OPEN -> "open"
                IntakeStates.CLOSED -> "Closed"
                IntakeStates.ACTIVE -> "Active"
            }
        )
        SmartDashboard.putString(
            "Feeder State", when (Feed.state) {
                Feed.Mode.IDLE -> "Idle"
                Feed.Mode.SHOOT -> "Shooting"
                Feed.Mode.FEED -> "Feeding"
            }
        )

    }

    override fun periodic() {
        if (LastShotInitTime.value > Timer.getFPGATimestamp() + Constants.TIME_TO_SHOOT && !shooting) {
            Shooter.setRPM(RPM(0.0), RPM(0.0))
        }
        when (intakeState) {
            IntakeStates.ACTIVE -> {
                Intake.setOpenState(true)
                Intake.setIntake(true)
            }
            IntakeStates.OPEN -> {
                Intake.setOpenState(true)
                Intake.setIntake(false)
            }
            IntakeStates.CLOSED -> {
                Intake.setOpenState(false)
                Intake.setIntake(false)
            }
        }
        when (RobotState) {
            RobotStates.N1N2 -> {
                if (intakeCommand) {
                    intakeState = IntakeStates.ACTIVE
                } else if (intakeState == IntakeStates.ACTIVE) {
                    intakeState = IntakeStates.CLOSED
                }
                if (shootCommand) { // Ignore
                }
                Feed.changeState(Feed.Mode.IDLE)
            }
            RobotStates.N1B2 -> {
                Feed.changeState(Feed.Mode.FEED) // No inputs allowed, just load the ball into slot 1
            }
            RobotStates.B1N2 -> {
                if (intakeCommand && LastShotInitTime.value >= Timer.getFPGATimestamp() + Constants.TIME_TO_SHOOT) {
                    intakeState = IntakeStates.ACTIVE // IMPORTANT: Activate, but only if it didn't just shoot (to prevent 3 balls from simultaneously being in the robot
                } else if (intakeState == IntakeStates.ACTIVE) {
                    intakeState = IntakeStates.CLOSED
                }
                if (shootCommand) {
                    shoot(distance)
                } else {
                    Feed.changeState(Feed.Mode.IDLE)
                }
            }
            RobotStates.B1B2 -> {
                if (shootCommand) {
                    shoot(distance)
                } else {
                    Feed.changeState(Feed.Mode.IDLE)
                }
                if (intakeCommand) { // Ignore
                }
            }
        }
        putToShuffleboard()
        if(shootCommand && forceUse){
            shoot(distance)
        }
        if(intakeCommand && forceUse){
            intakeState = IntakeStates.ACTIVE
        }
    }
}