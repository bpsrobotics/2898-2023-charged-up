// Some constants aren't used outside of this file, but they may be later
@file:Suppress("MemberVisibilityCanBePrivate")

package com.team2898.robot

import com.bpsrobotics.engine.utils.*
import com.team2898.robot.subsystems.Climb
import com.team2898.robot.subsystems.Shooter
import edu.wpi.first.math.controller.ElevatorFeedforward
import edu.wpi.first.math.trajectory.TrapezoidProfile

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants.  This class should not be used for any other purpose.  All constants should be
 * declared globally (i.e. public static).  Do not put anything functional in this class.
 */
object Constants {
    // Drivetrain info
    const val DRIVETRAIN_CONTINUOUS_CURRENT_LIMIT = 10
    const val DRIVETRAIN_PEAK_CURRENT_LIMIT = 50
    const val DRIVETRAIN_PEAK_CURRENT_LIMIT_DURATION = 50

    // Can't be const because it's an expression
    val DRIVETRAIN_MAX_VELOCITY = `M/s`(2.0)
    val DRIVETRAIN_MAX_ACCELERATION = MetersPerSecondSquared(0.5)  // placeholder

    // Horizontal distance between the centers of the wheels on each side of the drivetrain
    val DRIVETRAIN_TRACK_WIDTH = In(23.0).toMeters()

    // Drivetrain characterization parameters, see [https://docs.wpilib.org/en/stable/docs/software/wpilib-tools/robot-characterization/index.html]
    // These do not carry from robot to robot, even if they're the same design! Characterize each drivetrain.
    const val DRIVETRAIN_KS = 0.084314 / 12  // Voltage to make the motor start turning
    const val DRIVETRAIN_KV = 2.4325 / 12  // Coefficient describing the friction proportional to rotation speed
    const val DRIVETRAIN_KA = 0.39392 / 12 // Describes how much voltage is required for a given amount of acceleration
    const val DRIVETRAIN_KP = 3.0 / 12 // Proportional PID component
    const val DRIVETRAIN_KD = 0.0 // Derivative PID component (note: no I term is used because it can lead to runaway)

    // Ramsete parameters, see [https://file.tavsys.net/control/controls-engineering-in-frc.pdf] page 81
    const val DRIVETRAIN_RAMSETE_B = 6.0  // Higher values make it more aggressively stick to the trajectory. 0 < B
    const val DRIVETRAIN_RAMSETE_Z = 0.7  // Higher values give it more dampening. 0 < Z < 1

    const val CLIMBER_ENDSTOP = 100000

    // TODO: Difference between current and target RPM that must be reached before shooter lock is removed
    const val SHOOTER_THRESHOLD = 5
    const val TIME_TO_SHOOT = 1.0 // TODO: Time it takes to shoot (approx, should be reasonably similar each time)

    const val FEEDER_VECTOR_SPEED = 0.3
    const val FEEDER_SPEED = 0.5
    const val FEEDER_MIN_DISTANCE = 2.0 // TODO: Distance at which ball is in shooter
    const val FEEDER_MAX_DISTANCE = 5.0 // TODO: Primary LaserShark distance threshold that corresponds to no ball in feeder

    // Control Scheme Constants
    enum class DriverMap {
        DEFAULT,
        FORZA
    }
    val DRIVER_MAP = DriverMap.DEFAULT

    val DUMP_SPEED = Shooter.ShooterSpeeds(RPM(0.0), RPM(0.0)) //TODO: values
}
