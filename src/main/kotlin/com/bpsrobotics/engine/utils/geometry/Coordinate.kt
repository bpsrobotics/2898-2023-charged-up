package com.bpsrobotics.engine.utils.geometry

import com.bpsrobotics.engine.utils.DistanceUnit
import com.bpsrobotics.engine.utils.Feet
import com.bpsrobotics.engine.utils.Inches
import com.bpsrobotics.engine.utils.Meters
import kotlin.math.pow
import kotlin.math.sqrt

class Coordinate(val x: Double, val y: Double) {

    constructor(x: DistanceUnit, y: DistanceUnit) : this(x.meterValue(), y.meterValue())
    companion object {
        @JvmName("newMeters")
        fun new(x: Meters, y: Meters) = Coordinate(x.value, y.value)
        @JvmName("newInches")
        fun new(x: Inches, y: Inches) = Coordinate(x.meterValue(), y.meterValue())
        @JvmName("newFeet")
        fun new(x: Feet, y: Feet) = Coordinate(x.meterValue(), y.meterValue())
    }

    /**
     * Distance from 0, 0
     * */
    val magnitude get() = sqrt(x.pow(2) + y.pow(2))
    operator fun plus(other: Coordinate) : Coordinate {
        return Coordinate(x + other.x, y + other.y)
    }
    operator fun minus(other: Coordinate) : Coordinate {
        return Coordinate(x - other.x,y - other.y)
    }

    override fun toString(): String {
        return "(x: ${x}, y: ${y})"
    }

    /**
     * Reflects the point across a horizontal line
     * @return Reflected point
     * @param x The x value of the vertical line to reflect across
     * @author Ozy King
     */
    fun reflectHorizontally(x: DistanceUnit) : Coordinate{
        return Coordinate(x.meterValue() + (x.meterValue() - this.x),y)
    }
}