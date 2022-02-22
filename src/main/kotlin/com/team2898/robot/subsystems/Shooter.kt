package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.Interpolation
import com.bpsrobotics.engine.utils.RPM
import com.bpsrobotics.engine.utils.minus
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMax.ControlType
import com.revrobotics.CANSparkMaxLowLevel
import com.team2898.robot.Constants
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.SubsystemBase
import kotlin.math.absoluteValue
import kotlin.math.max

object Shooter : SubsystemBase() {
    // TODO: constants
    private val shooterMotor = CANSparkMax(1, CANSparkMaxLowLevel.MotorType.kBrushless)
    private val spinnerMotor = CANSparkMax(2, CANSparkMaxLowLevel.MotorType.kBrushless)
    private var shooterGoal = 0.0
    private var spinnerGoal = 0.0
    private var lastShotTime = 0.0

    init {
        // TODO Tune FF and PID loops, set constants
        shooterMotor.pidController.p = 10.0
        shooterMotor.pidController.ff = 2.5

        spinnerMotor.pidController.p = 10.0
        shooterMotor.pidController.ff = 2.0
    }

    fun shoot() {
        lastShotTime = Timer.getFPGATimestamp()
        state = ShooterStates.SPINUP
        val speeds = Interpolation.getRPMs()
        setGoals(speeds.first, speeds.second)
    }

    fun dump() {
        lastShotTime = Timer.getFPGATimestamp()
        state = ShooterStates.SPINUP
        setGoals(Constants.EJECT_SPEED.first, Constants.EJECT_SPEED.second)
    }


    enum class ShooterStates {
        IDLE,
        SPINUP,
        READY,
        DUMP
    }

    var state = ShooterStates.IDLE
        private set

    private fun setGoals(shooterSpeed: RPM, spinnerSpeed: RPM) {
        shooterGoal = shooterSpeed.value
        spinnerGoal = spinnerSpeed.value
    }

    fun getRPM(): Pair<RPM, RPM> {
        return Pair(RPM(shooterMotor.encoder.velocity), RPM(spinnerMotor.encoder.velocity))
    }

    override fun periodic() {
//        val vel = max(shooterMotor.encoder.velocity.absoluteValue, spinnerMotor.encoder.velocity.absoluteValue)
        if (state != ShooterStates.IDLE) {
            shooterMotor.pidController.setReference(shooterGoal, ControlType.kVelocity)
            spinnerMotor.pidController.setReference(-spinnerGoal, ControlType.kVelocity)
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
            ShooterStates.IDLE -> {}
            ShooterStates.SPINUP -> {
                val speeds = Interpolation.getRPMs()
                setGoals(speeds.first, speeds.second)

                val shooterDiff = getRPM().first - shooterGoal.RPM
                // TODO: sign
                val spinnerDiff = getRPM().second - spinnerGoal.RPM
                if (max(spinnerDiff.value.absoluteValue, shooterDiff.value.absoluteValue) < Constants.SHOOTER_THRESHOLD) {
                    state = ShooterStates.READY
                }
                if (lastShotTime < Timer.getFPGATimestamp() - 5.0) {
                    state = ShooterStates.IDLE
                }
            }
            ShooterStates.READY -> {
                val speeds = Interpolation.getRPMs()
                setGoals(speeds.first, speeds.second)

                val shooterDiff = getRPM().first - shooterGoal.RPM
                // TODO: sign
                val spinnerDiff = getRPM().second - spinnerGoal.RPM
                if (max(spinnerDiff.value.absoluteValue, shooterDiff.value.absoluteValue) > Constants.SHOOTER_THRESHOLD) {
                    state = ShooterStates.SPINUP
                }
                if (lastShotTime < Timer.getFPGATimestamp() - 5.0) {
                    state = ShooterStates.IDLE
                }
            }
            ShooterStates.DUMP -> {
                if (lastShotTime < Timer.getFPGATimestamp() - 5.0) {
                    state = ShooterStates.IDLE
                }
            }
        }
    }
}
