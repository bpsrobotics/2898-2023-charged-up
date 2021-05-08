package com.bpsrobotics.engine.async

import com.bpsrobotics.engine.utils.Millis
import com.bpsrobotics.engine.utils.Sugar.clamp
import edu.wpi.first.wpilibj.Timer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

object AsyncLooper {

    /**
     * Calls [lambda] every [delay] milliseconds using kotlin coroutines.
     * Uses [Timer.getFPGATimestamp] to make sure it runs at exactly the right pace (unless [lambda] takes too long).
     */
    fun loop(delay: Millis, lambda: suspend () -> Unit): Job {
        return GlobalScope.launch {
            while (true) {
                val start = Timer.getFPGATimestamp()
                lambda()
                val timeTaken = Timer.getFPGATimestamp() - start
                val delayTime = delay.value - (timeTaken / 1000)
                delay(delayTime.clamp(0.0, delay.value).roundToLong())  // Make sure it isn't negative
            }
        }
    }
}
