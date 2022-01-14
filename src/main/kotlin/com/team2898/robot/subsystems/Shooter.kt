package com.team2898.robot.subsystems

import com.bpsrobotics.engine.controls.Controller
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import edu.wpi.first.wpilibj.controller.PIDController
import edu.wpi.first.wpilibj2.command.SubsystemBase
import kotlin.math.abs

object Shooter : SubsystemBase() {
    private val MotorControllerA = CANSparkMax(0 /* TODO IDs */, CANSparkMaxLowLevel.MotorType.kBrushless)
    private val MotorControllerB = CANSparkMax(1 /* TODO IDs */, CANSparkMaxLowLevel.MotorType.kBrushless)

    private val MotorPIDA = Controller.PID(0.0, 0.0)
    private val MotorPIDB = Controller.PID(0.0, 0.0)

    fun setRPM(speed: Double) {
        MotorPIDA.setpoint = speed
        MotorPIDA.setpoint = -speed
    }

    override fun periodic() {
        /*if (abs(MotorPIDA.setpoint) < 0.01) return
        MotorControllerA.set(MotorPIDA.calculate(MotorControllerA.encoder.velocity))
        MotorControllerB.set(MotorPIDB.calculate(MotorControllerB.encoder.velocity))*/
        println(MotorControllerA.encoder.position)
    }
}