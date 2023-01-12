package com.team2898.robot.subsystems

import com.team2898.robot.commands.AutoAlign
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless
import com.team2898.robot.Constants.ARM_MAXACCEL
import com.team2898.robot.Constants.ARM_MAXSPEED
import com.team2898.robot.Constants.ARM_RAISED_KD
import com.team2898.robot.Constants.ARM_RAISED_KI
import com.team2898.robot.Constants.ARM_RAISED_KP
import com.team2898.robot.RobotMap.ARM_MAIN
import com.team2898.robot.RobotMap.ARM_SECONDARY
import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.DoubleSolenoid.Value.kForward
import edu.wpi.first.wpilibj.DoubleSolenoid.Value.kReverse
import edu.wpi.first.wpilibj2.command.SubsystemBase

class ArmControls : SubsystemBase() {

    var constraints = TrapezoidProfile.Constraints(ARM_MAXSPEED, ARM_MAXACCEL)
    var controller = ProfiledPIDController(ARM_RAISED_KP, ARM_RAISED_KI, ARM_RAISED_KD,constraints)
    var currentGoal: Double? = null
    var armMotor1 = CANSparkMax(ARM_MAIN, kBrushless)
    var armMotor2 = CANSparkMax(ARM_SECONDARY, kBrushless)
    var breakSolenoid = DoubleSolenoid(TODO(),TODO(),TODO(),TODO())

    override fun periodic() {
        val profile = currentGoal
        if (profile == null) {

            //Engage breaks, stop motors
            armMotor1.stopMotor()
            armMotor2.stopMotor()
            breakSolenoid.set(kReverse)
        }
        else {

            //Controller moves the arm
            breakSolenoid.set(kForward)
        }
    }


}