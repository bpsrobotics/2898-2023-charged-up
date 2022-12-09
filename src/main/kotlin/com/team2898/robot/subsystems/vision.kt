package com.team2898.robot.subsystems

import edu.wpi.first.networktables.EntryListenerFlags
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.SubsystemBase
import kotlin.math.pow

/** Contains and updates data received from vision */
object Vision : SubsystemBase() {
    /*private val last3xDist = mutableListOf<Double>()
    private val last3yDist = mutableListOf<Double>()
    private val last3zDist = mutableListOf<Double>()*/
    /** Camera's local X distance to Apriltag in meters */
    var xdist = 0.0
    /** Camera's local Y distance to Apriltag in meters */
    var ydist = 0.0
    /** Camera's local Z distance to Apriltag in meters */
    var zdist = 0.0
    /** Timestamp of last time vision was updated */
    var lastGotten = 0.0
    /** Time since the distance was updated (in seconds) */
    val timeSinceLastFix get() = Timer.getFPGATimestamp() - lastGotten
    /** Is true if the Apriltag is (likely) in the camera's picture */
    val inCameraRange get() = lastGotten < 0.25

    init {
        SmartDashboard.getEntry("VisionX").addListener({
            lastGotten = Timer.getFPGATimestamp()
        }, EntryListenerFlags.kUpdate)
    }

    /** X and Y distance combined (flat plane) in meters */
    val magnitude2D get() = (xdist.pow(2) + zdist.pow(2)).pow(0.5)
    override fun periodic() {
        //Getting values from SmartDashboard
        //last3xDist.add(SmartDashboard.getNumber("VisionX", 0.0)
        xdist = SmartDashboard.getNumber("VisionX",0.0)
        ydist = SmartDashboard.getNumber("VisionY", 0.0)
        zdist = SmartDashboard.getNumber("VisionZ", 0.0)
    }
}