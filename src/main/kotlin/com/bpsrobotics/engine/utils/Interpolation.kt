package com.bpsrobotics.engine.utils

import com.bpsrobotics.engine.utils.TrajectoryUtils.centerField
import com.team2898.robot.subsystems.Odometry
import com.team2898.robot.subsystems.Shooter
import com.team2898.robot.subsystems.Vision
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj.Timer
import kotlin.math.abs
import kotlin.math.atan2

object Interpolation {
    private val targetDistance get() = Meters(centerField.translation.getDistance(Odometry.pose.translation))
    val isAligned get() = if ((Timer.getFPGATimestamp() - Vision.lastUpdated.value) < 0.25) {
        abs(Vision.angle.radiansValue()) < 0.02
    } else {
        val translation = Odometry.pose.translation.minus(centerField.translation)
        val rotation = Rotation2d(atan2(translation.y, translation.x))
        abs(rotation.radians - Odometry.pose.rotation.radians) < 0.02
    }

    fun getRPMs(distance: Meters = targetDistance): Shooter.ShooterSpeeds { // TODO update equations
        return Shooter.ShooterSpeeds(
            RPM(distance.value * distance.value * 1.18467 + distance.value * 76.6137 + 1136.61) /* Primary Motor */,
            RPM(distance.value * distance.value * 1.18467 + distance.value * 76.6137 + 1136.61) /* Secondary Motor*/
        )
    }

    fun getAccuracy(distance: Meters): Double {
        return 1.0 //TODO
    }
}