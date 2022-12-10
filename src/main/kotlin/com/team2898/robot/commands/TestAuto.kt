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

class TestAuto : CommandBase() {
    // Steals path file and makes trajectory
    private var firstPath: Trajectory = PathPlanner.loadPath(
            "bunnybot-initial-path",
            Constants.DRIVETRAIN_MAX_VELOCITY.metersPerSecondValue(),
            Constants.DRIVETRAIN_MAX_ACCELERATION.metersPerSecondSquaredValue()
    )

    override fun initialize() {
        // Resets Odometry to the start of the first path rather than 0, 0
        val initial = firstPath.initialPose
        Odometry.reset(M(initial.x), M(initial.y), Degrees(initial.rotation.degrees))

        Drivetrain.follow(firstPath)
    }

    /*override fun execute() {
        println(firstPath.endPose.translation.getDistance(Odometry.pose.translation))
    }*/

    override fun isFinished(): Boolean {
        return firstPath.endPose.translation.getDistance(Odometry.pose.translation) < 0.75
    }

    override fun end(interrupted: Boolean) {
        Drivetrain.stopAuto()
    }
}