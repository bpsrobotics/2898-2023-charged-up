package com.team2898.robot.subsystems

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.SubsystemBase
import kotlin.math.pow

/** Contains and updates data received from vision */
object Vision : SubsystemBase() {
    /** Camera's local X distance to Apriltag in meters */
    var xdist = 0.0
    /** Camera's local Y distance to Apriltag in meters */
    var ydist = 0.0
    /** Camera's local Z distance to Apriltag in meters */
    var zdist = 0.0

    /** X and Y distance combined (flat plane) in meters */
    val magnitude2D get() = (xdist.pow(2) + ydist.pow(2)).pow(0.5)
    override fun periodic() {
        //Getting values from SmartDashboard
        xdist = SmartDashboard.getNumber("VisionX", 0.0)
        ydist = SmartDashboard.getNumber("VisionY", 0.0)
        zdist = SmartDashboard.getNumber("VisionZ", 0.0)
    }
}