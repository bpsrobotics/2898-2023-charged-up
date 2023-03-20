package com.team2898.robot.commands.Balance

import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.absoluteValue

class ChargestationMobility(private val direction: DriveDirection) : CommandBase() {
    private var pitch = Odometry.NavxHolder.navx.pitch.toDouble()
    private val timer = Timer()
    override fun execute() {
        Drivetrain.stupidDrive(`M/s`(2.0 * direction.multiplier), `M/s`(2.0 * direction.multiplier))
    }
    override fun end(interrupted: Boolean) {
        Drivetrain.fullStop()
    }
    override fun isFinished(): Boolean {
        return (timer.hasElapsed(0.2) && pitch.absoluteValue < 2.5) || timer.hasElapsed(0.5)
    }
}