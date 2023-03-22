package com.team2898.robot.commands.autos.simple

import com.team2898.robot.Constants
import com.team2898.robot.commands.ArmMove
import com.team2898.robot.commands.Balance.*
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup

/**
 * Drive over charge station out of community, then back on backwards to balance
 */
class MobilityBalanceAuto : CommandBase() {
    class BalanceAuto : CommandBase() {
        private lateinit var autoCommandGroup: Command

        override fun initialize() {
            //SequentialCommandGroup(SuperSimpleAuto(), SimpleBalance())
            autoCommandGroup = SequentialCommandGroup(
                    PreloadAuto(),
                    ArmMove(Constants.ArmHeights.PICKUP),
                    DriveOntoChargestation(DriveDirection.BACKWARDS),
                    DriveToTipChargestation(DriveDirection.BACKWARDS),
                    ChargestationMobility(DriveDirection.BACKWARDS),
                    DriveOntoChargestation(DriveDirection.FORWARDS),
                    DriveToTipChargestation(DriveDirection.FORWARDS),
                    Balance(DriveDirection.BACKWARDS)
            )
            autoCommandGroup.schedule()
        }

        override fun isFinished(): Boolean {
            return autoCommandGroup.isFinished
        }
    }
}