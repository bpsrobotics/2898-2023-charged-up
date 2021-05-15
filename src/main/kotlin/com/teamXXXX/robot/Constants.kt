/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

// Some constants aren't used outside of this file, but they may be later
@file:Suppress("MemberVisibilityCanBePrivate")

package com.teamXXXX.robot

import com.bpsrobotics.engine.controls.RamseteDrivetrain.*
import com.bpsrobotics.engine.utils.Ft
import com.bpsrobotics.engine.utils.MetersPerSecondSquared
import com.bpsrobotics.engine.utils.Volts
import com.bpsrobotics.engine.utils.`M/s`

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

    // GPIO
    const val DRIVETRAIN_LEFT_ENCODER_A = 0
    const val DRIVETRAIN_LEFT_ENCODER_B = 1
    const val DRIVETRAIN_RIGHT_ENCODER_A = 2
    const val DRIVETRAIN_RIGHT_ENCODER_B = 3

    // Drivetrain info
    const val DRIVETRAIN_CONTINUOUS_CURRENT_LIMIT = 30
    const val DRIVETRAIN_PEAK_CURRENT_LIMIT = 50
    const val DRIVETRAIN_PEAK_CURRENT_LIMIT_DURATION = 50
    const val DRIVETRAIN_MAX_VOLTAGE = 8.0  // Sometimes up to 10 or so

    // Can't be const because it's an expression
    val DRIVETRAIN_MAX_VELOCITY = `M/s`(2.0)  // Placeholder value
    val DRIVETRAIN_MAX_ACCELERATION = MetersPerSecondSquared(1.0)  // Placeholder value

    // Horizontal distance between the centers of the wheels on each side of the drivetrain
    val DRIVETRAIN_TRACK_WIDTH = Ft(2.5)  // Placeholder value

    val DRIVETRAIN_CONSTRAINTS = AutoConstraints(
        DRIVETRAIN_MAX_VELOCITY,
        DRIVETRAIN_MAX_ACCELERATION,
        Volts(DRIVETRAIN_MAX_VOLTAGE)
    )

    // Drivetrain characterization parameters, see [https://docs.wpilib.org/en/stable/docs/software/wpilib-tools/robot-characterization/index.html]
    // These do not carry from robot to robot, even if they're the same design! Characterize each drivetrain.
          val DRIVETRAIN_KS = Volts(0.0)  // Voltage to make the motor start turning
    const val DRIVETRAIN_KV = 0.0  // Coefficient describing the friction proportional to rotation speed
    const val DRIVETRAIN_KA = 0.0  // Describes how much voltage is required for a given amount of acceleration
    const val DRIVETRAIN_KP = 0.0  // Proportional PID component
    const val DRIVETRAIN_KD = 0.0  // Derivative PID component (note: no I term is used because it can lead to runaway)

    val DRIVETRAIN_CHARACTERIZATION = DrivetrainCharacterization(
        DRIVETRAIN_KS, DRIVETRAIN_KV, DRIVETRAIN_KA, DRIVETRAIN_KP, DRIVETRAIN_KD
    )

    // Ramsete parameters, see [https://file.tavsys.net/control/controls-engineering-in-frc.pdf] page 81
    const val DRIVETRAIN_RAMSETE_B = 0.0  // Higher values make it more aggressively stick to the trajectory. 0 < B
    const val DRIVETRAIN_RAMSETE_Z = 0.0  // Higher values give it more dampening. 0 < Z < 1

    val DRIVETRAIN_RAMSETE = RamseteParameters(DRIVETRAIN_RAMSETE_B, DRIVETRAIN_RAMSETE_Z)
}
