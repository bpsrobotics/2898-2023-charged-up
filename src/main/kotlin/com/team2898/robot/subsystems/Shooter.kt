package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.Interpolation
import com.bpsrobotics.engine.utils.RPM
import com.bpsrobotics.engine.utils.minus
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMax.ControlType
import com.revrobotics.CANSparkMaxLowLevel
import com.team2898.robot.Constants
import com.team2898.robot.OI
import edu.wpi.first.wpilibj2.command.SubsystemBase
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.max

object Shooter : SubsystemBase() {
    // TODO: constants
    private val shooterMotor = CANSparkMax(1, CANSparkMaxLowLevel.MotorType.kBrushless)
    private val spinnerMotor = CANSparkMax(2, CANSparkMaxLowLevel.MotorType.kBrushless)
    private var shooterGoal = 0.0
    private val shooterActual by OI.Ramp.ramp(60.0) { shooterGoal }
    private var spinnerGoal = 0.0
    private val spinnerActual by OI.Ramp.ramp(60.0) { spinnerGoal }
    private val shootCommand: Boolean get() = TODO() // TODO: Place every possible method of activating shooter here

    init {
        // apply 5 volts if it's off by 15 rev/sec
        val kP = (5.0 / 12) / (15 * 60)
        val kD = kP / 5

        listOf(shooterMotor, spinnerMotor).forEach {
            it.restoreFactoryDefaults()
            it.pidController.p = kP
//            it.pidController.i = 0.000001
            it.pidController.d = kD
            it.pidController.ff = 0.00021
//            it.pidController.setOutputRange(-0.3, 0.3)
        }
    }

    enum class ShooterStates {
        IDLE,
        SPINUP,
        READY,
        REQUIRESRELEASE,
    }

    var state = ShooterStates.IDLE

    fun setRPM(shooterSpeed: RPM, spinnerSpeed: RPM) {
        if (isDisabled) return
        shooterGoal = shooterSpeed.value
        spinnerGoal = spinnerSpeed.value
//        controllerA.pidController.setReference(speed, ControlType.kVelocity)
//        controllerB.pidController.setReference(-speed, ControlType.kVelocity)
    }

    fun getRPM(): Pair<RPM, RPM> {
        return Pair(RPM(shooterMotor.encoder.velocity), RPM(spinnerMotor.encoder.velocity))
    }

    private var isDisabled = false

    fun disable() {
        isDisabled = true
        shooterMotor.disable()
        spinnerMotor.disable()
    }

    fun reEnable() {
        isDisabled = false
    }

    override fun periodic() {
        val vel = max(shooterMotor.encoder.velocity.absoluteValue, spinnerMotor.encoder.velocity.absoluteValue)
        if (!isDisabled) {
            shooterMotor.pidController.setReference(shooterActual, ControlType.kVelocity)
            spinnerMotor.pidController.setReference(-spinnerActual, ControlType.kVelocity)
            if (vel > (40.0 * 60)) {
                println("RPM is $vel, over threshold, stopping")
                disable()
                return
            } else if (max(shooterMotor.motorTemperature, spinnerMotor.motorTemperature) > 40.0) {
                println(
                    "Max motor temperature reached (${
                        max(
                            shooterMotor.motorTemperature,
                            spinnerMotor.motorTemperature
                        )
                    }), stopping"
                )
                disable()
                return
            }
            println(vel)
        } else {
            shooterMotor.pidController.p = 0.0
            shooterMotor.pidController.i = 0.0
            shooterMotor.pidController.d = 0.0
            shooterMotor.pidController.ff = 0.0

            spinnerMotor.pidController.p = 0.0
            spinnerMotor.pidController.i = 0.0
            spinnerMotor.pidController.d = 0.0
            spinnerMotor.pidController.ff = 0.0

            shooterMotor.set(0.0)
            spinnerMotor.set(0.0)
        }
        when (state) {
            ShooterStates.IDLE -> if (shootCommand) {
                state = ShooterStates.SPINUP
            }
            ShooterStates.SPINUP -> {
                val targetMotorSpeeds = Interpolation.interpolate(Interpolation.targetDistance)
                setRPM(targetMotorSpeeds.first, targetMotorSpeeds.second)
                if (abs((getRPM().first - targetMotorSpeeds.first).value) > Constants.SHOOTER_THRESHOLD || abs((getRPM().second - targetMotorSpeeds.second).value) > Constants.SHOOTER_THRESHOLD) {
                    state = ShooterStates.READY
                }
            }
            ShooterStates.READY -> {
                if (shootCommand) {
                    Feed.shoot()
                    state = ShooterStates.REQUIRESRELEASE
                }
            }
            ShooterStates.REQUIRESRELEASE -> {
                if (!shootCommand) {
                    state = ShooterStates.IDLE
                }
            }
        }
    }
}