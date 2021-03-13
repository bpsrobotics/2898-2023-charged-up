package com.teamXXXX.robot.commands

import com.teamXXXX.robot.OI
import com.teamXXXX.robot.subsystems.Drivetrain
import edu.wpi.first.wpilibj2.command.CommandBase

class TeleOp : CommandBase() {
  /**
   * Creates a new TeleOp.
   */
  init {}

  // Called when the command is started.
  override fun initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  override fun execute() {
    when {
      // Quickturn buttons means turn in place
      OI.quickTurnRight -> Drivetrain.cheesyDrive(1.0, OI.throttle, true)
      OI.quickTurnLeft -> Drivetrain.cheesyDrive(-1.0, OI.throttle, true)

      // Otherwise, drive and turn normally
      else -> Drivetrain.cheesyDrive(OI.turn, OI.throttle, false)
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
