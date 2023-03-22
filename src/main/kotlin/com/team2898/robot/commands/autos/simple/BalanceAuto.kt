package com.team2898.robot.commands.autos

import com.team2898.robot.Constants.ArmHeights.HIGHCUBELAUNCH
import com.team2898.robot.Constants.ArmHeights.PICKUP
import com.team2898.robot.commands.*
import com.team2898.robot.commands.ActivateIntake.RunningIntakes.RUNOUTTAKE
import com.team2898.robot.commands.Balance.Balance
import com.team2898.robot.commands.Balance.DriveDirection
import com.team2898.robot.commands.Balance.DriveOntoChargestation
import com.team2898.robot.commands.Balance.DriveToTipChargestation
import com.team2898.robot.commands.ChangeIntakeState.IntakeState.CLOSEINTAKE
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup

/**
 * Balances on the charge station
 */
class BalanceAuto : CommandBase() {
    private lateinit var autoCommandGroup: Command

    override fun initialize() {
        //SequentialCommandGroup(SuperSimpleAuto(), SimpleBalance())
        autoCommandGroup = SequentialCommandGroup(
                DriveOntoChargestation(DriveDirection.BACKWARDS),
                DriveToTipChargestation(DriveDirection.BACKWARDS),
                Balance(DriveDirection.FORWARDS))
        autoCommandGroup.schedule()
    }

    override fun isFinished(): Boolean {
        return autoCommandGroup.isFinished
    }
}