package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.RPM
import com.bpsrobotics.engine.utils.plus
import com.bpsrobotics.engine.utils.seconds
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMax.ControlType.kVelocity
import com.revrobotics.CANSparkMax.IdleMode.kCoast
import com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless
import com.team2898.robot.Constants.DUMP_SPEED
import com.team2898.robot.RobotMap.SHOOTER_FLYWHEEL
import com.team2898.robot.RobotMap.SHOOTER_SPINNER
import edu.wpi.first.util.sendable.SendableBuilder
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.SubsystemBase
import kotlin.math.absoluteValue

object Shooter : SubsystemBase() {
    private val flywheelController = CANSparkMax(SHOOTER_FLYWHEEL, kBrushless)
    private val spinnerController = CANSparkMax(SHOOTER_SPINNER, kBrushless)
    private var spunUpTime = 0.seconds
    val ready get() =
        (flywheelController.encoder.velocity - target.flywheel.value).absoluteValue < 10 &&
                (spinnerController.encoder.velocity - target.spinner.value).absoluteValue < 10
    var target = ShooterSpeeds(0.RPM, 0.RPM)

    data class ShooterSpeeds(val flywheel: RPM, val spinner: RPM)

    init {
        listOf(flywheelController, spinnerController).forEach {
            it.restoreFactoryDefaults()
            it.setSmartCurrentLimit(20)
            it.idleMode = kCoast
            it.inverted = true
        }

//        flywheelController.pidController.ff = 0.0
//        flywheelController.pidController.p = 0.0
//        flywheelController.pidController.i = 0.0
//        flywheelController.pidController.d = 0.0
//
//        spinnerController.pidController.ff = 0.0
//        spinnerController.pidController.p = 0.0
//        spinnerController.pidController.i = 0.0
//        spinnerController.pidController.d = 0.0
    }

    fun spinUp(speeds: ShooterSpeeds = DUMP_SPEED) {
        spunUpTime = Timer.getFPGATimestamp().seconds + 5.seconds
        target = speeds
    }

    override fun periodic() {
        if (Timer.getFPGATimestamp() < spunUpTime.value) {
            flywheelController.set(0.3)
            spinnerController.set(0.1)
//            flywheelController.pidController.setReference(target.flywheel.value, kVelocity)
//            spinnerController.pidController.setReference(target.spinner.value, kVelocity)
        } else {
            flywheelController.set(0.0)
            spinnerController.set(0.0)
        }
    }

    override fun initSendable(builder: SendableBuilder) {
        builder.setSmartDashboardType("Subsystem")
        builder.addDoubleProperty("shooter RPM", { flywheelController.encoder.velocity }) {}
        builder.addDoubleProperty("spinner RPM", { spinnerController.encoder.velocity }) {}
        builder.addDoubleProperty("shooter target", { target.flywheel.value }) {}
        builder.addDoubleProperty("spinner target", { target.spinner.value }) {}
    }
}
