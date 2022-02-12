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
    enum class IntakeStates{
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

    fun Shoot(distance: Meters) {
        LastShotInitTime = Seconds(Timer.getFPGATimestamp())
        val targetMotorSpeeds = Interpolation.interpolate(distance)
        Shooter.setRPM(targetMotorSpeeds.first, targetMotorSpeeds.second)
        val currentMotorSpeeds = Shooter.getRPM()
        if (
            abs(targetMotorSpeeds.first.value - currentMotorSpeeds.first.value) < Constants.SHOOTER_THRESHOLD &&
            abs(targetMotorSpeeds.second.value - currentMotorSpeeds.second.value) < Constants.SHOOTER_THRESHOLD
        ) {
            forceShoot()
        }
    }

    fun forceShoot() {
        LastShotInitTime = Seconds(Timer.getFPGATimestamp())
        Feed.changeState(Feed.Mode.SHOOT)
    }

    fun intakeBall() {
        if(ballCount <= 2){
            forceIntake()
        }
    }

    fun forceIntake(){

    }

    fun putToShuffleboard(){
        SmartDashboard.putNumber("Number of Balls", ballCount.toDouble())
        SmartDashboard.putNumber("Accuracy", Interpolation.getAccuracy(distance)))
        SmartDashboard.putString("Intake State", when(intakeState){
            IntakeStates.OPEN -> "open"
            IntakeStates.CLOSED -> "Closed"
            IntakeStates.ACTIVE -> "Active"
        })
        SmartDashboard.putString("Feeder State", when(Feed.state){
            Feed.Mode.IDLE -> "Idle"
            Feed.Mode.SHOOT -> "Shooting"
            Feed.Mode.FEED -> "Feeding"
        })

    }
    override fun periodic() {
        if (LastShotInitTime.value > Timer.getFPGATimestamp() + Constants.TIME_TO_SHOOT && !shooting) {
            Shooter.setRPM(RPM(0.0), RPM(0.0))
        }
        if (secondBall && !firstBall && !shooting && Feed.state != Feed.Mode.SHOOT) {
            Feed.changeState(Feed.Mode.FEED)
        }
        when(intakeState){
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
        putToShuffleboard()
    }
}