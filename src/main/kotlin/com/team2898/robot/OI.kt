package com.team2898.robot

import com.bpsrobotics.engine.async.AsyncLooper
import com.bpsrobotics.engine.utils.Millis
import com.bpsrobotics.engine.utils.Sugar.clamp
import com.bpsrobotics.engine.utils.Volts
import com.bpsrobotics.engine.utils.seconds
import com.team2898.robot.Constants.DRIVER_MAP
import com.team2898.robot.OI.Ramp.ramp
import com.team2898.robot.commands.TargetAlign
import com.team2898.robot.subsystems.Climb
import com.team2898.robot.subsystems.Feeder
import com.team2898.robot.subsystems.Intake
import com.team2898.robot.subsystems.Shooter
import edu.wpi.first.wpilibj.*
import edu.wpi.first.wpilibj.GenericHID.RumbleType.kLeftRumble
import edu.wpi.first.wpilibj.XboxController.Button.*
import edu.wpi.first.wpilibj2.command.StartEndCommand
import edu.wpi.first.wpilibj2.command.SubsystemBase
import edu.wpi.first.wpilibj2.command.button.JoystickButton
import edu.wpi.first.wpilibj2.command.button.Trigger
import kotlin.math.abs
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
    private const val DEADZONE_THRESHOLD = 0.05
    private const val TRIG_DEADZONE_THRESHOLD = 0.05

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

    // conflicts with the other definition, name it something else after compilation
    @JvmName("process1")
    fun Double.process(deadzone: Boolean = false, square: Boolean = false, cube: Boolean = false) = process(this, deadzone, square, cube)

    private val driverController = XboxController(0)
    private val operatorController = Joystick(1)

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

    // Left and right shoulder switches (the ones next to the trigger) for quickturn
    val quickTurnRight
        get() = if (DRIVER_MAP == Constants.DriverMap.FORZA) {
            process(if (driverController.rightBumper) 1.0 else 0.0)
        } else {
            process(driverController.rightTriggerAxis, deadzone = true, square = true)
        }
    val quickTurnLeft
        get() = if (DRIVER_MAP == Constants.DriverMap.FORZA) {
            process(if (driverController.leftBumper) 1.0 else 0.0)
        } else {
            process(driverController.leftTriggerAxis, deadzone = true, square = true)
        }

    // Right joystick y-axis.  Controller mapping can be tricky, the best way is to use the driver station to see what buttons and axis are being pressed.
    // Squared for better control on turn, cubed on throttle
    val throttle
        get() = if (DRIVER_MAP == Constants.DriverMap.FORZA) {
            process(
                if (driverController.rightTriggerAxis > TRIG_DEADZONE_THRESHOLD || driverController.leftTriggerAxis > TRIG_DEADZONE_THRESHOLD) {
                    driverController.rightTriggerAxis - driverController.leftTriggerAxis
                } else {
                    0.0
                },
                deadzone = true
            )
        } else {
            process(-driverController.leftY, deadzone = true)
        }
    val turn get() = if (DRIVER_MAP == Constants.DriverMap.FORZA) {
        process(driverController.leftX, deadzone = true, square = true)
    } else {
        process(driverController.rightX, deadzone = true, square = true)
    }

    /*
    * POV stick to run intake wheels, mystery button to deploy/retract intake
    * forward rightmost button is climb mode
    * right 2 close buttons for climb actuation (only enabled in climb mode)
    * left 3 buttons for manual speeds
    *
    */

    /**
     * A property delegate (see top of the file for info) that returns true when the lambda switches to true,
     * and false otherwise.
     */
    class LeadingEdge(private val lambda: () -> Boolean) {
        private var lastValue = false

        operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
            val res = lambda()
            if (res && !lastValue) {
                lastValue = true
                return true
            }
            lastValue = res
            return false
        }
    }

    class Toggle(private val lambda: () -> Boolean) {
        private var value = false
        private var lastValue = false

        operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
            val res = lambda()
            lastValue = if (res && !lastValue) {
                value = !value
                true
            } else {
                res
            }
            return value
        }
    }

    val climbMode by Toggle { operatorController.getRawButton(8) }
    val climbModeTrigger = Trigger { climbMode }

    val climbMove get() = if (climbMode) operatorController.y else 0.0
    val climbPistonForward = Trigger { if (climbMode) operatorController.getRawButtonPressed(11) else false }
    val climbPistonReverse = Trigger { if (climbMode) operatorController.getRawButtonPressed(12) else false }

    init {
        climbPistonForward.whenActive({ Climb.pistons(DoubleSolenoid.Value.kForward) }, Climb)
        climbPistonReverse.whenActive({ Climb.pistons(DoubleSolenoid.Value.kReverse) }, Climb)
        climbModeTrigger.whileActiveContinuous({ Climb.openLoop(Volts(-climbMove * 2.0)) }, Climb)
    }

    val intakeTrigger = JoystickButton(operatorController, 1)

    init {
        intakeTrigger.whenActive(Intake::startIntake).whenInactive(Intake::stopIntake)
    }

    val intakeDown = Trigger { operatorController.pov != -1 }

    init {
        intakeDown.toggleWhenActive(StartEndCommand(Intake::openIntake, Intake::closeIntake, Intake))
    }

    val spinUpButton = JoystickButton(driverController, kY.value)
    val dumpSpinUpButton = JoystickButton(driverController, kA.value)
    val cancelButton = JoystickButton(driverController, kX.value)
    val shootButton = JoystickButton(driverController, kB.value)

    val rumbleTrigger = Trigger { Shooter.ready }

    init {
        spinUpButton.whileActiveContinuous(Shooter::spinUp)
        dumpSpinUpButton.whileActiveContinuous(Shooter::spinUp)
//        cancelButton.whileActiveContinuous(Shooter::stopShooter)
        shootButton.whenActive(Feeder::shoot)
//        rumbleTrigger.whileActiveOnce(
//            StartEndCommand(
//                { driverController.setRumble(kLeftRumble, 0.2) },
//                { driverController.setRumble(kLeftRumble, 0.0) }
//            ).withTimeout(1.0)
//        )
    }

    val targetAlignButton = JoystickButton(driverController, kLeftBumper.value).and(JoystickButton(driverController, kRightBumper.value))

    init {
        targetAlignButton.whenActive(TargetAlign().until(targetAlignButton.negate()))
    }

    val manualShoot by object {
        // The time that the driver last had a manual speed button pressed
        var lastUpdated = 0.seconds
        // The return value, kept around so that it can be repeated for a few seconds after it's released
        var value = -1.0

        // Whenever the value is read, run this
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Double {
            val newValue = when {
                operatorController.getRawButton(1) -> 1.0
                operatorController.getRawButton(2) -> 1.5
                operatorController.getRawButton(3) -> 2.0
                else -> -1.0
            }

            if (newValue != -1.0) {
                // If a speed is selected, reset the timeout and set the output value.
                lastUpdated = Timer.getFPGATimestamp().seconds
                value = newValue
            } else {
                // Otherwise, check if the timeout has expired.  If it has, set the value to -1 (no manual speed)
                if (Timer.getFPGATimestamp() - lastUpdated.value > 5.0) {
                    value = -1.0
                }
            }
            return value
        }
    }
}
