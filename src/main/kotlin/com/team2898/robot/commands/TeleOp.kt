package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.Volts
import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.OI
import com.team2898.robot.subsystems.Climb
import com.team2898.robot.subsystems.Drivetrain
import edu.wpi.first.wpilibj.DoubleSolenoid.Value.*
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj.drive.DifferentialDrive.curvatureDriveIK

class TeleOp : CommandBase() {

    init {
        addRequirements(Drivetrain)
    }

    // Called when the command is started.
    override fun initialize() {
        Drivetrain.mode = Drivetrain.Mode.OPEN_LOOP
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

        Drivetrain.stupidDrive(`M/s`(speeds.left * 5), `M/s`(speeds.right * 5))
    }

    // Called once the command ends or is interrupted.
    override fun end(interrupted: Boolean) {
    }

    // Returns true when the command should end.
    override fun isFinished(): Boolean {
        return false
    }
}
