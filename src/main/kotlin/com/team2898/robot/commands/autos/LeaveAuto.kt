package com.team2898.robot.commands.autos

import com.team2898.robot.Constants.ArmHeights.HIGHCUBELAUNCH
import com.team2898.robot.commands.*
import com.team2898.robot.commands.ActivateIntake.RunningIntakes.RUNOUTTAKE
import com.team2898.robot.commands.ChangeIntakeState.IntakeState.CLOSEINTAKE
import com.team2898.robot.commands.ChangeIntakeState.IntakeState.OPENINTAKE
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup

class LeaveAuto : CommandBase() {
    private lateinit var autoCommandGroup: Command

    override fun initialize() {
        autoCommandGroup = SequentialCommandGroup (
            ChangeIntakeState(CLOSEINTAKE),
            ArmMove(HIGHCUBELAUNCH),
            ActivateIntake(RUNOUTTAKE),
            ChangeIntakeState(OPENINTAKE),
            PathFollowCommand("LowerRun.path", true)
            )
        autoCommandGroup.schedule()
    }

    override fun isFinished(): Boolean {
        return false
    }
}