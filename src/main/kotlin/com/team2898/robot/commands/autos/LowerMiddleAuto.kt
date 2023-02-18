package com.team2898.robot.commands.autos

import com.team2898.robot.Constants
import com.team2898.robot.Constants.ArmHeights.MIDDLEBOXGOAL
import com.team2898.robot.commands.*
import com.team2898.robot.commands.ActivateIntake.RunningIntakes.RUNINTAKE
import com.team2898.robot.commands.ActivateIntake.RunningIntakes.RUNOUTTAKE
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup

class LowerMiddleAuto : CommandBase() {
    private lateinit var autoCommandGroup: Command

    override fun initialize() {
        autoCommandGroup = SequentialCommandGroup (
//            ArmMove(MIDDLEBOXGOAL),
//            ActivateIntake(RUNOUTTAKE),
            PathFollowCommand("MidDropCube.path", true),
//            ActivateIntake(RUNINTAKE),
            PathFollowCommand("MidPickupCube.path", false),

        )
        autoCommandGroup.schedule()
    }

    override fun isFinished(): Boolean {
        return false
    }
}