package com.team2898.robot

import edu.wpi.first.wpilibj.PneumaticsModuleType

object RobotMap {
    // Motor IDs
    const val DRIVETRAIN_LEFT_MAIN        = 1
    const val DRIVETRAIN_LEFT_SECONDARY   = 3
    const val DRIVETRAIN_RIGHT_MAIN       = 2
    const val DRIVETRAIN_RIGHT_SECONDARY  = 4
    const val ARM_MAIN = 5
    const val ARM_SECONDARY = 6
    const val INTAKE_MOTOR = 7


    // DIO
    const val DRIVETRAIN_LEFT_ENCODER_A   = 2
    const val DRIVETRAIN_LEFT_ENCODER_B   = 3

    const val DRIVETRAIN_RIGHT_ENCODER_A  = 0
    const val DRIVETRAIN_RIGHT_ENCODER_B  = 1

    const val ARM_ENCODER_A = 4
    const val ARM_ENCODER_B = 5

    //Pneumatics
    const val PNUEMATICS_MODULE = 42
    val PNEUMATICS_MODULE_TYPE = PneumaticsModuleType.REVPH
    const val DISK_BREAK_FORWARD = 1
    const val DISK_BREAK_BACKWARD = 2
    const val INTAKE_BREAK_FORWARD = 3
    const val INTAKE_BREAK_BACKWARD = 4


}
