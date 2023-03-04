package com.bpsrobotics.engine.utils.geometry

import com.bpsrobotics.engine.utils.DistanceUnit
import com.bpsrobotics.engine.utils.Feet
import com.bpsrobotics.engine.utils.Inches
import com.bpsrobotics.engine.utils.Meters
import edu.wpi.first.math.geometry.Pose2d
import kotlin.math.pow
import kotlin.math.sqrt

class Coordinate(val x: Double, val y: Double) {
    constructor(pose: Pose2d) : this(pose.x, pose.y)
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
     * Distance from 0, 0, calculated using pythagorean theorem
     * */
    val magnitude get() = sqrt(x.pow(2) + y.pow(2))
    fun distance(pos: Coordinate): Double{ return sqrt((x-pos.x).pow(2) + (y-pos.y).pow(2)) }
    fun distance(pose: Pose2d): Double{ return distance(Coordinate(pose)) }
    fun xdistance(pos: Double): Double { return x-pos}
    fun xdistance(pos: Coordinate): Double { return x-pos.x}
    fun xdistance(pos: Pose2d): Double { return y-pos.y}
    fun ydistance(pos: Double): Double { return y-pos}
    fun ydistance(pos: Coordinate): Double { return y-pos.y}
    fun ydistance(pos: Pose2d): Double { return y-pos.y}

    operator fun plus(other: Coordinate) : Coordinate {
        return Coordinate(x + other.x, y + other.y)
    }
    operator fun minus(other: Coordinate) : Coordinate {
        return Coordinate(x - other.x,y - other.y)
    }
    operator fun div(other: Double) : Coordinate{
        return Coordinate(x / other,y / other)
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