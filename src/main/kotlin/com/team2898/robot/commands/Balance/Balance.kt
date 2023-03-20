package com.team2898.robot.commands.Balance

import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.subsystems.Drivetrain
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase

class Balance(private val direction: DriveDirection) : CommandBase() {
    private val timer = Timer()
    override fun initialize() {
        timer.reset()
        timer.start()
    }
    override fun execute() {
        Drivetrain.stupidDrive(`M/s`(2.0 * direction.multiplier), `M/s`(2.0 * direction.multiplier))
    }
    override fun end(interrupted: Boolean) {
        Drivetrain.fullStop()
    }
    override fun isFinished(): Boolean {
        return timer.hasElapsed(0.1)
    }
}