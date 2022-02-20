package com.bpsrobotics.engine.utils

import com.bpsrobotics.engine.utils.TrajectoryUtils.centerField
import com.team2898.robot.subsystems.Odometry

object Interpolation {
    private val targetDistance get() = Meters(centerField.translation.getDistance(Odometry.pose.translation))

    fun getRPMs(distance: Meters = targetDistance): Pair<RPM, RPM> { // TODO update equations
        return Pair(
            RPM(distance.value * distance.value * 1.18467 + distance.value * 76.6137 + 1136.61) /* Primary Motor */,
            RPM(distance.value * distance.value * 1.18467 + distance.value * 76.6137 + 1136.61) /* Secondary Motor*/
        )
    }

    fun getAccuracy(distance: Meters): Double {
        return 1.0 //TODO
    }
}