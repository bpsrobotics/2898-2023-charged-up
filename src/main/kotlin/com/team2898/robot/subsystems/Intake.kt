package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.Sugar.clamp
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import com.team2898.robot.Constants.INTAKE_MOTOR
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Intake : SubsystemBase() {
    private val Controller = CANSparkMax(INTAKE_MOTOR, CANSparkMaxLowLevel.MotorType.kBrushless)

    fun setSpeed(speed: Double) {
        Controller.set(speed.clamp(0.0, 1.0))
    }

}
