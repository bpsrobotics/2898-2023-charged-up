package com.bpsrobotics.engine.utils.geometry

import kotlin.math.pow
import kotlin.math.sqrt

class Coordinate(val x: Double, val y: Double){
    val magnitude get() = sqrt(x.pow(2) + y.pow(2))
    operator fun plus(other: Coordinate) : Coordinate {
        return Coordinate(x + other.x, y + other.y)
    }
    operator fun minus(other: Coordinate) : Coordinate {
        return Coordinate(x - other.x,y - other.y)
    }
}