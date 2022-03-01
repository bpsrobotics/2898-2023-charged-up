package com.team2898.robot

object RobotMap {
    // Pneumatics IDs
    const val INTAKE_L_FORWARD = 2
    const val INTAKE_L_REVERSE = 5
    const val INTAKE_R_FORWARD = 8
    const val INTAKE_R_REVERSE = 7

    const val CLIMB_L_FORWARD  = 4
    const val CLIMB_L_REVERSE  = 1
    const val CLIMB_R_FORWARD  = 6
    const val CLIMB_R_REVERSE  = 3

    // Motor IDs
    const val DRIVETRAIN_LEFT_MAIN        = 1
    const val DRIVETRAIN_LEFT_SECONDARY   = 3
    const val DRIVETRAIN_RIGHT_MAIN       = 2
    const val DRIVETRAIN_RIGHT_SECONDARY  = 4

    const val CLIMBER_LEFT_MAIN           = 6
    const val CLIMBER_LEFT_SECONDARY      = 7

    const val CLIMBER_RIGHT_MAIN          = 8
    const val CLIMBER_RIGHT_SECONDARY     = 9

    const val SHOOTER_FLYWHEEL            = 10
    const val SHOOTER_SPINNER             = 11

    const val INTAKE_MOTOR                = 12

    const val FEEDER_LEFT_VECTOR          = 13
    const val FEEDER_RIGHT_VECTOR         = 14
    const val FEEDER_UPPER                = 15

    // DIO
    const val DRIVETRAIN_LEFT_ENCODER_A   = 0
    const val DRIVETRAIN_LEFT_ENCODER_B   = 1

    const val DRIVETRAIN_RIGHT_ENCODER_A  = 2
    const val DRIVETRAIN_RIGHT_ENCODER_B  = 3

    const val CLIMBER_LEFT_LIMIT_SWITCH   = 4
    const val CLIMBER_RIGHT_LIMIT_SWITCH  = 5

    const val CLIMBER_LEFT_ENCODER_A      = 6
    const val CLIMBER_LEFT_ENCODER_B      = 7
    const val CLIMBER_RIGHT_ENCODER_A     = 8
    const val CLIMBER_RIGHT_ENCODER_B     = 9

    const val FEEDER_LASERSHARK           = 10  // on mxp

    // PWM
}
