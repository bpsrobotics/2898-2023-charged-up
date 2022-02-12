package com.bpsrobotics.engine.utils

class Interpolation {
    private fun interpolate(distance:Meters) : RPM{
        return RPM(distance.value*distance.value*1.18467 + distance.value*76.6137 + 1136.61)
    }

    fun getValue(distance:Meters) : RPM{
        return interpolate(distance)
    }
}