/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.teamXXXX.robot

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants.  This class should not be used for any other purpose.  All constants should be
 * declared globally (i.e. public static).  Do not put anything functional in this class.
 */
object Constants {
    // Pneumatics ports
    const val GRABBER_PISTON_ONE_FORWARD = 1
    const val GRABBER_PISTON_ONE_REVERSE = 2
    const val GRABBER_PISTON_TWO_FORWARD = 3
    const val GRABBER_PISTON_TWO_REVERSE = 4

    // Motor IDs
    const val DRIVETRAIN_LEFT_MAIN = 1
    const val DRIVETRAIN_LEFT_SECONDARY = 2
    const val DRIVETRAIN_RIGHT_MAIN = 3
    const val DRIVETRAIN_RIGHT_SECONDARY = 4

    // Drivetrain limits
    const val DRIVETRAIN_CONTINUOUS_CURRENT_LIMIT = 30
    const val DRIVETRAIN_PEAK_CURRENT_LIMIT = 50
    const val DRIVETRAIN_PEAK_CURRENT_LIMIT_DURATION = 50
}
