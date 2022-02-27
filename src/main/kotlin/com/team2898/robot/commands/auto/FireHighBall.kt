package com.team2898.robot.commands.auto

import com.bpsrobotics.engine.utils.Meters
import com.bpsrobotics.engine.utils.TrajectoryUtils.centerField
import com.team2898.robot.subsystems.Feed
import com.team2898.robot.subsystems.Odometry
import com.team2898.robot.subsystems.Shooter
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.max

class FireHighBall(private val location: Pose2d) : CommandBase() {
    private var startedShooter = false
    private var completed = false

    /** Current distance from optimal shot point. */
    private val currentDistance get() = Meters(location.translation.getDistance(Odometry.pose.translation))

    override fun execute() {
        if (!startedShooter && currentDistance.value < 2.0) {
            Shooter.spinUp(Meters(centerField.translation.getDistance(location.translation)))
            startedShooter = true
        } else if (
            startedShooter &&
            currentDistance.value < 1.0 &&
            max(Odometry.vels.leftMetersPerSecond, Odometry.vels.rightMetersPerSecond) < 0.25
        ) {
            Feed.shoot()
        } else if (startedShooter && currentDistance.value > 3.0) {
            Shooter.stopShooter()
            completed = true
        }
    }

    override fun isFinished(): Boolean {
        return completed
    }
}