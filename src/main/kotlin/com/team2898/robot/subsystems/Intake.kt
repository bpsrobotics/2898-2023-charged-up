package com.team2898.robot.subsystems

import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import com.revrobotics.ControlType
import com.team2898.robot.Constants.INTAKE_MOTOR
import com.team2898.robot.OI
import edu.wpi.first.wpilibj2.command.SubsystemBase
import edu.wpi.first.wpiutil.math.MathUtil.clamp
import kotlin.math.absoluteValue
import kotlin.math.max

object Intake : SubsystemBase() {
    private val Controller = CANSparkMax(INTAKE_MOTOR, CANSparkMaxLowLevel.MotorType.kBrushless)

    init {
    }

    fun setSpeed(speed:Double){
        var clampedSpeed = clamp(speed, 0.0, 1.0)
        Controller.set(clampedSpeed)
    }

}
