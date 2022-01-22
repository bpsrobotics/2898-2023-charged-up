package com.team2898.robot.subsystems

import com.bpsrobotics.engine.controls.Controller
import com.bpsrobotics.engine.utils.Meters
import com.bpsrobotics.engine.utils.minus
import com.bpsrobotics.engine.utils.seconds
import com.team2898.robot.Constants.CLIMBER_1_LOADED
import com.team2898.robot.Constants.CLIMBER_1_UNLOADED
import com.team2898.robot.Constants.CLIMBER_2_LOADED
import com.team2898.robot.Constants.CLIMBER_2_UNLOADED
import edu.wpi.first.math.controller.ElevatorFeedforward
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.Encoder
import edu.wpi.first.wpilibj.PneumaticsModuleType
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup
import edu.wpi.first.wpilibj2.command.SubsystemBase

class Climb : SubsystemBase() {
    private val arm1 = Arm(
        MotorControllerGroup(arrayOf()),
        Encoder(4, 5),
        DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 1),
        CLIMBER_1_LOADED, CLIMBER_1_UNLOADED
    )

    private val arm2 = Arm(
        MotorControllerGroup(arrayOf()),
        Encoder(4, 5),
        DoubleSolenoid(PneumaticsModuleType.CTREPCM, 2, 3),
        CLIMBER_2_LOADED, CLIMBER_2_UNLOADED
    )

    private val piston1 = DoubleSolenoid(PneumaticsModuleType.CTREPCM, 4, 5)
    private val piston2 = DoubleSolenoid(PneumaticsModuleType.CTREPCM, 6, 7)

    fun pistons(value: DoubleSolenoid.Value) {
        piston1.set(value)
        piston2.set(value)
    }

    // TODO: move controller out of this class so I can keep it in Constants.kt without regrets
    data class ProfileManager(val controller: Controller, val feedforward: ElevatorFeedforward, val constraints: TrapezoidProfile.Constraints)

    private class Arm(
        private val motors: MotorControllerGroup,
        private val encoder: Encoder,
        private val brake: DoubleSolenoid,
        private val loaded: ProfileManager,
        private val unloaded: ProfileManager
    ) {
        var isLoaded = false
        private var profile: TrapezoidProfile? = null
        private var startTime = 0.seconds

        fun goTo(destination: Meters) {
            profile = if (isLoaded) {
                TrapezoidProfile(loaded.constraints, TrapezoidProfile.State(destination.value, 0.0))
            } else {
                TrapezoidProfile(unloaded.constraints, TrapezoidProfile.State(destination.value, 0.0))
            }
            startTime = Timer.getFPGATimestamp().seconds
        }

        fun update() {
            val p = profile ?: run {
                brake.set(DoubleSolenoid.Value.kForward)
                return
            }
            val time = Timer.getFPGATimestamp().seconds - startTime

            if (p.isFinished(time.value)) {
                motors.set(0.0)
                brake.set(DoubleSolenoid.Value.kForward)
                profile = null
                return
            }

            brake.set(DoubleSolenoid.Value.kReverse)
            val goal = p.calculate(time.value)

            val pid = if (isLoaded) {
                loaded.controller.calculate(encoder.rate)
            } else {
                unloaded.controller.calculate(encoder.rate)
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
