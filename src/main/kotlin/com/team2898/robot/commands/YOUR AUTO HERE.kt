package com.team2898.robot.commands

import com.team2898.robot.Constants
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup

class `YOUR AUTO HERE`: CommandBase() {
    override fun initialize() {
        val autoCommandGroup = SequentialCommandGroup(
            //EX: DriveForward(/* Seconds */ 1.0, /* Speed */ 1.0)
            // Move forward
            // Bring arm up (Constants.ArmHeights.MIDDLECONEGOAL)
            // Outtake
            // Wait
            // Stop Outtake
            // Bring arm down (MOVING)
            // Backup

        )
        autoCommandGroup.schedule()
    }
}