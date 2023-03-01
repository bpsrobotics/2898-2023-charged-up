package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.Polynomial
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.wpilibj2.command.SubsystemBase
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard


/**
 * @property x The position of the robot in the field on the X-axis (Long end)
 * @property y The position of the robot in the field on the Y-axis (Short end)
 * @property z The height of the robot off of the ground
 * @property r The rotation, in degrees, of the robot, (Yaw)
 * @property pose The Pose2d derived from vision measurements.
 * @property lastUpdate The timestamp of the last update the vision was updated, measured in seconds since epoch
 */
object Vision : SubsystemBase() {
    var currentRobotPose = Pose2d()
    val x get() = SmartDashboard.getNumber("VisionZ",0.0)
    val y get() = SmartDashboard.getNumber("VisionX",0.0)
    val z get() = SmartDashboard.getNumber("VisionY",0.0)
    val r get() = SmartDashboard.getNumber("VisionRotation", 0.0)
    val stdev get() = SmartDashboard.getNumber("VisionSTDEV",1.0) //TODO: Make into Matrix
    val lastUpdate get() = SmartDashboard.getNumber("VisionLastUpdate", 0.0)
    val pose get() = Pose2d(Translation2d(x,y), Rotation2d(r))
}
