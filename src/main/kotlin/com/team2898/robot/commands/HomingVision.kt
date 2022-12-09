package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.`M/s`
import com.bpsrobotics.engine.utils.Degrees
import com.bpsrobotics.engine.utils.Sugar.clamp
import com.pathplanner.lib.PathPlanner
import com.team2898.robot.Constants
import com.team2898.robot.OI
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.math.trajectory.Trajectory
import edu.wpi.first.wpilibj2.command.CommandBase
import com.team2898.robot.subsystems.Vision
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import kotlin.math.atan2

class HomingVision : CommandBase() {
    private var throttle = 0.0
    override fun execute() {
        if (Vision.magnitude2D > 1) {
            throttle = (Vision.magnitude2D/10).clamp(0.5,1.0)
            val speeds = DifferentialDrive.curvatureDriveIK(throttle, atan2(Vision.xdist, Vision.zdist) / -3.0, true)
            Drivetrain.stupidDrive(`M/s`(speeds.left * -5), `M/s`(speeds.right * -5))
        } else {
            Drivetrain.rawDrive(0.0,0.0)
        }

    }

    override fun isFinished(): Boolean {
        return Vision.magnitude2D <= 1
    }

    override fun end(interrupted: Boolean) {
        Drivetrain.rawDrive(0.0,0.0)
    }
}