package com.team2898.robot.commands.autos

import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.Constants
import com.team2898.robot.commands.ArmMove
import com.team2898.robot.commands.autos.simple.MobilityBalanceAuto
import com.team2898.robot.commands.autos.simple.PreloadAuto
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import edu.wpi.first.wpilibj2.command.WaitCommand

/**
 * Auto that does a variety of things that include preloading, balancing, mobility
 * @param preload Should the robot deposit it's prelaod during auto?
 * @param balance Should the robot balance during auto?
 * @param mobility Should the robot leave the community during auto?
 * @author Ozy King
 * */
class SimpleAutos(preload: Boolean, balance: Boolean, mobility: Boolean) : CommandBase() {
    private val autoCommandGroup: Command

    init {
        val commands = mutableListOf<CommandBase>()
        commands.add(StupidDriveCommand(`M/s`(0.0), `M/s`(0.0)))
        if (preload) {
            commands.add(PreloadAuto())
        }
        if (balance || mobility) {
            if (preload) {
                commands.add(ArmMove(Constants.ArmHeights.STOWED))
                commands.add(WaitCommand(0.25))
            }
            if (balance && mobility) {
                commands.add(MobilityBalanceAuto())
            } else if (balance) {
                commands.add(BalanceAuto())
            } else {
                commands.add(MobilityAuto())
            }
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