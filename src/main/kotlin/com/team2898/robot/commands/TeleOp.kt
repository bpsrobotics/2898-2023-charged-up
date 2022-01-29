package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.OI
import com.team2898.robot.subsystems.Drivetrain
import edu.wpi.first.wpilibj.drive.DifferentialDrive.curvatureDriveIK
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.absoluteValue
import kotlin.math.min

class TeleOp : CommandBase() {
    /**
     * Creates a new TeleOp.
     */
    init {}

    // Called when the command is started.
    override fun initialize() {
        Drivetrain.stupidDrive(`M/s`(0.0), `M/s`(0.0))
    }

    // Called every time the scheduler runs while the command is scheduled.
    override fun execute() {
        val speeds = when {
            // Quickturn buttons means turn in place
            OI.quickTurnRight - OI.quickTurnLeft != 0.0 -> curvatureDriveIK(
                OI.throttle,
                OI.quickTurnRight - OI.quickTurnLeft,
                true
            )

            // Otherwise, drive and turn normally
            else -> curvatureDriveIK(OI.throttle, OI.turn, false)
        }

//        if (min(speeds.left.absoluteValue, speeds.right.absoluteValue) == 0.0) {
//            Drivetrain.rawDrive(0.0, 0.0)
//        } else {
            Drivetrain.stupidDrive(`M/s`(speeds.left * 2), `M/s`(speeds.right * 2))
//        }
    }

    // Called once the command ends or is interrupted.
    override fun end(interrupted: Boolean) {
    }

    // Returns true when the command should end.
    override fun isFinished(): Boolean {
        return false
    }
}
