package com.team2898.robot.commands

import com.team2898.robot.Constants
import com.team2898.robot.Constants.ArmHeights.*
import com.team2898.robot.subsystems.Arm
import edu.wpi.first.wpilibj2.command.CommandBase

class ArmDeposit : CommandBase() {
    var hasCone: Boolean = true

    override fun execute() {
        if (hasCone) {
            Arm.setGoal(MIDDLECONEGOAL.position)
        } else {
            Arm.setGoal(MIDDLEBOXGOAL.position)
        }
        //hasCone = false
    }
}