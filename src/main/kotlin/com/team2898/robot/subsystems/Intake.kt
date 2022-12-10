package com.team2898.robot.subsystems

import com.ctre.phoenix.motorcontrol.TalonSRXControlMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import com.team2898.robot.RobotMap
import com.team2898.robot.RobotMap.INTAKE_MOTOR
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Intake : SubsystemBase() {
    private val intakeMotor = CANSparkMax(RobotMap.INTAKE_MOTOR, CANSparkMaxLowLevel.MotorType.kBrushless)

    fun setSpeed(power: Double) {
        intakeMotor.set(power)
        if (power > 0.0) {
            Feeder.startIntaking(false)
        }
    }
}