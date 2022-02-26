package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.minus
import com.bpsrobotics.engine.utils.seconds
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import com.cuforge.libcu.Lasershark
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless
import com.team2898.robot.DriverDashboard
import com.team2898.robot.RobotMap.FEEDER_LEFT_VECTOR
import com.team2898.robot.RobotMap.FEEDER_RIGHT_VECTOR
import com.team2898.robot.RobotMap.FEEDER_UPPER
import com.team2898.robot.subsystems.Feed.State.*
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Feed : SubsystemBase() {
    private val mainLasershark = Lasershark(1)  // TODO: port

    // unused at the moment
    /* private val ballDetector1 = Lasershark(Constants.FEEDER_BALL_DETECTOR_1)
    private val ballDetectorShooter = Lasershark(Constants.FEEDER_BALL_DETECTOR_SHOOTER)
    private val ballDetector2 = Lasershark(Constants.FEEDER_BALL_DETECTOR_2) */

    private val vectorLeft = CANSparkMax(FEEDER_LEFT_VECTOR, kBrushless)
    private val vectorRight = CANSparkMax(FEEDER_RIGHT_VECTOR, kBrushless)
    private val vector = MotorControllerGroup(vectorLeft, vectorRight)

    private val feederMotor = WPI_TalonSRX(FEEDER_UPPER)

    // public so that things can make decisions based on if it's ready or not
    var state = EMPTY
        private set(value) { // not settable from outside the object
            stateStartTime = Timer.getFPGATimestamp().seconds
            field = value
        }

    /** The reading range in which the feeder will run */
    private val RUN_RANGE = 0.2..1.0

    enum class State {
        EMPTY, READY, FEEDING, SHOOTING, INTAKING
    }

    private var stateStartTime = 0.seconds

    fun intake() {
        state = INTAKING
    }

    fun stopIntaking() {
        if (state == INTAKING) state = EMPTY  // switches to the right one automatically
    }

    // TODO: current limits

    override fun periodic() {
        val distance = mainLasershark.distanceMeters
        when (state) {
            EMPTY -> {
                vector.set(0.0)
                feederMotor.set(0.0)

                if (distance in RUN_RANGE) {
                    state = FEEDING
                }
                DriverDashboard.string("Feeder State", "Empty")
            }
            FEEDING -> {
                DriverDashboard.string("Feeder State", "Feeding")
                if (distance < RUN_RANGE.start) {
                    state = READY
                    return
                } else if (distance > RUN_RANGE.endInclusive) {
                    state = EMPTY
                    return
                }
                vector.set(1.0)  // TODO speed
                feederMotor.set(1.0)
            }
            INTAKING -> {
                DriverDashboard.string("Feeder State", "Feeding")

//                if (Timer.getFPGATimestamp() - stateStartTime.value > 1.0) {
//                    state = EMPTY  // will automatically switch out of state if it isn't empty
//                    return
//                }

                if (distance < RUN_RANGE.start) {
                    state = READY
                    return
                }

                vector.set(1.0)  // TODO speed
                feederMotor.set(0.0)

                if (distance < RUN_RANGE.endInclusive) {
                    state = FEEDING
                }
            }
            READY -> {
                vector.set(0.0)
                feederMotor.set(0.0)

                // has no automatic transition
                DriverDashboard.string("Feeder State", "Ready")
            }
            SHOOTING -> {
                // TODO speeds
                vector.set(1.0)
                feederMotor.set(1.0)

                // if it's been in the shooting state for long enough switch to empty
                // will switch again if there's another ball
                if ((Timer.getFPGATimestamp().seconds - stateStartTime).value > 0.5) {  // TODO: constant
                    state = EMPTY
                    DriverDashboard.string("Feeder State", "Shooting")
                }
            }
        }
    }

    fun shoot() {
        if (state == READY) {
            state = SHOOTING
        }
    }
}
