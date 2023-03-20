package com.team2898.robot.commands.Balance

import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase

class DriveOntoChargestation(private val direction: DriveDirection) : CommandBase() {
    private var pitch = Odometry.NavxHolder.navx.pitch.toDouble()
    private val timer = Timer()
    override fun initialize() {
        timer.reset()
        timer.start()
    }
    override fun execute() {
        if(timer.hasElapsed(1.0)){
            Drivetrain.stupidDrive(`M/s`(2.0 * direction.multiplier), `M/s`(2.0 * direction.multiplier))
        } else{
            Drivetrain.stupidDrive(`M/s`(0.0), `M/s`(0.0))
        }
    }
    override fun end(interrupted: Boolean) {
        Drivetrain.fullStop()
    }

    override fun isFinished(): Boolean {
        return when(direction) {
            DriveDirection.FORWARDS -> pitch < -5
            DriveDirection.BACKWARDS -> pitch > 5
        }
    }
}