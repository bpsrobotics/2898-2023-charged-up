package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.Sugar.clamp
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import com.team2898.robot.Constants.INTAKE_MOTOR
import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.PneumaticsModuleType
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Intake : SubsystemBase() {
    private val piston1 = DoubleSolenoid(PneumaticsModuleType.REVPH, 0, 1) // TODO Piston Constants
    private val piston2 = DoubleSolenoid(PneumaticsModuleType.REVPH, 0, 1) // TODO Piston Constants
    private val controller = CANSparkMax(INTAKE_MOTOR, CANSparkMaxLowLevel.MotorType.kBrushless)

    fun setOpenState(state: Boolean) {
        piston1.set(if (state) DoubleSolenoid.Value.kForward else DoubleSolenoid.Value.kReverse)
        piston2.set(if (state) DoubleSolenoid.Value.kForward else DoubleSolenoid.Value.kReverse)
    }

    fun setIntake(state: Boolean) {
        if (state) controller.set(0.5) else controller.set(0.0)
    }
}
