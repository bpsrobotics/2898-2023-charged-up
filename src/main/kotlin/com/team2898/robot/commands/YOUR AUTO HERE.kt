package com.team2898.robot.commands

import com.team2898.robot.Constants

class `YOUR AUTO HERE`: CommandBase() {
    override fun initialize() {
        autoCommandGroup = SequentialCommandGroup(
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