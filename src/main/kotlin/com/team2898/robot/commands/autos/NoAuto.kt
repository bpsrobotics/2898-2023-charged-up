package com.team2898.robot.commands.autos

import com.team2898.robot.subsystems.Drivetrain
import edu.wpi.first.wpilibj2.command.CommandBase

class NoAuto: CommandBase() {
    override fun execute() {
        Drivetrain.rawDrive(0.0, 0.0)
    }
}