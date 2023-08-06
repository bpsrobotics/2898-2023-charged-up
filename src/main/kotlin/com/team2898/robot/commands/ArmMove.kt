package com.team2898.robot.commands

import com.team2898.robot.Constants
import com.team2898.robot.subsystems.Arm
import edu.wpi.first.wpilibj2.command.CommandBase

/**
 * Moves arm to given height
 * @property goal Height to set arm to. Use Constants.
 * @author Ori
 */
class ArmMove(private val goal: Double) : CommandBase() {
    constructor(goal: Constants.ArmHeights) : this(goal.position)

    override fun initialize() {
        Arm.setGoal(goal)
    }

    override fun isFinished(): Boolean {
        return !Arm.isMoving()
    }
}
