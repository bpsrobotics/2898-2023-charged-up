package com.bpsrobotics.engine.utils

import edu.wpi.first.math.geometry.Pose2d
import kotlin.math.sqrt

object Interpolation {
    fun interpolate(distance:Meters) : Pair<RPM, RPM>{ // Is a placeholder
        return Pair(
            RPM(distance.value*distance.value*1.18467 + distance.value*76.6137 + 1136.61) /* Primary Motor */,
            RPM(distance.value*distance.value*1.18467 + distance.value*76.6137 + 1136.61) /* Secondary Motor*/
        )
    }
    fun getAccuracy(distance: Meters): Double{
        return 1.0 //TODO
    }
    fun poseToDistanceFromTarget(Location: Pose2d): Meters{
        val TargetX = 823.0
        val TargetY = 411.5
        val CurrentX = Location.x
        val CurrentY = Location.y
        val DistanceAsDouble = sqrt((CurrentX - TargetX) * (CurrentX - TargetX) + (CurrentY - TargetY) * (CurrentY - TargetY))
        return Meters(DistanceAsDouble)
    }
}