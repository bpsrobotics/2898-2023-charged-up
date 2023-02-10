package com.team2898.robot.commands

import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.CommandBase

class BalanceTesting : CommandBase() {
    override fun initialize() {
        SmartDashboard.putNumber("Motor Output", 0.0)
    }

    override fun execute() {
        val speed = SmartDashboard.getNumber("Motor Output", 0.0)
        Drivetrain.rawDrive(speed, speed)
        SmartDashboard.putNumber("pitch", Odometry.NavxHolder.navx.pitch.toDouble())
    }
}
