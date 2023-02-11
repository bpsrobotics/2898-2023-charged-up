package com.team2898.robot.commands

import com.team2898.robot.Constants
import com.team2898.robot.Constants.ArmHeights.MIDDLEBOXGOAL
import com.team2898.robot.Constants.ArmHeights.PICKUP
import com.team2898.robot.commands.ActivateIntake.RunningIntakes.RUNINTAKE
import com.team2898.robot.commands.ActivateIntake.RunningIntakes.RUNOUTTAKE
import com.team2898.robot.commands.ChangeIntakeState.IntakeState.CLOSEINTAKE
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
/**
 * Auto function that: deposits a game piece, goes parallel to the charge station, and balances.
 * @author Ori
 * */
class SimpleAuto : CommandBase() {
    private lateinit var autoCommandGroup: Command

    override fun initialize() {
        autoCommandGroup = SequentialCommandGroup (
            ArmMove(MIDDLEBOXGOAL),
            ActivateIntake(RUNOUTTAKE),
            //TODO: Run the path to drive back
            ArmMove(PICKUP),
            //Move the bot to the cone
            ActivateIntake(RUNINTAKE),
            ChangeIntakeState(CLOSEINTAKE),
            //Move the bot down
            AutoPerpendicular(),
            AutoBalance()
        )
    }

    override fun isFinished(): Boolean {
        return false
    }
}