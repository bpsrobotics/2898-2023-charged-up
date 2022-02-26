package com.bpsrobotics.engine.utils

import com.bpsrobotics.engine.utils.TrajectoryUtils.centerField
import com.team2898.robot.subsystems.Odometry
import com.team2898.robot.subsystems.Shooter

object Interpolation {
    private val targetDistance get() = Meters(centerField.translation.getDistance(Odometry.pose.translation))

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