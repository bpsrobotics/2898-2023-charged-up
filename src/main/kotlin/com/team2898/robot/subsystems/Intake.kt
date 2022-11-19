package com.team2898.robot.subsystems

import com.ctre.phoenix.motorcontrol.TalonSRXControlMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.team2898.robot.RobotMap.INTAKE_MOTOR
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Intake : SubsystemBase() {
    private val intakeMotor = TalonSRX(INTAKE_MOTOR)

    fun setSpeed(power: Double) {
        intakeMotor.set(TalonSRXControlMode.PercentOutput, power)
    }
}