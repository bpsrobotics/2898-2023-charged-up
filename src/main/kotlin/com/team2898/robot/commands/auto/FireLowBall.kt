package com.team2898.robot.commands.auto

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.wpilibj2.command.CommandBase

class FireLowBall(private val ballCount: Int, private val location: Pose2d) : CommandBase() {
    override fun isFinished(): Boolean {
        return true
    }
}