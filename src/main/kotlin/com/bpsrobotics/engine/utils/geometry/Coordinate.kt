package com.bpsrobotics.engine.utils.geometry

import com.bpsrobotics.engine.utils.Feet
import com.bpsrobotics.engine.utils.Inches
import com.bpsrobotics.engine.utils.Meters
import kotlin.math.pow
import kotlin.math.sqrt

class Coordinate(val x: Double, val y: Double) {
    companion object {
        @JvmName("newMeters")
        fun new(x: Meters, y: Meters) = Coordinate(x.value, y.value)
        @JvmName("newInches")
        fun new(x: Inches, y: Inches) = Coordinate(x.meterValue(), y.meterValue())
        @JvmName("newFeet")
        fun new(x: Feet, y: Feet) = Coordinate(x.meterValue(), y.meterValue())
    }


    val magnitude get() = sqrt(x.pow(2) + y.pow(2))
    operator fun plus(other: Coordinate) : Coordinate {
        return Coordinate(x + other.x, y + other.y)
    }
    operator fun minus(other: Coordinate) : Coordinate {
        return Coordinate(x - other.x,y - other.y)
    }
}