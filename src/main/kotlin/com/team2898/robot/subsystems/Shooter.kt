package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.Interpolation
import com.bpsrobotics.engine.utils.RPM
import com.bpsrobotics.engine.utils.minus
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMax.ControlType
import com.revrobotics.CANSparkMaxLowLevel
import com.team2898.robot.Constants
import com.team2898.robot.Constants.EJECT_SPEED
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.SubsystemBase
import kotlin.math.abs

object Shooter : SubsystemBase() {
    // TODO: constants
    private val shooterMotor = CANSparkMax(1, CANSparkMaxLowLevel.MotorType.kBrushless)
    private val spinnerMotor = CANSparkMax(2, CANSparkMaxLowLevel.MotorType.kBrushless)
    private var shooterGoal = 0.0
    private var spinnerGoal = 0.0
    private var spinUpCommand = false
    private var spinUpDumpCommand = false
    private var lastShotTime = 0.0

    init {
        // TODO Tune FF and PID loops, set constants
        shooterMotor.pidController.p = 10.0
        shooterMotor.pidController.ff = 2.5

        spinnerMotor.pidController.p = 10.0
        shooterMotor.pidController.ff = 2.0
    }

    fun shoot(enabled: Boolean){
        spinUpCommand = enabled
    }
    fun dump(enabled: Boolean){
        spinUpDumpCommand = enabled
    }


    enum class ShooterStates {
        IDLE,
        ENABLED,
        READY,
        DUMP_ENABLED,
        DUMP_READY
    }

    var state = ShooterStates.IDLE
        private set

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
    }

    fun enable() {
        isDisabled = false
    }

    override fun periodic() {
//        val vel = max(shooterMotor.encoder.velocity.absoluteValue, spinnerMotor.encoder.velocity.absoluteValue)
        if (!isDisabled) {
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
            ShooterStates.IDLE -> {
                if (spinUpCommand) {
                    state = ShooterStates.ENABLED
                }
                if (spinUpDumpCommand) {
                    state = ShooterStates.DUMP_ENABLED
                }
            }
            ShooterStates.ENABLED -> {
                val targetMotorSpeeds = Interpolation.getRPMs()
                setRPM(targetMotorSpeeds.first, targetMotorSpeeds.second)
                if (abs((getRPM().first - targetMotorSpeeds.first).value) > Constants.SHOOTER_THRESHOLD || abs((getRPM().second - targetMotorSpeeds.second).value) > Constants.SHOOTER_THRESHOLD) {
                    state = ShooterStates.READY
                }
                if (lastShotTime < Timer.getFPGATimestamp() - 5.0){
                    state = ShooterStates.IDLE
                }
            }
            ShooterStates.READY -> {
                val targetMotorSpeeds = Interpolation.getRPMs()
                setRPM(targetMotorSpeeds.first, targetMotorSpeeds.second)
                if (abs((getRPM().first - targetMotorSpeeds.first).value) > Constants.SHOOTER_THRESHOLD || abs((getRPM().second - targetMotorSpeeds.second).value) > Constants.SHOOTER_THRESHOLD) {
                } else {
                    state = ShooterStates.ENABLED
                }
                if (lastShotTime < Timer.getFPGATimestamp() - 5.0){
                    state = ShooterStates.IDLE
                }
            }
            ShooterStates.DUMP_ENABLED -> {
                setRPM(EJECT_SPEED.first, EJECT_SPEED.second)
                if (abs((getRPM().first - EJECT_SPEED.first).value) > Constants.SHOOTER_THRESHOLD || abs((getRPM().second - EJECT_SPEED.second).value) > Constants.SHOOTER_THRESHOLD) {
                    state = ShooterStates.READY
                }
                if (lastShotTime < Timer.getFPGATimestamp() - 5.0){
                    state = ShooterStates.IDLE
                }
            }
            ShooterStates.DUMP_READY -> {
                setRPM(EJECT_SPEED.first, EJECT_SPEED.second)
                if (abs((getRPM().first - EJECT_SPEED.first).value) > Constants.SHOOTER_THRESHOLD || abs((getRPM().second - EJECT_SPEED.second).value) > Constants.SHOOTER_THRESHOLD) {
                } else {
                    state = ShooterStates.ENABLED
                }
                if (lastShotTime < Timer.getFPGATimestamp() - 5.0){
                    state = ShooterStates.IDLE
                }
            }        }
        if (spinUpCommand || spinUpDumpCommand){
            lastShotTime = Timer.getFPGATimestamp()
        }
    }
}