package com.team2898.robot.commands.autos

import com.team2898.robot.Constants
import com.team2898.robot.Constants.ArmHeights.*
import com.team2898.robot.commands.*
import com.team2898.robot.commands.ActivateIntake.RunningIntakes.RUNINTAKE
import com.team2898.robot.commands.ActivateIntake.RunningIntakes.RUNOUTTAKE
import com.team2898.robot.commands.ChangeIntakeState.IntakeState.CLOSEINTAKE
import com.team2898.robot.commands.ChangeIntakeState.IntakeState.OPENINTAKE
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup

class ScoringAuto: CommandBase() {
    private lateinit var autoCommandGroup: Command

    override fun initialize() {
        autoCommandGroup = SequentialCommandGroup (
            ArmMove(HIGHCUBELAUNCH),
            ActivateIntake(RUNOUTTAKE),
            PathFollowCommand("LowerScore", true),
            ArmMove(PICKUP),
            ActivateIntake(RUNINTAKE),
            PathFollowCommand("LowerGrab2", false),
            PathFollowCommand("LowerRunBack", false),
            ArmMove(MIDDLEBOXGOAL),
            ActivateIntake(RUNOUTTAKE),
            PathFollowCommand("LowerScore", true),
            ArmMove(PICKUP),
            ActivateIntake(RUNINTAKE),
            PathFollowCommand("LowerGrab", false),
            PathFollowCommand("LowerRunBack2", false),
            ArmMove(LOWGOAL),
            ActivateIntake(RUNOUTTAKE)


        )
        autoCommandGroup.schedule()
    }

    override fun isFinished(): Boolean {
        return false
    }
}
