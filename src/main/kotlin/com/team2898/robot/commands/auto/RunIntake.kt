package com.team2898.robot.commands.auto

import com.team2898.robot.subsystems.Intake
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.InstantCommand
import kotlin.math.abs

/**
 * Commands the intake to pick up a ball at a specific location.
 * Only runs the intake once.
 */
class RunIntake(private val location: Translation2d) : InstantCommand({ Intake.openIntake(); Intake.startIntake() }) {

//    init {
//        addRequirements(Intake)
//    }

//    /**
//     * Numbering from top to bottom of field (see map)
//     *
//     * Auto-relevant balls only
//     */
    enum class Ball(val position: Translation2d) {
        BLUE_TERMINAL(Translation2d(1.22, 1.18)),
        BLUE_1(Translation2d(5.04, 6.14)),
        BLUE_2(Translation2d(5.18, 1.91)),
        BLUE_3(Translation2d(7.61, 0.36)),
        RED_TERMINAL(Translation2d(15.33, 7.06)),
        RED_1(Translation2d(8.92, 7.88)),
        RED_2(Translation2d(11.41, 6.32)),
        RED_3(Translation2d(11.51, 2.09)),
    }
//
    constructor(ball: Ball) : this(ball.position)
//
//    var ranIntake = false
//    var finishedIntake = false
//
//    override fun execute() {
////        if (abs(Odometry.pose.translation.getDistance(location)) < 2.0) {
//            Intake.setOpenState(true)
//            Intake.setIntake(true)
////            ranIntake = true
////        } else if (abs(Odometry.pose.translation.getDistance(location)) > 2.0 && ranIntake) {
////            Intake.setOpenState(false)
////            Intake.setIntake(false)
////            finishedIntake = true
////        } else {
////            Intake.setOpenState(false)
////            Intake.setIntake(false)
////        }
//    }
//
//    override fun isFinished(): Boolean {
////        return finishedIntake
//        return false
//    }
//
//    override fun end(interrupted: Boolean) {
//        Intake.setOpenState(false)
//        Intake.setIntake(false)
//    }
}