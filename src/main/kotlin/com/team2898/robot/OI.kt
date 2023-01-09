package com.team2898.robot

import edu.wpi.first.math.MathUtil
import edu.wpi.first.wpilibj.Joystick
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj2.command.SubsystemBase
import kotlin.math.pow
import kotlin.math.sign
import kotlin.reflect.KProperty

/**
 * The Operator Interface object.
 * This is where you put all of the joystick, button, or keyboard inputs.
 *
 * A note about delegated properties, which are used in this object:
 *  A delegated property is where getting (or setting) a field is outsourced
 *  to another object.  Then, whenever you read the property, it asks the
 *  object the property is delegated to for the value.
 */
object OI : SubsystemBase() {
    /**
     * Threshold below which [process] will return 0.
     * 0.1 historically used, but optimal value unknown.
     */
    private const val DEADZONE_THRESHOLD = 0.1

    /**
     * Utility function for controller axis, optional deadzone and square/cube for extra fine-grain control
     */
    private fun process(
        input: Double,
        deadzone: Boolean = false,
        square: Boolean = false,
        cube: Boolean = false
    ): Double {
        var output = 0.0

        if (deadzone) {
            output = MathUtil.applyDeadband(input, DEADZONE_THRESHOLD)
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

    // conflicts with the other definition, name it something else after compilation
    @JvmName("process1")
    fun Double.process(deadzone: Boolean = false, square: Boolean = false, cube: Boolean = false) =
        process(this, deadzone, square, cube)

    private val driverController = XboxController(0)
    private val operatorController = Joystick(1)

    // Left and right shoulder switches (the ones next to the trigger) for quickturn
    val quickTurnRight
        get() = process(driverController.rightTriggerAxis, deadzone = true, square = true)
    val quickTurnLeft
        get() = process(driverController.leftTriggerAxis, deadzone = true, square = true)

    // Right joystick y-axis.  Controller mapping can be tricky, the best way is to use the driver station to see what buttons and axis are being pressed.
    // Squared for better control on turn, cubed on throttle
    val throttle
        get() = process(-driverController.leftY, deadzone = true, square = true)
    val turn
        get() = process(driverController.rightX, deadzone = true, square = true)

    val outtakeButton get() = operatorController.getRawButton(12)
    val overrideOpenGate get() = operatorController.getRawButton(9)
    val overrideClosedGate get() = operatorController.getRawButton(10)

    val  intakeForward get() = operatorController.trigger

    val intakeBackward get() = operatorController.getRawButton(2)
    val alignButton get() = driverController.bButton

    var defenseMode by object {
        var lastValue = false
        var v = false

        operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
            val inp = driverController.aButton
            if (lastValue != inp) {
                if (inp) {
                    v = !v
                    println("defense mode: $v")
                }
                lastValue = inp
            }

            return v
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, v: Boolean) {
            this.v = v
        }
    }
}
