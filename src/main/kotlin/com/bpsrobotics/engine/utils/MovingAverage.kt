package com.bpsrobotics.engine.utils

import com.bpsrobotics.engine.async.AsyncLooper
import kotlin.reflect.KProperty

@Suppress("NOTHING_TO_INLINE")
class MovingAverage(size: Int) {
    private val buffer = DoubleArray(size)
    private var i = 0

    var average = 0.0
        private set

    fun add(v: Double) {
        buffer[i++ % buffer.size] = v
        average = buffer.average()
    }

    inline operator fun getValue(thisRef: Any?, property: KProperty<*>): Double = average

    companion object {
        fun movingAverage(size: Int, frequency: Hertz = 50.hz, getter: () -> Double): MovingAverage {
            val v = MovingAverage(size)
            AsyncLooper.loop(frequency.toMillis(), "movingAverage") {
                v.add(getter())
            }
            return v
        }
    }
}
