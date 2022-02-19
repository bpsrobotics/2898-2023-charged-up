@file:Suppress("unused", "MemberVisibilityCanBePrivate", "NOTHING_TO_INLINE")  // Not all of these classes are used

/**
 * A set of value classes that represent a value with a unit.  These value classes are represented
 * at runtime as java primitive doubles, so they don't require heap ram allocation and are performant.
 *
 * Note: if you specify an argument type as one of the interfaces declared in this file, it will box
 * the type, leading to a performance hit.  _This is likely fine_, because computers are fast.
 */
package com.bpsrobotics.engine.utils

import com.bpsrobotics.engine.utils.Sugar.radiansToDegrees
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

    override fun toString(): String {
        return "$value m"
    }
}

@JvmInline
value class Feet(override val value: Double) : DistanceUnit {
    override fun meterValue() = value * 0.3048

    override fun toString(): String {
        return "$value ft"
    }
}

@JvmInline
value class Inches(override val value: Double) : DistanceUnit {
    override fun meterValue() = value * 0.0254

    override fun toString(): String {
        return "$value in"
    }
}

// Velocity
interface VelocityUnit : Unit {
    fun metersPerSecondValue(): Double
}

@JvmInline
value class MetersPerSecond(override val value: Double) : VelocityUnit {
    override fun metersPerSecondValue() = value

    override fun toString(): String {
        return "$value m/s"
    }
}

@JvmInline
value class FeetPerSecond(override val value: Double) : VelocityUnit {
    override fun metersPerSecondValue() = value * 0.3084

    override fun toString(): String {
        return "$value ft/s"
    }
}

@JvmInline
value class KilometersPerHour(override val value: Double) : VelocityUnit {
    override fun metersPerSecondValue() = value / 3.6
}

@JvmInline
value class MilesPerHour(override val value: Double) : VelocityUnit {
    override fun metersPerSecondValue() = value * 0.44704

    override fun toString(): String {
        return "$value mph"
    }
}

// Acceleration
interface AccelerationUnit : Unit {
    fun metersPerSecondSquaredValue(): Double
}

@JvmInline
value class MetersPerSecondSquared(override val value: Double) : AccelerationUnit {
    override fun metersPerSecondSquaredValue() = value

    override fun toString(): String {
        return "$value m/s^2"
    }
}

// Rotations
interface Rotations : Unit {
    fun rotationsPerMinute(): Double
}

@JvmInline
value class RPM(override val value: Double) : Rotations {
    override fun rotationsPerMinute() = value
}

// Angles
interface AngleUnit : Unit {
    fun radiansValue(): Double
}

@JvmInline
value class Radians(override val value: Double) : AngleUnit {
    override fun radiansValue() = value

    override fun toString(): String {
        return "$value rad"
    }
}

@JvmInline
value class Degrees(override val value: Double) : AngleUnit {
    companion object {
        const val DEGREES_TO_RADS = (2 * PI) / 360
    }

    override fun radiansValue() = value * DEGREES_TO_RADS

    override fun toString(): String {
        return "$value deg"
    }
}

// Mass
interface MassUnit : Unit {
    fun kilogramsValue(): Double
}

@JvmInline
value class Kilograms(override val value: Double) : MassUnit, WeightUnit {  // special case, both mass and weight
    override fun kilogramsValue() = value

    override fun kilogramsWeightValue() = value

    override fun toString(): String {
        return "$value kg"
    }
}

// Weight
interface WeightUnit : Unit {
    fun kilogramsWeightValue(): Double
}

@JvmInline
value class Pounds(override val value: Double) : WeightUnit {
    override fun kilogramsWeightValue() = value * 0.453592

    override fun toString(): String {
        return "$value lbs"
    }
}

// Time
interface TimeUnit : Unit {
    fun secondsValue(): Double
}

@JvmInline
value class Seconds(override val value: Double) : TimeUnit {
    override fun secondsValue() = value

    override fun toString(): String {
        return "$value s"
    }
}

// Frequency
interface FrequencyValue : Unit {
    fun hertzValue(): Double
}

@JvmInline
value class Hertz(override val value: Double) : FrequencyValue {
    override fun hertzValue() = value

    override fun toString(): String {
        return "$value Hz"
    }
}

@JvmInline
value class Milliseconds(override val value: Double) : TimeUnit {
    /**
     * Creates a new [Milliseconds], but with a long input instead.  It's converted to a double,
     * this is purely so you don't have to specify 10.0 milliseconds, which would be weird.
     */
    constructor(count: Long) : this(count.toDouble())

    override fun secondsValue() = value / 1000

    override fun toString(): String {
        return "$value ms"
    }
}

// Electrical (no interface because there is only one set of electrical units)
@JvmInline
value class Volts(override val value: Double) : Unit {
    override fun toString(): String {
        return "$value v"
    }
}

@JvmInline
value class Amps(override val value: Double) : Unit {
    override fun toString(): String {
        return "$value a"
    }
}

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

fun Radians.toDegrees() = Degrees(value.radiansToDegrees())
// TODO: more

inline val Double.volts get() = Volts(this)
inline val Double.seconds get() = Seconds(this)
inline val Int.seconds get() = Seconds(toDouble())
inline val Double.m get() = Meters(this)
inline val Int.m get() = Meters(toDouble())
inline val Double.deg get() = Degrees(this)
inline val Int.deg get() = Degrees(toDouble())
inline val Int.rad get() = Radians(toDouble())
inline val Double.rad get() = Radians(this)
inline val Double.hz get() = Hertz(this)
inline val Int.hz get() = Hertz(toDouble())
inline val Int.RPM get() = RPM(toDouble())
inline val Double.RPM get() = RPM(this)

inline operator fun Seconds.minus(other: Seconds) = (value - other.value).seconds

inline operator fun Volts.plus(other: Volts) = (value + other.value).volts

inline operator fun Radians.plus(other: Radians) = (value + other.value).rad

inline operator fun Radians.minus(other: Radians) = (value - other.value).rad

inline operator fun Meters.plus(other: Meters) = (value + other.value).m

inline operator fun Meters.minus(other: Meters) = (value - other.value).m

inline operator fun Meters.unaryMinus() = Meters(-value)

inline operator fun RPM.unaryMinus() = (-value).RPM

inline operator fun RPM.plus(other: RPM) = (value + other.value).RPM

inline operator fun RPM.minus(other: RPM) = (value - other.value).RPM

@JvmInline
value class RGBA(val packed: UInt) {
    constructor(r: UByte, g: UByte, b: UByte, a: UByte = 255.toUByte()) : this(r.toUInt() or (g.toUInt() shl 8) or (b.toUInt() shl 16) or (a.toUInt() shl 24))

    constructor(r: Int, g: Int, b: Int) : this(r.toUByte(), g.toUByte(), b.toUByte())

    val r get() = packed.toUByte()
    val g get() = (packed shr 8).toUByte()
    val b get() = (packed shr 16).toUByte()
    val a get() = (packed shr 24).toUByte()
}

@JvmInline
value class HSVA(val packed: UInt) {
    /**
     * s, b, and a are from 0 to 255, h is from 0 to 180
     */
    constructor(h: Int, s: UByte, b: UByte, a: UByte = 255.toUByte()) : this(h.toUInt() or (s.toUInt() shl 8) or (b.toUInt() shl 16) or (a.toUInt() shl 24))

    /**
     * s, b, and a are from 0 to 255, h is from 0 to 180
     */
    constructor(h: Int, s: Int, v: Int) : this(h, s.toUByte(), v.toUByte())

    /** 0 to 180 */
    val h get() = packed.toUByte()
    val s get() = (packed shr 8).toUByte()
    val v get() = (packed shr 16).toUByte()
    val a get() = (packed shr 24).toUByte()
}

@Suppress("EXPERIMENTAL_API_USAGE", "FINAL_UPPER_BOUND")
@JvmInline
value class RGBAArray(val array: UIntArray) {
    constructor(size: Int) : this(UIntArray(size))

    companion object {
        // this is dumb, but inline classes can't be varargs, so we trick it into having them be objects
        fun <T : RGBA> of(vararg colors: T): RGBAArray {
            return RGBAArray(UIntArray(colors.size) { colors[it].packed })
        }
    }

    val size get() = array.size
    val indices get() = array.indices

    operator fun get(index: Int) = RGBA(array[index])

    operator fun set(index: Int, value: RGBA) {
        array[index] = value.packed
    }

    fun fill(value: RGBA, fromIndex: Int = 0, toIndex: Int = size - 1) {
        array.fill(value.packed, fromIndex, toIndex)
    }
}
