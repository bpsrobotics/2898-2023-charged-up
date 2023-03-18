package com.team2898.robot.commands.autos

import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.Constants.ArmHeights.*
import com.team2898.robot.commands.*
import com.team2898.robot.commands.ActivateIntake.RunningIntakes.RUNOUTTAKE
import com.team2898.robot.commands.ChangeIntakeState.IntakeState.CLOSEINTAKE
import com.team2898.robot.commands.ChangeIntakeState.IntakeState.OPENINTAKE
import com.team2898.robot.subsystems.Drivetrain
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup

class TopMiddleAuto : CommandBase() {
    private lateinit var autoCommandGroup: Command

    override fun initialize() {
        Drivetrain.stupidDrive(`M/s`(0.0), `M/s`(0.0))
        autoCommandGroup = SequentialCommandGroup(
            ChangeIntakeState(CLOSEINTAKE),
            ArmMove(HIGHCUBELAUNCH),
            ActivateIntake(RUNOUTTAKE),
            ChangeIntakeState(OPENINTAKE),
//            PathFollowCommand("BackwardCharging.path", true),
//            AutoPerpendicular(),
            ParallelCommandGroup(
                ArmMove(LOWGOAL),
                SequentialCommandGroup(SuperSimpleAuto(), SimpleBalance())
            )
        )
        autoCommandGroup.schedule()
    }

    override fun isFinished(): Boolean {
        return false
    }
}