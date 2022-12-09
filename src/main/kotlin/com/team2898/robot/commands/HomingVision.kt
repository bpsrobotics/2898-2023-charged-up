package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.`M/s`
import com.bpsrobotics.engine.utils.Sugar.clamp
import com.team2898.robot.subsystems.Drivetrain
import edu.wpi.first.wpilibj2.command.CommandBase
import com.team2898.robot.subsystems.Vision
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import kotlin.math.atan2

/** Robot moves within 1 meter of target, assuming Apriltag is within visual range of camera */
class HomingVision : CommandBase() {
    override fun execute() {
        // Makes sure the robot is not closer than 1 meter
        if (Vision.magnitude2D > 2) {
            // Throttle reduces as it gets closer to target
            val throttle = (Vision.magnitude2D/10).clamp(0.5,1.0) //TODO: Just use log 10 instead of /10
            val speeds = DifferentialDrive.curvatureDriveIK(throttle, atan2(Vision.xdist, Vision.zdist) / 3.0, true)
            Drivetrain.stupidDrive(`M/s`(speeds.left * -1), `M/s`(speeds.right * -1))
        } else {
            Drivetrain.rawDrive(0.0,0.0) // Full stop
        }
        println(Vision.timeSinceLastFix)
    }

    override fun isFinished(): Boolean {
        return Vision.inCameraRange || (Vision.magnitude2D <= 2) //Checks if it is closer than 2 meters
    }

    override fun end(interrupted: Boolean) {
        Drivetrain.rawDrive(0.0,0.0) // Full stop
    }

}