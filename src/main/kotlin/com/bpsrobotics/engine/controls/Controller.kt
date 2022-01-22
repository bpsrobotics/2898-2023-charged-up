package com.bpsrobotics.engine.controls

import edu.wpi.first.math.controller.PIDController

/**
 * A closed-loop controller controlling a one-dimensional output,
 * usually a motor.
 */
interface Controller {
    var setpoint: Double

    fun calculate(current: Double, setpoint: Double = this.setpoint): Double

    class PID(private val wpiPID: PIDController) : Controller {
        constructor(kP: Double, kI: Double, kD: Double) : this(PIDController(kP, kI, kD))
        constructor(kP: Double, kD: Double) : this(kP, 0.0, kD)

        override var setpoint: Double
            get() = wpiPID.setpoint
            set(value) { wpiPID.setpoint = value }

        override fun calculate(current: Double, setpoint: Double): Double {
            return wpiPID.calculate(current)
        }
    }
}
