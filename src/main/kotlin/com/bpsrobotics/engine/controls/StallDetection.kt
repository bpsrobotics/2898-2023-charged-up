package com.bpsrobotics.engine.controls

import com.bpsrobotics.engine.utils.Millis
import com.bpsrobotics.engine.utils.Volts
import edu.wpi.first.wpilibj.Timer
import kotlin.math.absoluteValue

class StallDetection(private val timeout: Millis, threshold: Volts = Volts(1.0)) {
    private val enabledTimer = Timer()
    private var running = false
    private var lastMotorPower = 0.0
    private val runningThreshold = threshold.value / 12
    private var lastValue = 0.0

    fun isStalled(motorPower: Double, encoderValue: Double): Boolean {
        val motorPreviouslyRunning = lastMotorPower.absoluteValue > runningThreshold
        val currentlyRunning = motorPower > runningThreshold

        if (!motorPreviouslyRunning && currentlyRunning) {
            enabledTimer.reset()
            enabledTimer.start()
            running = true
        }

        if (!currentlyRunning) {
            enabledTimer.stop()
            running = false
        }

        if (lastValue != encoderValue) {
            enabledTimer.reset()
        }

        lastValue = encoderValue

        return running && enabledTimer.get() > timeout.secondsValue()
    }
}
