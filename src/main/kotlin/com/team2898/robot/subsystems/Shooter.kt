package com.team2898.robot.subsystems

import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import com.revrobotics.ControlType
import com.team2898.robot.OI
import edu.wpi.first.wpilibj2.command.SubsystemBase
import kotlin.math.absoluteValue
import kotlin.math.max

object Shooter : SubsystemBase() {
    // TODO: constants
    private val controllerA = CANSparkMax(1, CANSparkMaxLowLevel.MotorType.kBrushless)
    private val controllerB = CANSparkMax(2, CANSparkMaxLowLevel.MotorType.kBrushless)
    private var goal = 0.0
    private val actual by OI.Ramp.ramp(60.0) { goal }

    init {
        // apply 5 volts if it's off by 15 rev/sec
        val kP = (5.0 / 12) / (15 * 60)
        val kD = kP / 5

        listOf(controllerA, controllerB).forEach {
            it.restoreFactoryDefaults()
            it.pidController.p = kP
//            it.pidController.i = 0.000001
            it.pidController.d = kD
            it.pidController.ff = 0.00021
//            it.pidController.setOutputRange(-0.3, 0.3)
        }
    }

    fun setRPM(speed: Double) {
        if (isDisabled) return
        goal = speed
//        controllerA.pidController.setReference(speed, ControlType.kVelocity)
//        controllerB.pidController.setReference(-speed, ControlType.kVelocity)
    }

    private var isDisabled = false

    fun disable() {
        isDisabled = true
        controllerA.disable()
        controllerB.disable()
    }

    override fun periodic() {
        val vel = max(controllerA.encoder.velocity.absoluteValue, controllerB.encoder.velocity.absoluteValue)
        if (!isDisabled) {
            controllerA.pidController.setReference(actual, ControlType.kVelocity)
            controllerB.pidController.setReference(-actual, ControlType.kVelocity)
            if (vel > (40.0 * 60)) {
                println("RPM is $vel, over threshold, stopping")
                disable()
                return
            } else if (max(controllerA.motorTemperature, controllerB.motorTemperature) > 40.0) {
                println("Max motor temperature reached (${max(controllerA.motorTemperature, controllerB.motorTemperature)}), stopping")
                disable()
                return
            }
            println(vel)
        } else {
            controllerA.pidController.p = 0.0
            controllerA.pidController.i = 0.0
            controllerA.pidController.d = 0.0
            controllerA.pidController.ff = 0.0

            controllerB.pidController.p = 0.0
            controllerB.pidController.i = 0.0
            controllerB.pidController.d = 0.0
            controllerB.pidController.ff = 0.0

            controllerA.set(0.0)
            controllerB.set(0.0)
        }
    }
}
