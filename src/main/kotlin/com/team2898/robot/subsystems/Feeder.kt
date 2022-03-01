package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.minus
import com.bpsrobotics.engine.utils.seconds
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import com.cuforge.libcu.Lasershark
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMax.IdleMode.kBrake
import com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless
import com.team2898.robot.Constants.FEEDER_MAX_DISTANCE
import com.team2898.robot.Constants.FEEDER_MIN_DISTANCE
import com.team2898.robot.Constants.FEEDER_SPEED
import com.team2898.robot.Constants.FEEDER_VECTOR_SPEED
import com.team2898.robot.Constants.TIME_TO_SHOOT
import com.team2898.robot.DriverDashboard
import com.team2898.robot.RobotMap.FEEDER_LASERSHARK
import com.team2898.robot.RobotMap.FEEDER_LEFT_VECTOR
import com.team2898.robot.RobotMap.FEEDER_RIGHT_VECTOR
import com.team2898.robot.RobotMap.FEEDER_UPPER
import com.team2898.robot.subsystems.Feeder.State.*
import edu.wpi.first.util.sendable.SendableBuilder
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Feeder : SubsystemBase() {
    private val mainLasershark = Lasershark(FEEDER_LASERSHARK)

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
    private val RUN_RANGE = FEEDER_MIN_DISTANCE..FEEDER_MAX_DISTANCE

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

    init {
        listOf(vectorLeft, vectorRight).forEach {
            it.setSmartCurrentLimit(10, 10)
            it.idleMode = kBrake
        }
        feederMotor.configFactoryDefault()
        feederMotor.setNeutralMode(NeutralMode.Brake)
        feederMotor.configContinuousCurrentLimit(10)
    }

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
                vector.set(FEEDER_VECTOR_SPEED)
                feederMotor.set(FEEDER_SPEED)
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

                vector.set(FEEDER_VECTOR_SPEED)
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
                vector.set(FEEDER_VECTOR_SPEED)
                feederMotor.set(FEEDER_SPEED)

                // if it's been in the shooting state for long enough switch to empty
                // will switch again if there's another ball
                if ((Timer.getFPGATimestamp().seconds - stateStartTime).value > TIME_TO_SHOOT) {
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

    fun forceShoot() {
        state = SHOOTING
    }

    override fun initSendable(builder: SendableBuilder) {
        builder.setSmartDashboardType("Subsystem")
        builder.addDoubleProperty("lasershark distance", mainLasershark::getDistanceMeters) {}
        builder.addStringProperty("state", { state.toString() }) {}
    }
}
