package com.teamXXXX.robot

import edu.wpi.first.wpilibj.XboxController
import kotlin.math.abs
import kotlin.math.sign

object OI {
    /**
     * The Operator Interface file.
     * This is where you put all of the joystick, button, or keyboard inputs.
     */

    /**
     * Utility function for controller axis, optional deadzone and square/cube for extra fine-grain control
     */
    private fun process(input: Double,
                        deadzone: Boolean = false,
                        square: Boolean = false,
                        cube: Boolean = false): Double {
        var output = 0.0

        if (deadzone) {
            output = if (abs(input) < 0.1) {
                0.0
            } else {
                input
            }
        }

        if (square) {
            output *= output * sign(output)
        }

        if (cube) {
            output *= output * output
        }

        return output
    }

    private val driverController = XboxController(0);

    // Right joystick y axis, probably.  Controller mapping can be tricky, the best way is to use the driver station to see what buttons and axis are being pressed.
    // This value is deadzoned and squared, like a drivetrain-controlling axis might be
    val exampleJoystickAxis get() = process(driverController.getRawAxis(5), deadzone = true, square = true)

    // Right switch (the one next to the trigger), probably
    val exampleButton get() = driverController.getRawButton(6)
}
