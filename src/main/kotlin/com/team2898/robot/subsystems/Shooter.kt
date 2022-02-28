package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.RPM
import com.bpsrobotics.engine.utils.plus
import com.bpsrobotics.engine.utils.seconds
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMax.ControlType.kVelocity
import com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless
import com.team2898.robot.Constants.DUMP_SPEED
import com.team2898.robot.RobotMap.SHOOTER_FLYWHEEL
import com.team2898.robot.RobotMap.SHOOTER_SPINNER
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.SubsystemBase
import kotlin.math.absoluteValue

object Shooter : SubsystemBase() {
    private val flywheelController = CANSparkMax(SHOOTER_FLYWHEEL, kBrushless)
    private val spinnerController = CANSparkMax(SHOOTER_SPINNER, kBrushless)
    private var spunUpTime = 0.seconds
    val ready get() =
        (flywheelController.encoder.velocity - DUMP_SPEED.flywheel.value).absoluteValue < 10 &&
                (spinnerController.encoder.velocity - DUMP_SPEED.spinner.value).absoluteValue < 10

    data class ShooterSpeeds(val flywheel: RPM, val spinner: RPM)

    init {
        listOf(flywheelController, spinnerController).forEach {
            it.restoreFactoryDefaults()
            it.setSmartCurrentLimit(20)
        }

        flywheelController.pidController.ff = 0.0
        flywheelController.pidController.p = 0.0
        flywheelController.pidController.i = 0.0
        flywheelController.pidController.d = 0.0

        spinnerController.pidController.ff = 0.0
        spinnerController.pidController.p = 0.0
        spinnerController.pidController.i = 0.0
        spinnerController.pidController.d = 0.0
    }

    fun spinUp() {
        spunUpTime = Timer.getFPGATimestamp().seconds + 5.seconds
    }

    override fun periodic() {
        if (Timer.getFPGATimestamp() < spunUpTime.value) {
            flywheelController.pidController.setReference(DUMP_SPEED.flywheel.value, kVelocity)
            spinnerController.pidController.setReference(DUMP_SPEED.spinner.value, kVelocity)
        } else {
            flywheelController.set(0.0)
            spinnerController.set(0.0)
        }
    }
}
