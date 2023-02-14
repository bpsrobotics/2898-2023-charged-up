package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.CommandBase

class BangBangBalance : CommandBase() {
    private var deadZone = 0.0
    private val encoder = Odometry.NavxHolder.navx

    override fun initialize() {
        SmartDashboard.putNumber("BangBang Speed", 0.0)
        SmartDashboard.putNumber("Deadzone", 0.0)
    }
    override fun execute() {
        val pitch = encoder.pitch
        val traverseSpeed = SmartDashboard.getNumber("BangBang Speed",0.0)
        deadZone = SmartDashboard.getNumber("Deadzone", 0.0)
        //TODO: Tune traverseSpeed for BangBang
        val speed = when {
            pitch < -deadZone -> traverseSpeed
            pitch > deadZone -> -traverseSpeed
            else -> 0.0
        }
        Drivetrain.stupidDrive(`M/s`(speed),`M/s`(speed))
    }
}