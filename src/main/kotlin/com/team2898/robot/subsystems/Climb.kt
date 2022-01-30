package com.team2898.robot.subsystems

import com.bpsrobotics.engine.controls.Controller
import com.bpsrobotics.engine.controls.Controller.PID
import com.bpsrobotics.engine.utils.Meters
import com.bpsrobotics.engine.utils.minus
import com.bpsrobotics.engine.utils.seconds
import com.team2898.robot.Constants.CLIMBER_LOADED
import com.team2898.robot.Constants.CLIMBER_UNLOADED
import edu.wpi.first.math.controller.ElevatorFeedforward
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.math.trajectory.TrapezoidProfile.State
import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.Encoder
import edu.wpi.first.wpilibj.PneumaticsModuleType
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Climb : SubsystemBase() {
    private val arm1 = Arm(
        MotorControllerGroup(arrayOf()),
        Encoder(4, 5),
        DoubleSolenoid(PneumaticsModuleType.REVPH, 0, 1),
        CLIMBER_LOADED, CLIMBER_UNLOADED
    )

    private val arm2 = Arm(
        MotorControllerGroup(arrayOf()),
        Encoder(4, 5),
        DoubleSolenoid(PneumaticsModuleType.REVPH, 2, 3),
        CLIMBER_LOADED, CLIMBER_UNLOADED
    )

    private val piston1 = DoubleSolenoid(PneumaticsModuleType.REVPH, 4, 5)
    private val piston2 = DoubleSolenoid(PneumaticsModuleType.REVPH, 6, 7)

    fun pistons(value: DoubleSolenoid.Value) {
        piston1.set(value)
        piston2.set(value)
    }

    fun arms(value: Meters, loaded: Boolean) {
        arm1.goTo(value, loaded)
        arm2.goTo(value, loaded)
    }

    val isFinished get() = arm1.isFinished && arm2.isFinished

    data class ClimbControllerSpec(
        val kS: Double, val kP: Double, val kI: Double, val kD: Double,
        val feedforward: ElevatorFeedforward,
        val constraints: TrapezoidProfile.Constraints
    )

    private class Arm(
        private val motors: MotorControllerGroup,
        private val encoder: Encoder,
        private val brake: DoubleSolenoid,
        private val loaded: ClimbControllerSpec,
        private val unloaded: ClimbControllerSpec
    ) {
        var isLoaded = false
        private var profile =
            TrapezoidProfile(unloaded.constraints, State(0.0, 0.0), State(0.0, 0.0))
        private var startTime = 0.seconds

        val isFinished get() = profile.isFinished(Timer.getFPGATimestamp() - startTime.value)

        private val loadedPID: Controller = PID(loaded.kP, loaded.kI, loaded.kD)

        private val unloadedPID: Controller = PID(unloaded.kP, unloaded.kI, unloaded.kD)

        fun goTo(destination: Meters, useLoaded: Boolean) {
            isLoaded = useLoaded
            profile = if (isLoaded) {
                TrapezoidProfile(loaded.constraints, State(destination.value, 0.0))
            } else {
                TrapezoidProfile(unloaded.constraints, State(destination.value, 0.0))
            }
            startTime = Timer.getFPGATimestamp().seconds
        }

        fun update() {
            val p = profile
            val time = Timer.getFPGATimestamp().seconds - startTime

            if (p.isFinished(time.value)) {
                motors.set(0.0)
                brake.set(DoubleSolenoid.Value.kForward)
                return
            }

            brake.set(DoubleSolenoid.Value.kReverse)
            val goal = p.calculate(time.value)

            val pid = if (isLoaded) {
                loadedPID.calculate(encoder.rate)
            } else {
                unloadedPID.calculate(encoder.rate)
            }

            val ff = if (isLoaded) {
                loaded.feedforward.calculate(goal.velocity)
            } else {
                unloaded.feedforward.calculate(goal.velocity)
            }

            motors.set(pid + ff)
        }
    }
}
