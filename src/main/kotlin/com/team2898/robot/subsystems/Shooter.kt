package com.team2898.robot.subsystems

import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import com.revrobotics.ControlType
import edu.wpi.first.wpilibj2.command.SubsystemBase
import kotlin.math.absoluteValue
import kotlin.math.max

object Shooter : SubsystemBase() {
    // TODO: constants
    private val controllerA = CANSparkMax(1, CANSparkMaxLowLevel.MotorType.kBrushless)
    private val controllerB = CANSparkMax(2, CANSparkMaxLowLevel.MotorType.kBrushless)
    private var goal = 0.0
    private val actual = Ramp.ramp

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
            it.pidController.setOutputRange(-0.3, 0.3)
        }
    }

    fun setRPM(speed: Double) {
        if (isDisabled) return
        goal = speed
//        controllerA.pidController.setReference(speed, ControlType.kVelocity)
//        controllerB.pidController.setReference(-speed, ControlType.kVelocity)
    }

    private var isDisabled = false

    override fun periodic() {
        val vel = max(controllerA.encoder.velocity.absoluteValue, controllerB.encoder.velocity.absoluteValue)
        if (!isDisabled) {
            if (vel > (20.0 * 60)) {
                isDisabled = true
                controllerA.disable()
                controllerB.disable()
                println("RPM is $vel, over threshold, stopping")
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
