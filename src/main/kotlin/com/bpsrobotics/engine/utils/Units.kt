@file:Suppress("unused", "MemberVisibilityCanBePrivate", "NOTHING_TO_INLINE")  // Not all of these classes are used

/**
 * A set of value classes that represent a value with a unit.  These value classes are represented
 * at runtime as java primitive doubles, so they don't require heap ram allocation and are performant.
 *
 * Note: if you specify an argument type as one of the interfaces declared in this file, it will box
 * the type, leading to a performance hit.  _This is likely fine_, because computers are fast.
 */
package com.bpsrobotics.engine.utils

import kotlin.math.PI

interface Unit {
    val value: Double
}

// Distance
interface DistanceUnit : Unit {
    fun meterValue(): Double
}

@JvmInline
value class Meters(override val value: Double) : DistanceUnit {
    override fun meterValue() = value
}

@JvmInline
value class Feet(override val value: Double) : DistanceUnit {
    override fun meterValue() = value * 0.3048
}

@JvmInline
value class Inches(override val value: Double) : DistanceUnit {
    override fun meterValue() = value * 0.0254
}

// Velocity
interface VelocityUnit : Unit {
    fun metersPerSecondValue(): Double
}

@JvmInline
value class MetersPerSecond(override val value: Double) : VelocityUnit {
    override fun metersPerSecondValue() = value
}

@JvmInline
value class FeetPerSecond(override val value: Double) : VelocityUnit {
    override fun metersPerSecondValue() = value * 0.3084
}

@JvmInline
value class KilometersPerHour(override val value: Double) : VelocityUnit {
    override fun metersPerSecondValue() = value / 3.6
}

@JvmInline
value class MilesPerHour(override val value: Double) : VelocityUnit {
    override fun metersPerSecondValue() = value * 0.44704
}

// Acceleration
interface AccelerationUnit : Unit {
    fun metersPerSecondSquaredValue(): Double
}

@JvmInline
value class MetersPerSecondSquared(override val value: Double) : AccelerationUnit {
    override fun metersPerSecondSquaredValue() = value
}

// Angles
interface AngleUnit : Unit {
    fun radiansValue(): Double
}

@JvmInline
value class Radians(override val value: Double) : AngleUnit {
    override fun radiansValue() = value
}

@JvmInline
value class Degrees(override val value: Double) : AngleUnit {
    companion object {
        const val DEGREES_TO_RADS = (2 * PI) / 360
    }

    override fun radiansValue() = value * DEGREES_TO_RADS
}

// Mass
interface MassUnit : Unit {
    fun kilogramsValue(): Double
}

@JvmInline
value class Kilograms(override val value: Double) : MassUnit, WeightUnit {  // special case, both mass and weight
    override fun kilogramsValue() = value

    override fun kilogramsWeightValue() = value
}

// Weight
interface WeightUnit : Unit {
    fun kilogramsWeightValue(): Double
}

@JvmInline
value class Pounds(override val value: Double) : WeightUnit {
    override fun kilogramsWeightValue() = value * 0.453592
}

// Time
interface TimeUnit : Unit {
    fun secondsValue(): Double
}

@JvmInline
value class Seconds(override val value: Double) : TimeUnit {
    override fun secondsValue() = value
}

@JvmInline
value class Milliseconds(override val value: Double) : TimeUnit {
    /**
     * Creates a new [Milliseconds], but with a long input instead.  It's converted to a double,
     * this is purely so you don't have to specify 10.0 milliseconds, which would be weird.
     */
    constructor(count: Long) : this(count.toDouble())

    override fun secondsValue() = value / 1000
}

// Electrical (no interface because there is only one set of electrical units)
@JvmInline
value class Volts(override val value: Double) : Unit

@JvmInline
value class Amps(override val value: Double) : Unit

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

typealias Millis = Milliseconds


// Conversion functions
fun DistanceUnit.toMeters() = Meters(meterValue())

fun VelocityUnit.toMetersPerSecond() = `M/s`(metersPerSecondValue())
// TODO: more

inline val Double.volts get() = Volts(this)
inline val Double.seconds get() = Seconds(this)
inline val Int.seconds get() = Seconds(toDouble())

inline operator fun Seconds.minus(other: Seconds) = (value - other.value).seconds

inline operator fun Volts.plus(other: Volts) = (value + other.value).volts
