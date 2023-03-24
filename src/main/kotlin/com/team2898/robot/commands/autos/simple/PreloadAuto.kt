package com.team2898.robot.commands.autos.simple

import com.team2898.robot.Constants.ArmHeights.*
import com.team2898.robot.commands.*
import com.team2898.robot.commands.ActivateIntake.RunningIntakes.RUNOUTTAKE
import com.team2898.robot.commands.ChangeIntakeState.IntakeState.CLOSEINTAKE
import com.team2898.robot.subsystems.Intake
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.InstantCommand
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup

/**
 * Deposits preload on high cube.
 */
class PreloadAuto : CommandBase() {
    private lateinit var autoCommandGroup: Command

    override fun initialize() {
        autoCommandGroup = SequentialCommandGroup(
            ParallelDeadlineGroup(
                SequentialCommandGroup(
                    ArmMove(PICKUP),
                    ArmMove(HIGHCUBELAUNCH),
                ),
                InstantCommand({ Intake.runIntake(0.5) })
            ),
            ActivateIntake(RUNOUTTAKE),
        )
//        autoCommandGroup.schedule()
        autoCommandGroup.initialize()
    }

    override fun execute() {
        autoCommandGroup.execute()
    }

    override fun isFinished(): Boolean {
        return autoCommandGroup.isFinished
    }

    override fun end(interrupted: Boolean) {
        println("PRELOAD DONE")
        autoCommandGroup.end(interrupted)
    }
}