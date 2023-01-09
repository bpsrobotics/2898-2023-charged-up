package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.OI
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Feeder
import com.team2898.robot.subsystems.Intake
import com.team2898.robot.subsystems.Vision.xdist
import com.team2898.robot.subsystems.Vision.zdist
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj.drive.DifferentialDrive.curvatureDriveIK
import kotlin.math.atan2

class TeleOp : CommandBase() {

    init {
        addRequirements(Drivetrain)
    }

    // Called when the command is started.
    override fun initialize() {
        Drivetrain.mode = Drivetrain.Mode.OPEN_LOOP
        OI.defenseMode = false
    }

    // Called every time the scheduler runs while the command is scheduled.
    override fun execute() {

        val turn = OI.turn * 0.5
        val speeds = when {
            // When the align button is held, ignore the turn input
            OI.alignButton -> {
                val angleToTarget = -atan2(xdist, zdist)
                val kP = 0.333333
                curvatureDriveIK(OI.throttle, angleToTarget * kP, true)
            }
            // Quickturn buttons means turn in place
            OI.quickTurnRight - OI.quickTurnLeft != 0.0 -> DifferentialDrive.arcadeDriveIK(
                0.0,
                OI.quickTurnRight - OI.quickTurnLeft,
                false
            )

            // Otherwise, drive and turn normally
            else -> curvatureDriveIK(OI.throttle, turn, true)
        }

        if (OI.defenseMode) {
            Drivetrain.rawDrive(-speeds.right, -speeds.left)
        } else {
            Drivetrain.stupidDrive(`M/s`(speeds.left * 5.0), `M/s`(speeds.right * 5.0))
        }

        when {
            OI.overrideOpenGate -> Feeder.startOuttaking(true)
            OI.overrideClosedGate -> Feeder.startIntaking(true)
            OI.outtakeButton -> Feeder.startOuttaking(false)
        }

        when {
            OI.intakeBackward -> Intake.setSpeed(-0.5)
            OI.intakeForward && !Feeder.isFull() -> Intake.setSpeed(0.5)
            else -> Intake.setSpeed(0.0)
        }
    }

    // Called once the command ends or is interrupted.
    override fun end(interrupted: Boolean) {
    }

    // Returns true when the command should end.
    override fun isFinished(): Boolean {
        return false
    }
}
