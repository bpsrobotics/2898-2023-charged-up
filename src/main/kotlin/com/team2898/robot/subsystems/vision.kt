package com.team2898.robot.subsystems

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Vision : SubsystemBase() {
    var xdist = 0.0
    var ydist = 0.0
    var zdist = 0.0
    override fun periodic() {
        xdist = SmartDashboard.getNumber("VisionX")
        ydist = SmartDashboard.getNumber("VisionY")
        zdist = SmartDashboard.getNumber("VisionZ")


    }
}