/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

// Some constants aren't used outside of this file, but they may be later
@file:Suppress("MemberVisibilityCanBePrivate")

package com.team2898.robot

import com.bpsrobotics.engine.controls.Controller
import com.bpsrobotics.engine.utils.Ft
import com.bpsrobotics.engine.utils.MetersPerSecondSquared
import com.bpsrobotics.engine.utils.Volts
import com.bpsrobotics.engine.utils.`M/s`
import com.bpsrobotics.engine.utils.*
import com.team2898.robot.subsystems.Climb
import edu.wpi.first.math.controller.ElevatorFeedforward
import edu.wpi.first.math.trajectory.TrapezoidProfile

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants.  This class should not be used for any other purpose.  All constants should be
 * declared globally (i.e. public static).  Do not put anything functional in this class.
 */
object Constants {
    // Pneumatics ports

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

    // Can't be const because it's an expression
    val DRIVETRAIN_MAX_VELOCITY = `M/s`(5.0)
    val DRIVETRAIN_MAX_ACCELERATION = MetersPerSecondSquared(10.0)  // placeholder

    // Horizontal distance between the centers of the wheels on each side of the drivetrain
    val DRIVETRAIN_TRACK_WIDTH = In(22.0).toMeters()

    // Drivetrain characterization parameters, see [https://docs.wpilib.org/en/stable/docs/software/wpilib-tools/robot-characterization/index.html]
    // These do not carry from robot to robot, even if they're the same design! Characterize each drivetrain.
          val DRIVETRAIN_KS = Volts(0.10259)  // Voltage to make the motor start turning
    const val DRIVETRAIN_KV = 4.8 / 12  // Coefficient describing the friction proportional to rotation speed
    const val DRIVETRAIN_KA = 0.11235 / 12 // Describes how much voltage is required for a given amount of acceleration
    const val DRIVETRAIN_KP = 0.18636 // Proportional PID component
    const val DRIVETRAIN_KD = 0.0     // Derivative PID component (note: no I term is used because it can lead to runaway)

    // Ramsete parameters, see [https://file.tavsys.net/control/controls-engineering-in-frc.pdf] page 81
    const val DRIVETRAIN_RAMSETE_B = 2.0  // Higher values make it more aggressively stick to the trajectory. 0 < B
    const val DRIVETRAIN_RAMSETE_Z = 0.7  // Higher values give it more dampening. 0 < Z < 1

    val CLIMBER_1_LOADED = Climb.ProfileManager(
        Controller.PID(0.0, 0.0),
        ElevatorFeedforward(0.0, 0.0, 0.0, 0.0),
        TrapezoidProfile.Constraints(0.0, 0.0)
    )

    val CLIMBER_1_UNLOADED = Climb.ProfileManager(
        Controller.PID(0.0, 0.0),
        ElevatorFeedforward(0.0, 0.0, 0.0, 0.0),
        TrapezoidProfile.Constraints(0.0, 0.0)
    )

    val CLIMBER_2_LOADED = Climb.ProfileManager(
        Controller.PID(0.0, 0.0),
        ElevatorFeedforward(0.0, 0.0, 0.0, 0.0),
        TrapezoidProfile.Constraints(0.0, 0.0)
    )

    val CLIMBER_2_UNLOADED = Climb.ProfileManager(
        Controller.PID(0.0, 0.0),
        ElevatorFeedforward(0.0, 0.0, 0.0, 0.0),
        TrapezoidProfile.Constraints(0.0, 0.0)
    )
}
