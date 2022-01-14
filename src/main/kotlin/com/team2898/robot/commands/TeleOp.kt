package com.team2898.robot.commands

import com.team2898.robot.OI
//import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Shooter
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.PI

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
    /*when {
      // Quickturn buttons means turn in place
      OI.quickTurnRight - OI.quickTurnLeft != 0.0 -> Drivetrain.cheesyDrive(OI.quickTurnRight - OI.quickTurnLeft, OI.throttle, true)

      // Otherwise, drive and turn normally
      else -> Drivetrain.cheesyDrive(OI.turn, OI.throttle, false)
    }*/
    Shooter.setRPM(PI)
  }

  // Called once the command ends or is interrupted.
  override fun end(interrupted: Boolean) {
  }

  // Returns true when the command should end.
  override fun isFinished(): Boolean {
    return false
  }
}
