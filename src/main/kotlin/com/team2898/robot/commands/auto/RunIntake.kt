package com.team2898.robot.commands.auto

import com.team2898.robot.subsystems.Intake
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.abs

/**
 * Commands the intake to pick up a ball at a specific location.
 * Only runs the intake once.
 */
class RunIntake(private val location: Translation2d) : CommandBase() {
    /**
     * Numbering from top to bottom of field (see map)
     *
     * Auto-relevant balls only
     */
    enum class Ball {
        BLUE_TERMINAL,
        BLUE_1,
        BLUE_2,
        BLUE_3,
        RED_TERMINAL,
        RED_1,
        RED_2,
        RED_3
    }

    constructor(ball: Ball) : this(
        when (ball) {
            Ball.BLUE_TERMINAL -> Translation2d(1.22, 1.18)
            Ball.BLUE_1 -> Translation2d(5.04, 6.14)
            Ball.BLUE_2 -> Translation2d(5.18, 1.91)
            Ball.BLUE_3 -> Translation2d(7.61, 0.36)
            Ball.RED_TERMINAL -> Translation2d(15.33, 7.06)
            Ball.RED_1 -> Translation2d(8.92, 7.88)
            Ball.RED_2 -> Translation2d(11.41, 6.32)
            Ball.RED_3 -> Translation2d(11.51, 2.09)
        }
    )

    var ranIntake = false
    var finishedIntake = false

    override fun execute() {
//        if (abs(Odometry.pose.translation.getDistance(location)) < 2.0) {
//            Intake.setOpenState(true)
//            Intake.setIntake(true)
//            ranIntake = true
//        } else if (abs(Odometry.pose.translation.getDistance(location)) > 2.0 && ranIntake) {
//            Intake.setOpenState(false)
//            Intake.setIntake(false)
//            finishedIntake = true
//        } else {
//            Intake.setOpenState(false)
//            Intake.setIntake(false)
//        }
    }

    override fun isFinished(): Boolean {
//        return finishedIntake
        return true
    }

    override fun end(interrupted: Boolean) {
//        Intake.setOpenState(false)
//        Intake.setIntake(false)
    }
}