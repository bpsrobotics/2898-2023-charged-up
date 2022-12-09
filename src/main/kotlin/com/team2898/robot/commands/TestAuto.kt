package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.Degrees
import com.bpsrobotics.engine.utils.M
import com.team2898.robot.Constants
import com.pathplanner.lib.PathPlanner
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.trajectory.Trajectory
import edu.wpi.first.wpilibj2.command.CommandBase

class TestAuto : CommandBase() {
    // Steals path file and makes trajectory
    private var firstPath: Trajectory = PathPlanner.loadPath(
            "bunnybot-initial-path.path",
            Constants.DRIVETRAIN_MAX_VELOCITY.metersPerSecondValue(),
            Constants.DRIVETRAIN_MAX_ACCELERATION.metersPerSecondSquaredValue()
    )
    /*private val path = Drivetrain.trajectoryMaker.builder()
        .start(Pose2d())
        .point(1.5, 1.5)
        .point(0.0, 3.0)
        .point(-1.5, 1.5)
        .point(0.0, 0.0)
        .point(1.5, 1.5)
        .point(0.0, 3.0)
        .point(-1.5, 1.5)
        .end(Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)))
        .build()*/

    override fun initialize() {
        Odometry.reset(M(0.0), M(0.0), Degrees(0.0))
        Drivetrain.follow(firstPath)
    }

    override fun isFinished(): Boolean {
        return Drivetrain.mode != Drivetrain.Mode.CLOSED_LOOP
    }

    override fun end(interrupted: Boolean) {
        Drivetrain.stopAuto()
    }
}