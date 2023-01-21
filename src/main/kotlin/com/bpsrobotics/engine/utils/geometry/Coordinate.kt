package com.bpsrobotics.engine.utils.geometry

import com.bpsrobotics.engine.utils.Feet
import com.bpsrobotics.engine.utils.Inches
import com.bpsrobotics.engine.utils.Meters
import kotlin.math.pow
import kotlin.math.sqrt

class Coordinate(val x : Double, val y: Double) {
    constructor(x : Meters, y : Meters) : this(x.value, y.value)
    constructor(x : Inches, y : Inches) : this(x.meterValue(), y.meterValue())
    constructor(x : Feet, y : Feet) : this(x.meterValue(), y.meterValue())


    val magnitude get() = sqrt(x.pow(2) + y.pow(2))
    operator fun plus(other: Coordinate) : Coordinate {
        return Coordinate(x + other.x, y + other.y)
    }
    operator fun minus(other: Coordinate) : Coordinate {
        return Coordinate(x - other.x,y - other.y)
    }
}