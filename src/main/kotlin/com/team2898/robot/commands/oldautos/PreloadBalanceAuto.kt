package com.team2898.robot.commands.oldautos

import com.team2898.robot.Constants.ArmHeights.*
import com.team2898.robot.commands.*
import com.team2898.robot.commands.ActivateIntake.RunningIntakes.RUNOUTTAKE
import com.team2898.robot.commands.ChangeIntakeState.IntakeState.CLOSEINTAKE
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup

class PreloadBalanceAuto : CommandBase() {
    private lateinit var autoCommandGroup: Command

    override fun initialize() {
        autoCommandGroup = SequentialCommandGroup(
            ChangeIntakeState(CLOSEINTAKE),
            ArmMove(MIDDLEBOXGOAL),
            ArmMove(HIGHCUBELAUNCH),
            ActivateIntake(RUNOUTTAKE),
            ParallelCommandGroup(
                ArmMove(PICKUP),
                SequentialCommandGroup(SuperSimpleAuto(), SimpleBalance())
            )
        )
        autoCommandGroup.schedule()
    }

    override fun isFinished(): Boolean {
        return false
    }
}