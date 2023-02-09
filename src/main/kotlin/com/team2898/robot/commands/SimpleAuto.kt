package com.team2898.robot.commands

import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup

class SimpleAuto : CommandBase() {
    private lateinit var autoCommandGroup: Command

    override fun initialize() {
        autoCommandGroup = SequentialCommandGroup (
            ArmDeposit(),
            //TODO: Run the path to drive to the balance
            AutoParallel(),
            AutoBalance()
        )
    }

    override fun isFinished(): Boolean {
        return false
    }
}