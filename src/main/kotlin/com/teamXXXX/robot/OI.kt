package com.teamXXXX.robot

import edu.wpi.first.wpilibj.XboxController
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sign
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

    private val driverController = XboxController(0)

    // Left and right shoulder switches (the ones next to the trigger) for quickturn
    val quickTurnRight get() = driverController.getRawButton(6)
    val quickTurnLeft get() = driverController.getRawButton(5)

    // Right joystick y axis, probably.  Controller mapping can be tricky, the best way is to use the driver station to see what buttons and axis are being pressed.
    // Squared for better control
    val throttle get() = process(driverController.getRawAxis(5), deadzone = true, square = true)
    val turn get() = process(driverController.getRawAxis(1), deadzone = true, square = true)
}
