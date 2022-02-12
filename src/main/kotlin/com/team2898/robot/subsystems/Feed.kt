package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.Meters
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import com.cuforge.libcu.Lasershark
import com.team2898.robot.Constants
import edu.wpi.first.wpilibj2.command.SubsystemBase
object Feed : SubsystemBase() {
    val ballDetector1 = Lasershark(Constants.FEEDER_BALL_DETECTOR_1)
    val ballDetectorShooter = Lasershark(Constants.FEEDER_BALL_DETECTOR_SHOOTER)
    val ballDetector2 = Lasershark(Constants.FEEDER_BALL_DETECTOR_2)
    // x and y are the CAN IDs of the sparks
    private val topMotor = WPI_TalonSRX(Constants.FEEDER_TOP_MOTOR)
    private val bottomMotor = WPI_TalonSRX(Constants.FEEDER_BOTTOM_MOTOR)

    enum class Mode {
        IDLE, FEED, SHOOT
    }

    var state = Mode.IDLE

    private fun idleMotors() {
        topMotor.set(0.0)
        bottomMotor.set(0.0)
    }

    // we can determine the top motor's speed and the bottom motor's speed with some testing
    private fun feedBall() {
        if (ballDetector1.getDistanceCentimeters()>2.0 && ballDetector2.getDistanceCentimeters()<2.0){
            topMotor.set(Constants.TOP_SPEED)
            bottomMotor.set(Constants.BOTTOM_SPEED)
        }
        else {
            topMotor.set(0.0)
            bottomMotor.set(0.0)
            state = Mode.IDLE
        }
    }

    fun shootBall() {
        if (ballDetector1.getDistanceCentimeters()<2.0 && ballDetectorShooter.getDistanceCentimeters()<2.0){
            topMotor.set(Constants.TOP_SPEED)
            bottomMotor.set(Constants.BOTTOM_SPEED)
        }
        else {
            topMotor.set(0.0)
            bottomMotor.set(0.0)
            state = Mode.IDLE
        }
    }

    fun changeState(newState: Mode) {
        if (newState != state) {
            state = newState
        }
    }

    override fun periodic() {
        when (state) {
            Mode.IDLE -> {
                idleMotors()
            }
            Mode.FEED -> {
                feedBall()
            }
            Mode.SHOOT -> {
                shootBall()
            }
        }
    }
 }