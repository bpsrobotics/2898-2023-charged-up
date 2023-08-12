package com.team2898.robot.commands

import com.team2898.robot.Constants
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import com.team2898.robot.commands.ArmMove

object `YOUR AUTO HERE`: CommandBase() {
    override fun initialize() {
        val autoCommandGroup = SequentialCommandGroup(
            DriveForward(/* Seconds */ 1.0, /* Speed */ 0.69),
            // Move forward

            // Bring arm up (Constants.ArmHeights.MIDDLECONEGOAL)
            ArmMove(Constants.ArmHeights.MIDDLECONEGOAL),
            // Outtake
            ActivateIntake(ActivateIntake.RunningIntakes.RUNOUTTAKE),
            // Wait

            // Stop Outtake
            // Bring arm down (MOVING)
            ArmMove(Constants.ArmHeights.MOVING),
            // Backup
            DriveForward(1.0,-0.69)
        )
        autoCommandGroup.schedule()
    }

    override fun isFinished(): Boolean {
        return false
    }
}