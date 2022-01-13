package com.bpsrobotics.engine.async

import com.bpsrobotics.engine.utils.Millis
import com.bpsrobotics.engine.utils.Sugar.clamp
import edu.wpi.first.wpilibj.Timer
import kotlinx.coroutines.*
import kotlin.math.roundToLong

object AsyncLooper : Iterable<Pair<String, Job>> {
    /** A dictionary of jobs by name. */
    private val jobs = mutableMapOf<String, Job>()
    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * Calls [lambda] every [delay] milliseconds using kotlin coroutines.
     * Uses [Timer.getFPGATimestamp] to make sure it runs at exactly the right pace (unless [lambda] takes too long).
     */
    fun loop(delay: Millis, name: String, lambda: suspend () -> Unit): Job {
        val job = scope.launch { // Launch a coroutine (like a lighter weight thread)
            while (true) {
                val start = Timer.getFPGATimestamp()
                try {
                    lambda()
                } catch (e: Throwable) {
                    println("Exception in '$name':")
                    e.printStackTrace()
                }
                val timeTaken = Timer.getFPGATimestamp() - start
                val delayTime = delay.value - (timeTaken / 1000)
                if (delayTime < 0.0) {
                    println("Can't keep up with async loop '$name'!  Took $timeTaken ms, loop is trying to go every $delay ms")
                }
                delay(delayTime.clamp(0.0, delay.value).roundToLong())  // Make sure it isn't negative
            }
        }
        jobs[name] = job
        return job
    }

    /**
     * Gets a registered job by name.  May be null.
     *
     * Example usage:
     * ```
     * AsyncLooper.loop(Millis(50L), "job name") { println("AAA") }
     * AsyncLooper["job name"].cancel()
     * ```
     */
    operator fun get(name: String): Job? = jobs[name]

    /**
     * An iterator containing all the registered jobs and their names.
     */
    override fun iterator(): Iterator<Pair<String, Job>> {
        return jobs.entries.map { Pair(it.key, it.value) }.iterator()
    }
}
