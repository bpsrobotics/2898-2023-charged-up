package com.team2898.robot.subsystems

import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import com.team2898.robot.Constants.INTAKE_MOTOR
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Intake : SubsystemBase() {
    private val Controller = CANSparkMax(INTAKE_MOTOR, CANSparkMaxLowLevel.MotorType.kBrushless)

    init {
    }

    fun setSpeed(speed:Double){
        var clampedSpeed: Double
        if(speed<0){
            clampedSpeed = 0.0
        }else if(speed>1.0){
            clampedSpeed = 1.0
        }else{
            clampedSpeed = speed
        }
        Controller.set(clampedSpeed)
    }

}
