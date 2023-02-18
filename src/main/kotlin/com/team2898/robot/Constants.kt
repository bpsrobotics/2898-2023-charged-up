// Some constants aren't used outside of this file, but they may be later
@file:Suppress("MemberVisibilityCanBePrivate")

package com.team2898.robot

import com.bpsrobotics.engine.utils.*

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants.  This class should not be used for any other purpose.  All constants should be
 * declared globally (i.e. public static).  Do not put anything functional in this class.
 */
object Constants {
    // Drivetrain info
    const val DRIVETRAIN_CONTINUOUS_CURRENT_LIMIT = 30
    const val DRIVETRAIN_PEAK_CURRENT_LIMIT = 50
    const val DRIVETRAIN_PEAK_CURRENT_LIMIT_DURATION = 50

    // Can't be const because it's an expression
    val DRIVETRAIN_MAX_VELOCITY = `M/s`(1.0)
    val DRIVETRAIN_MAX_ACCELERATION = MetersPerSecondSquared(1.5)  // placeholder

    // Horizontal distance between the centers of the wheels on each side of the drivetrain
    val DRIVETRAIN_TRACK_WIDTH = In(11.0).toMeters()

    // Drivetrain characterization parameters, see [https://docs.wpilib.org/en/stable/docs/software/wpilib-tools/robot-characterization/index.html]
    // These do not carry from robot to robot, even if they're the same design! Characterize each drivetrain.
    const val DRIVETRAIN_KS = 0.080943 / 12  // Voltage to make the motor start turning
    const val DRIVETRAIN_KV = 4.9598 / 12  // Coefficient describing the friction proportional to rotation speed
    const val DRIVETRAIN_KA = 3.0903 / 12 // Describes how much voltage is required for a given amount of acceleration
    const val DRIVETRAIN_KP = 7.2798 / 12 // Proportional PID component
    const val DRIVETRAIN_KD = 0.0 // Derivative PID component (note: no I term is used because it can lead to runaway)

    // Ramsete parameters, see [https://file.tavsys.net/control/controls-engineering-in-frc.pdf] page 81
    // **DO NOT CHANGE THESE PARAMETERS**
    const val DRIVETRAIN_RAMSETE_B = 5.0  // Higher values make it more aggressively stick to the trajectory. 0 < B
    const val DRIVETRAIN_RAMSETE_Z = 0.7  // Higher values give it more dampening. 0 < Z < 1

    //PIDController  for the autobalance code
    const val AUTOBALANCE_KP = 0.0
    const val AUTOBALANCE_KI = 0.0
    const val AUTOBALANCE_KD = 0.0

    //Arm parameters
    const val ARM_MAXSPEED = 5.0
    const val ARM_MAXACCEL = 5.0
    const val ARM_RAISED_KP = 2.0
    const val ARM_RAISED_KI = 2.0
    const val ARM_RAISED_KD = 2.0
    const val ARM_LOWERED_KP = 2.0
    const val ARM_LOWERED_KI = 2.0
    const val ARM_LOWERED_KD = 2.0


    //TODO: Set up the proper positions for the arm
    enum class ArmHeights(val position: Double) {
        STOWED(0.0),
        PICKUP(0.0),
        LOWGOAL(0.0),
        MIDDLECONEGOAL(0.0),
        MIDDLEBOXGOAL(0.0),
        HIGHCUBELAUNCH(0.0)
    }
}
