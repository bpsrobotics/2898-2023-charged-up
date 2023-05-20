package com.team2898.robot.commands.autos

import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.subsystems.Drivetrain
import edu.wpi.first.wpilibj2.command.CommandBase

class StupidDriveCommand(val left: `M/s`, val right: `M/s`) : CommandBase() {
    override fun initialize() {
        Drivetrain.stupidDrive(left, right)
    }

    override fun isFinished(): Boolean {
        return true
    }
}
