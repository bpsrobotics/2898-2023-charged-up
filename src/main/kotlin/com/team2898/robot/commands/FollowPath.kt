package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.Degrees
import com.bpsrobotics.engine.utils.M
import com.bpsrobotics.engine.utils.TrajectoryUtils.endPose
import com.team2898.robot.Constants
import com.pathplanner.lib.PathPlanner
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.math.trajectory.Trajectory
import edu.wpi.first.wpilibj2.command.CommandBase

class FollowPath(pathFile: String,
                 private val stopTolerance: Double = 0.75,
                 private val resetOdometry: Boolean = true) : CommandBase() {
    // Steals path file and makes trajectory
    private var path: Trajectory = PathPlanner.loadPath(
            pathFile,
            Constants.DRIVETRAIN_MAX_VELOCITY.metersPerSecondValue(),
            Constants.DRIVETRAIN_MAX_ACCELERATION.metersPerSecondSquaredValue()
    )

    override fun initialize() {
        // Resets Odometry to the start of the first path rather than 0, 0
        val initial = path.initialPose
        if(resetOdometry) {Odometry.reset(M(initial.x), M(initial.y), Degrees(initial.rotation.degrees))}

        Drivetrain.follow(path)
    }

    override fun isFinished(): Boolean {
        return path.endPose.translation.getDistance(Odometry.pose.translation) < stopTolerance
    }

    override fun end(interrupted: Boolean) {
        Drivetrain.stopAuto()
    }
}