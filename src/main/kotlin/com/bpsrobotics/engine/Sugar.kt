package com.bpsrobotics.engine

import kotlin.math.PI

/**
 * Sugar is an object where we are adding convenience functions
 */

object Sugar{
    /**
     * Converts the value from radians to degrees
     *
     * @return the value in radians as a double
     */
    fun Double.radiansToDegrees():Double{
        return times(180/PI)
    }

    /**
     * Converts the value from degrees to radians
     *
     * @return the value in degrees as a double
     */
    fun Double.degreesToRadians():Double{
        return times(PI/180)
    }


    /**
     * Converts the value from radians to degrees
     *
     * @return the value in radians as a double
     */
    fun Int.radiansToDegrees():Double{
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
}
