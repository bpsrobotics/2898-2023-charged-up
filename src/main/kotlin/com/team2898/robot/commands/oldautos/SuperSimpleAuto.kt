package com.team2898.robot.commands.oldautos

import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.subsystems.Drivetrain
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase

class SuperSimpleAuto : CommandBase() {
    val timer = Timer()

    override fun initialize() {
        timer.start()
    }

    override fun execute() {
        Drivetrain.stupidDrive(`M/s`(-2.0), `M/s`(-2.0))
    }

    override fun isFinished(): Boolean {
        return timer.hasElapsed(1.7)
//        return timer.hasElapsed(1.3)
    }

    override fun end(interrupted: Boolean) {
        Drivetrain.stupidDrive(`M/s`(0.0), `M/s`(0.0))
    }
}
