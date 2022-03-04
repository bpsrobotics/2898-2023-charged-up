package com.team2898.robot.commands

import com.team2898.robot.subsystems.Drivetrain
import edu.wpi.first.wpilibj2.command.CommandBase

class NoopAuto : CommandBase() {
    override fun isFinished(): Boolean {
        return false
    }

    override fun execute() {
        Drivetrain.rawDrive(0.0, 0.0)
    }
}
