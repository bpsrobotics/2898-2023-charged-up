package com.team2898.robot

import com.bpsrobotics.engine.async.AsyncLooper
import com.bpsrobotics.engine.utils.Millis
import com.bpsrobotics.engine.utils.Sugar.clamp
import com.team2898.robot.Constants.INTAKE_BUTTON
import edu.wpi.first.wpilibj.Joystick
import edu.wpi.first.wpilibj.XboxController
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sign
import kotlin.reflect.KProperty

/**
 * The Operator Interface object.
 * This is where you put all of the joystick, button, or keyboard inputs.
 */
object OI {

    /**
     * Threshold below which [process] will return 0.
     * 0.1 historically used, but optimal value unknown.
     */
    private const val DEADZONE_THRESHOLD = 0.1

    /**
     * Utility function for controller axis, optional deadzone and square/cube for extra fine-grain control
     */
    private fun process(input: Double,
                        deadzone: Boolean = false,
                        square: Boolean = false,
                        cube: Boolean = false): Double {
        var output = 0.0

        if (deadzone) {
            output = if (abs(input) < DEADZONE_THRESHOLD) {
                0.0
            } else {
                input
            }
        }

        if (square) {
            // To keep the signage for output, we multiply by sign(output). This keeps negative inputs resulting in negative outputs.
            output = output.pow(2) * sign(output)
        }

        if (cube) {
            // Because cubing is an odd number of multiplications, we don't need to multiply by sign(output) here.
            output = output.pow(3)
        }

        return output
    }

    /**
     * do not question the r a m p
     *
     * A way of ramping a given value to produce an output value that doesn't change by more than a
     * specified amount per second.  Use [ramp].
     */
    object Ramp {
        private var values = doubleArrayOf()
        private var lambdas = arrayOf<(() -> Double)?>()
        private var rates = doubleArrayOf()

        init {
            AsyncLooper.loop(Millis(1000L / 50), "ramper") {
                synchronized(Ramp) {
                    for (index in values.indices) {
                        val goal = lambdas[index]!!.invoke()
                        val rate = rates[index]
                        values[index] += (goal - values[index]).clamp(-rate, rate)
                    }
                }
            }
        }

        class Delegator internal constructor(lambda: () -> Double, rate: Double) {
            private val id: Int
            init {
                synchronized(Ramp) {
                    id = values.size
                    values = values.copyOf(values.size + 1)
                    rates = rates.copyOf(rates.size + 1)
                    rates[rates.size - 1] = rate
                    lambdas = lambdas.copyOf(lambdas.size + 1)
                    lambdas[lambdas.size - 1] = lambda
                }
            }

            operator fun getValue(thisRef: Any?, property: KProperty<*>): Double {
                synchronized(Ramp) {
                    return values[id]
                }
            }
        }

        fun ramp(perSecond: Double = 10.0, lambda: () -> Double) = Delegator(lambda, perSecond / 50)
    }

    private val driverController = XboxController(0)
    private val operatorController = Joystick(1)

    // Left and right shoulder switches (the ones next to the trigger) for quickturn
    val quickTurnRight get() = process(driverController.getRawAxis(3), deadzone = true, square = true)
    val quickTurnLeft get() = process(driverController.getRawAxis(2), deadzone = true, square = true)

    // Right joystick y-axis.  Controller mapping can be tricky, the best way is to use the driver station to see what buttons and axis are being pressed.
    // Squared for better control on turn, cubed on throttle
    val throttle get() = process(driverController.getRawAxis(5), deadzone = true, cube = true)
    val turn get() = process(driverController.getRawAxis(0), deadzone = true, square = true)

    val intake get() = if(operatorController.getRawButton(INTAKE_BUTTON)) 0.5 else 0.0
}
