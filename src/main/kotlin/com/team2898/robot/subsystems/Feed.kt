package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.minus
import com.bpsrobotics.engine.utils.seconds
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import com.cuforge.libcu.Lasershark
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless
import com.team2898.robot.DriverDashboard
import com.team2898.robot.subsystems.Feed.State.*
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup
import edu.wpi.first.wpilibj2.command.SubsystemBase

// TODO: merge with intake?  Otherwise add special state that the intake
// puts it into to run the vectoring wheels while intaking the first ball

object Feed : SubsystemBase() {
    private val mainLasershark = Lasershark(1)  // TODO: port

    // unused at the moment
    /* private val ballDetector1 = Lasershark(Constants.FEEDER_BALL_DETECTOR_1)
    private val ballDetectorShooter = Lasershark(Constants.FEEDER_BALL_DETECTOR_SHOOTER)
    private val ballDetector2 = Lasershark(Constants.FEEDER_BALL_DETECTOR_2) */

    private val vectorLeft = CANSparkMax(1, kBrushless)  // TODO: id
    private val vectorRight = CANSparkMax(2, kBrushless)  // TODO: id
    private val vector = MotorControllerGroup(vectorLeft, vectorRight)

    private val feederMotor = WPI_TalonSRX(3) // TODO: id

    // public so that things can make decisions based on if it's ready or not
    var state = EMPTY
        private set(value) { // not settable from outside the object
            if (value == SHOOTING) shootStartTime = Timer.getFPGATimestamp().seconds
            field = value
        }

    /** The reading range in which the feeder will run */
    private val RUN_RANGE = 0.2..1.0

    enum class State {
        EMPTY, READY, FEEDING, SHOOTING
    }

    private var shootStartTime = 0.seconds

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
                vector.set(1.0)  // TODO speed
                feederMotor.set(1.0)

                if (distance < RUN_RANGE.start) {
                    state = READY
                } else if (distance > RUN_RANGE.endInclusive) {
                    state = EMPTY
                }
                DriverDashboard.string("Feeder State", "Feeding")
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
                if ((Timer.getFPGATimestamp().seconds - shootStartTime).value > 0.5) {  // TODO: constant
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
