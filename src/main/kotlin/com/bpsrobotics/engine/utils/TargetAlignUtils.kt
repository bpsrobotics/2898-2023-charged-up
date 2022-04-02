package com.bpsrobotics.engine.utils

import com.bpsrobotics.engine.utils.TrajectoryUtils.centerField
import com.bpsrobotics.engine.utils.interpolations.PolynomialInterpolation
import com.team2898.robot.Constants
import com.team2898.robot.subsystems.Odometry
import com.team2898.robot.subsystems.Shooter
import com.team2898.robot.subsystems.Vision
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.wpilibj.Timer
import kotlin.math.abs
import kotlin.math.atan2

object TargetAlignUtils {
    private val targetDistance get() = if (Vision.lastUpdated.get() < 0.25) {
        Vision.distance
    } else {
        Meters(centerField.translation.getDistance(Odometry.pose.translation))
    }

    val isAligned get() = if (Vision.lastUpdated.get() < 0.25) {
        abs(Vision.angle.radiansValue()) < 0.02
    } else {
        val translation = Odometry.pose.translation.minus(centerField.translation)
        val rotation = Rotation2d(atan2(translation.y, translation.x))
        abs(rotation.radians - Odometry.pose.rotation.radians) < 0.02
    }

    val isCorrectDistance get() = if (Vision.lastUpdated.get() < 0.25) {
        abs(Vision.distance.value - Constants.SHOOT_DISTANCE) < 0.02
    } else {
        val translation = Odometry.pose.translation.minus(centerField.translation)
        val distance = translation.getDistance(Translation2d()) - Constants.SHOOT_DISTANCE
        abs(distance) < 0.02
    }

    private val flywheelInterpolation = PolynomialInterpolation(listOf(
        PolynomialInterpolation.PolynomialCoefficient(0.0, 0.0),
        PolynomialInterpolation.PolynomialCoefficient(0.0, 0.0),
        PolynomialInterpolation.PolynomialCoefficient(0.0, 0.0),
        PolynomialInterpolation.PolynomialCoefficient(0.0, 0.0),
        PolynomialInterpolation.PolynomialCoefficient(0.0, 0.0)
    ))

    private val spinnerInterpolation = PolynomialInterpolation(listOf(
        PolynomialInterpolation.PolynomialCoefficient(0.0, 0.0),
        PolynomialInterpolation.PolynomialCoefficient(0.0, 0.0),
        PolynomialInterpolation.PolynomialCoefficient(0.0, 0.0),
        PolynomialInterpolation.PolynomialCoefficient(0.0, 0.0),
        PolynomialInterpolation.PolynomialCoefficient(0.0, 0.0)
    ))

    fun speeds() = Shooter.ShooterSpeeds(
        flywheelInterpolation.interpolate(targetDistance.value).RPM,
        spinnerInterpolation.interpolate(targetDistance.value).RPM,
    )
}
