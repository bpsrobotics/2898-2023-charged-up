package com.bpsrobotics.engine.utils

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
}