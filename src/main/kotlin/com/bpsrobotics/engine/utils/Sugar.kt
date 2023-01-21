package com.bpsrobotics.engine.utils

import com.kauailabs.navx.frc.AHRS
import kotlin.math.PI

// Don't show warnings if these functions are unused or could be private
@Suppress("unused", "MemberVisibilityCanBePrivate")

/**
 * Sugar is an object where we are adding convenience functions
 */
object Sugar {

    /**
     * Converts the value from radians to degrees
     *
     * @return the value in radians as a double
     */
    fun Double.radiansToDegrees(): Double {
        return times(180 / PI)
    }

    /**
     * Converts the value from degrees to radians
     *
     * @return the value in degrees as a double
     */
    fun Double.degreesToRadians(): Double {
        return times(PI / 180)
    }


    /**
     * Converts the value from radians to degrees
     *
     * @return the value in radians as a double
     */
    fun Int.radiansToDegrees(): Double {
        return toDouble().radiansToDegrees()
    }

    /**
     * Converts the value from degrees to radians
     *
     * @return the value in degrees as a double
     */
    fun Int.degreesToRadians():Double{
        return toDouble().degreesToRadians()
    }

    /**
     * Clamps this double to be within the range [min]..[max], inclusive.
     */
    fun Double.clamp(min: Double = 0.0, max: Double = 1.0) = this.coerceIn(min, max)

    /**
     * Finds angle difference between two angles in radians
     */
    fun angleDifference(angle1: Double, angle2: Double): Double {
        val a = angle1 - angle2
        return (a + PI).mod(2.0 * PI) - PI
    }
}

typealias NAVX = AHRS
