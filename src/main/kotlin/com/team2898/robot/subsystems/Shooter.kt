package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.*
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMax.ControlType
import com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless
import com.team2898.robot.Constants
import com.team2898.robot.DriverDashboard
import com.team2898.robot.RobotMap.SHOOTER_FLYWHEEL
import com.team2898.robot.RobotMap.SHOOTER_SPINNER
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.SubsystemBase
import kotlin.math.absoluteValue
import kotlin.math.max

object Shooter : SubsystemBase() {
    private val shooterMotor = CANSparkMax(SHOOTER_FLYWHEEL, kBrushless)
    private val spinnerMotor = CANSparkMax(SHOOTER_SPINNER, kBrushless)
    private var shooterGoal = 0.RPM
    private var spinnerGoal = 0.RPM
    private var lastShotTime = 0.0

    private var overrideMeters = 0.m
    private var overridePower = ShooterSpeeds(0.RPM, 0.RPM)

    data class ShooterSpeeds(val top: RPM, val bottom: RPM)

    init {
        // TODO Tune FF and PID loops, set constants
        shooterMotor.pidController.p = 0.0
        shooterMotor.pidController.ff = 0.5

        spinnerMotor.pidController.p = 0.0
        shooterMotor.pidController.ff = 0.0
    }

    fun spinUp() {
        overrideMeters = 0.0.m
        overridePower = ShooterSpeeds(0.RPM, 0.RPM)
        lastShotTime = Timer.getFPGATimestamp()
        state = ShooterStates.SPINUP
        val speeds = Interpolation.getRPMs()
        setGoals(speeds)
    }

    fun spinUp(distance: Meters) {
        overrideMeters = distance
        overridePower = ShooterSpeeds(0.RPM, 0.RPM)
        lastShotTime = Timer.getFPGATimestamp()
        state = ShooterStates.SPINUP
        val speeds = Interpolation.getRPMs(distance)
        setGoals(speeds)
    }

    fun spinUp(speeds: ShooterSpeeds) {
        overridePower = speeds
        overrideMeters = 0.m
        lastShotTime = Timer.getFPGATimestamp()
        state = ShooterStates.SPINUP
        setGoals(speeds)
    }

    fun dumpSpinUp() {
        lastShotTime = Timer.getFPGATimestamp()
        state = ShooterStates.DUMP
        setGoals(Constants.DUMP_SPEED)
    }

    fun stopShooter() {
        state = ShooterStates.IDLE
    }


    enum class ShooterStates {
        IDLE,
        SPINUP,
        READY,
        DUMP
    }

    var state = ShooterStates.IDLE
        private set

    private fun setGoals(speeds: ShooterSpeeds) {
        shooterGoal = speeds.top
        spinnerGoal = speeds.bottom
    }

    fun getRPM(): ShooterSpeeds {
        return ShooterSpeeds(RPM(shooterMotor.encoder.velocity), RPM(spinnerMotor.encoder.velocity))
    }

    override fun periodic() {
//        val vel = max(shooterMotor.encoder.velocity.absoluteValue, spinnerMotor.encoder.velocity.absoluteValue)
        if (state != ShooterStates.IDLE) {
            shooterMotor.pidController.setReference(shooterGoal.value, ControlType.kVelocity)
            spinnerMotor.pidController.setReference(-spinnerGoal.value, ControlType.kVelocity)
//            if (vel > (40.0 * 60)) {
//                println("RPM is $vel, over threshold, stopping")
//                disable()
//                return
//            } else if (max(shooterMotor.motorTemperature, spinnerMotor.motorTemperature) > 40.0) {
//                println(
//                    "Max motor temperature reached (${
//                        max(
//                            shooterMotor.motorTemperature,
//                            spinnerMotor.motorTemperature
//                        )
//                    }), stopping"
//                )
//                disable()
//                return
//            }
//            println(vel)
        } else {
            shooterMotor.stopMotor()
            spinnerMotor.stopMotor()
        }

        when (state) {
            ShooterStates.IDLE -> {
                DriverDashboard.string("Shooter State", "Idle")
            }
            ShooterStates.SPINUP -> {
                val speeds = if (overrideMeters.value >= 0.1) {
                    Interpolation.getRPMs(overrideMeters)
                } else if (overridePower.top.value != 0.0 || overridePower.bottom.value != 0.0) {
                    overridePower
                } else {
                    Interpolation.getRPMs()
                }
                setGoals(speeds)

                val shooterDiff = getRPM().top - shooterGoal
                // TODO: sign
                val spinnerDiff = getRPM().bottom - spinnerGoal
                if (max(spinnerDiff.value.absoluteValue, shooterDiff.value.absoluteValue) < Constants.SHOOTER_THRESHOLD) {
                    state = ShooterStates.READY
                }
                if (lastShotTime < Timer.getFPGATimestamp() - 5.0) {
                    state = ShooterStates.IDLE
                }
                DriverDashboard.string("Shooter State", "Spinup")
            }
            ShooterStates.READY -> {
                val speeds = if (overrideMeters.value >= 0.1) {
                    Interpolation.getRPMs(overrideMeters)
                } else if (overridePower.top.value != 0.0 || overridePower.bottom.value != 0.0) {
                    overridePower
                } else {
                    Interpolation.getRPMs()
                }
                setGoals(speeds)

                val shooterDiff = getRPM().top - shooterGoal
                // TODO: sign
                val spinnerDiff = getRPM().bottom - spinnerGoal
                if (max(spinnerDiff.value.absoluteValue, shooterDiff.value.absoluteValue) > Constants.SHOOTER_THRESHOLD) {
                    state = ShooterStates.SPINUP
                }
                if (lastShotTime < Timer.getFPGATimestamp() - 5.0) {
                    state = ShooterStates.IDLE
                }
                DriverDashboard.string("Shooter State", "Ready")
            }
            ShooterStates.DUMP -> {
                if (lastShotTime < Timer.getFPGATimestamp() - 5.0) {
                    state = ShooterStates.IDLE
                }
                DriverDashboard.string("Shooter State", "Dump")
            }
        }
        DriverDashboard.number("Shooter Target", shooterGoal.value)
        DriverDashboard.number("Spinner Target", spinnerGoal.value)
        DriverDashboard.number("Shooter Speed", getRPM().top.value)
        DriverDashboard.number("Spinner Speed", getRPM().bottom.value)
        DriverDashboard.boolean("Shooter up to speed", state == ShooterStates.READY)
    }
}
