package com.team2898.robot.subsystems

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Vision : SubsystemBase() {
    var xdist = 0.0
    var ydist = 0.0
    var zdist = 0.0
    override fun periodic() {
        xdist = SmartDashboard.getNumber("VisionX", 0.0)
        ydist = SmartDashboard.getNumber("VisionY", 0.0)
        zdist = SmartDashboard.getNumber("VisionZ", 0.0)


    }
}