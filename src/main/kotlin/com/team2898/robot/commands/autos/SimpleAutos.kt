package com.team2898.robot.commands.autos

import com.team2898.robot.Constants
import com.team2898.robot.commands.ArmMove
import com.team2898.robot.commands.autos.simple.MobilityBalanceAuto
import com.team2898.robot.commands.autos.simple.PreloadAuto
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup

class SimpleAutos(preload: Boolean, balance: Boolean, mobility: Boolean) : CommandBase() {
    private val autoCommandGroup: Command

    init {
        val commands = mutableListOf<CommandBase>()
        if(preload) commands.add(PreloadAuto())
        if(balance || mobility) {
            if (preload) commands.add(ArmMove(Constants.ArmHeights.PICKUP))
            if (balance && mobility) commands.add(MobilityBalanceAuto())
            else if(balance) commands.add(BalanceAuto())
            else commands.add(MobilityAuto())
        }
        autoCommandGroup = SequentialCommandGroup(*commands.toTypedArray())
    }

    override fun initialize() {
        autoCommandGroup.schedule()
    }
    override fun isFinished(): Boolean {
        return false
    }
}