package com.team2898.robot

import edu.wpi.first.wpilibj.PneumaticsModuleType

object RobotMap {
    // Motor IDs
    const val DRIVETRAIN_LEFT_MAIN        = 2
    const val DRIVETRAIN_LEFT_SECONDARY   = 4
    const val DRIVETRAIN_RIGHT_MAIN       = 1
    const val DRIVETRAIN_RIGHT_SECONDARY  = 3
    const val ARM_MAIN                    = 5
    const val INTAKE_MAIN                 = 6
    const val INTAKE_SECONDARY            = 7


    // DIO
    const val DRIVETRAIN_LEFT_ENCODER_A   = 2
    const val DRIVETRAIN_LEFT_ENCODER_B   = 3

    const val DRIVETRAIN_RIGHT_ENCODER_A  = 0
    const val DRIVETRAIN_RIGHT_ENCODER_B  = 1

    const val ARM_LIMIT_SWITCH            = 5


    // Analog in
    const val ARM_ENCODER_PORT            = 3


    // Pneumatics
    const val PNUEMATICS_MODULE           = 42
    val PNEUMATICS_MODULE_TYPE            = PneumaticsModuleType.REVPH
    const val DISK_BRAKE_FORWARD          = 12
    const val DISK_BRAKE_BACKWARD         = 13
    const val INTAKE_FORWARD              = 14
    const val INTAKE_BACKWARD             = 15
    const val INTAKE_IN                   = 11
    const val INTAKE_OUT                  = 10
}
