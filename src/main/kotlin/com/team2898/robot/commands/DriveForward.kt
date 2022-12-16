package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.subsystems.Drivetrain
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj.Timer

class DriveForward(private val driveTimer: Double) : CommandBase() {
    val timer = Timer()
    override fun initialize() {
        timer.reset()
        timer.start()
    }

    override fun execute() {
        Drivetrain.stupidDrive(`M/s`(1.0), `M/s`(1.0))
    }
    override fun isFinished(): Boolean {
        return timer.hasElapsed(driveTimer)
    }
}