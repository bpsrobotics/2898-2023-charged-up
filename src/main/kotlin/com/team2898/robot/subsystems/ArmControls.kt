package com.team2898.robot.subsystems

import com.team2898.robot.Constants.ARM_MAXACCEL
import com.team2898.robot.Constants.ARM_MAXSPEED
import com.team2898.robot.Constants.ARM_RAISED_KD
import com.team2898.robot.Constants.ARM_RAISED_KI
import com.team2898.robot.Constants.ARM_RAISED_KP
import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.wpilibj2.command.SubsystemBase

class ArmControls : SubsystemBase() {

    var constraints = TrapezoidProfile.Constraints(ARM_MAXSPEED, ARM_MAXACCEL)
    var controller = ProfiledPIDController(ARM_RAISED_KP, ARM_RAISED_KI, ARM_RAISED_KD,constraints)
    var currentGoal: Double? = null

    //Motor for the arm
    //NOTE: Will be Neos
    var armMotor = null

    override fun periodic() {
        val profile = currentGoal
        if (profile == null) {

            //Engage breaks, stop motors
        }
        else {
            //Controller moves the arm
            //controller.
        }
    }


}