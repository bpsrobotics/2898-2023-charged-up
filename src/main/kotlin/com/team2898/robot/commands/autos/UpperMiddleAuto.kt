package com.team2898.robot.commands.autos

import com.team2898.robot.Constants
import com.team2898.robot.commands.*
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup

class UpperMiddleAuto : CommandBase() {
    private lateinit var autoCommandGroup: Command

    override fun initialize() {
        autoCommandGroup = SequentialCommandGroup (
            ArmMove(Constants.ArmHeights.MIDDLEBOXGOAL),
            ActivateIntake(ActivateIntake.RunningIntakes.RUNOUTTAKE),
            PathFollowCommand("UpperCharging.path", true),
            AutoPerpendicular(),
            AutoBalance(),
            )
    }

    override fun isFinished(): Boolean {
        return false
    }
}