@file:Suppress("unused", "MemberVisibilityCanBePrivate")  // Not all of these classes are used

/**
 * A set of inline classes that represent a value with a unit.  These inline classes are represented
 * at runtime as java primitive doubles, so they don't require heap ram allocation and are performant.
 * Note: if you specify an argument type as one of the interfaces declared in this file, it will box
 * the type, leading to a performance hit.  _This is likely fine_, because computers are fast.
 */
package com.bpsrobotics.engine

import kotlin.math.PI

interface Unit {
    val value: Double
}

// Distance
interface DistanceUnit : Unit {
    fun toMeters(): Double
}

inline class Meters(override val value: Double) : DistanceUnit {
    override fun toMeters() = value
}

inline class Feet(override val value: Double) : DistanceUnit {
    override fun toMeters() = value * 0.3048
}

inline class Inches(override val value: Double) : DistanceUnit {
    override fun toMeters() = value * 0.0254
}

// Velocity
interface VelocityUnit : Unit {
    fun toMetersPerSecond(): Double
}

inline class MetersPerSecond(override val value: Double) : VelocityUnit {
    override fun toMetersPerSecond() = value
}

inline class FeetPerSecond(override val value: Double) : VelocityUnit {
    override fun toMetersPerSecond() = value * 0.3084
}

inline class KilometersPerHour(override val value: Double) : VelocityUnit {
    override fun toMetersPerSecond() = value / 3.6
}

inline class MilesPerHour(override val value: Double) : VelocityUnit {
    override fun toMetersPerSecond() = value * 0.44704
}

// Acceleration
interface AccelerationUnit : Unit {
    fun toMetersPerSecondSquared(): Double
}

inline class MetersPerSecondSquared(override val value: Double) : AccelerationUnit {
    override fun toMetersPerSecondSquared() = value
}

// Angles
interface AngleUnit : Unit {
    fun toRadians(): Double
}

inline class Radians(override val value: Double) : AngleUnit {
    override fun toRadians() = value
}

inline class Degrees(override val value: Double) : AngleUnit {
    companion object {
        const val DEGREES_TO_RADS = (2 * PI) / 360
    }

    override fun toRadians() = value * DEGREES_TO_RADS
}

// Mass
interface MassUnit : Unit {
    fun toKilograms(): Double
}

inline class Kilograms(override val value: Double) : MassUnit, WeightUnit {  // special case, both mass and weight
    override fun toKilograms() = value

    override fun toKilogramsWeight() = value
}

// Weight
interface WeightUnit : Unit {
    fun toKilogramsWeight(): Double
}

inline class Pounds(override val value: Double) : WeightUnit {
    override fun toKilogramsWeight() = value * 0.453592
}

// Electrical (no interface because there is only one set of electrical units)
inline class Volts(override val value: Double) : Unit

inline class Amps(override val value: Double) : Unit

// Typealiases for ease of use
typealias M = Meters
typealias Ft = Feet
typealias In = Inches

typealias `M/s` = MetersPerSecond
typealias Kmph = KilometersPerHour
typealias Mph = MilesPerHour
typealias Fps = FeetPerSecond

typealias Kg = Kilograms

typealias Lb = Pounds
