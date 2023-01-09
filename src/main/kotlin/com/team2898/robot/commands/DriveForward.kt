package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.subsystems.Drivetrain
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj.Timer

class DriveForward(private val driveTimer: Double, private val speed: `M/s`) : CommandBase() {
    val timer = Timer()
    override fun initialize() {
        timer.reset()
        timer.start()
        println("driving forward for $driveTimer seconds")
    }

    override fun execute() {
        Drivetrain.stupidDrive(speed, speed)
    }

    override fun isFinished(): Boolean {
        return timer.hasElapsed(driveTimer)
    }

    override fun end(interrupted: Boolean) {
        Drivetrain.mode = Drivetrain.Mode.OPEN_LOOP
        println("finished driving, stopping")
    }
}