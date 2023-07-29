package com.team2898.robot.commands

import com.team2898.robot.Constants
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup

class `YOUR AUTO HERE`: CommandBase() {
    override fun initialize() {
        val autoCommandGroup = SequentialCommandGroup(
            //Move forward
            //Bring arm up
            //Outake
            //Stop outake
            //Bring arm down (MIDDLECONEGOAL)
            //Backup

        )
        autoCommandGroup.schedule()
    }
}