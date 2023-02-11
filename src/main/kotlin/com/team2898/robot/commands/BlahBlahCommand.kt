package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.subsystems.Drivetrain
import edu.wpi.first.wpilibj2.command.CommandBase

class BlahBlahCommand : CommandBase() {
    override fun execute() {
        Drivetrain.stupidDrive(`M/s`(0.0), `M/s`(0.0))
    }
}
