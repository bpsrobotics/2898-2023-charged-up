package com.team2898.robot.subsystems

import com.bpsrobotics.engine.controls.Controller
import com.bpsrobotics.engine.controls.Controller.PID
import com.bpsrobotics.engine.controls.StallDetection
import com.bpsrobotics.engine.utils.*
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import com.team2898.robot.RobotMap.CLIMBER_LEFT_ENCODER_A
import com.team2898.robot.RobotMap.CLIMBER_LEFT_ENCODER_B
import com.team2898.robot.RobotMap.CLIMBER_LEFT_LIMIT_SWITCH
import com.team2898.robot.RobotMap.CLIMBER_LEFT_MAIN
import com.team2898.robot.RobotMap.CLIMBER_LEFT_SECONDARY
import com.team2898.robot.RobotMap.CLIMBER_RIGHT_LIMIT_SWITCH
import com.team2898.robot.RobotMap.CLIMBER_RIGHT_MAIN
import com.team2898.robot.RobotMap.CLIMBER_RIGHT_SECONDARY
import com.team2898.robot.Constants.CLIMBER_LOADED
import com.team2898.robot.RobotMap.CLIMBER_RIGHT_ENCODER_A
import com.team2898.robot.RobotMap.CLIMBER_RIGHT_ENCODER_B
import com.team2898.robot.Constants.CLIMBER_UNLOADED
import com.team2898.robot.RobotMap.CLIMB_L_FORWARD
import com.team2898.robot.RobotMap.CLIMB_L_REVERSE
import com.team2898.robot.RobotMap.CLIMB_R_FORWARD
import com.team2898.robot.RobotMap.CLIMB_R_REVERSE
import edu.wpi.first.math.controller.ElevatorFeedforward
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.math.trajectory.TrapezoidProfile.State
import edu.wpi.first.wpilibj.*
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Climb : SubsystemBase() {

    private val leftArmMain = WPI_TalonSRX(CLIMBER_LEFT_MAIN)
    private val leftArmSecondary = WPI_TalonSRX(CLIMBER_LEFT_SECONDARY)
    private val rightArmMain = WPI_TalonSRX(CLIMBER_RIGHT_MAIN)
    private val rightArmSecondary = WPI_TalonSRX(CLIMBER_RIGHT_SECONDARY)

    init {
        listOf(leftArmMain, leftArmSecondary, rightArmMain, rightArmSecondary).forEach {
            it.configFactoryDefault()
            it.configContinuousCurrentLimit(10)
            it.configPeakCurrentLimit(30, 50)
            it.enableVoltageCompensation(true)
        }
    }

    fun openLoop(value: Volts) {
        leftArm.openLoop(value)
        rightArm.openLoop(value)
    }

    private val leftArm = Arm(
        listOf(leftArmMain, leftArmSecondary),
        Encoder(CLIMBER_LEFT_ENCODER_A, CLIMBER_LEFT_ENCODER_B),
        CLIMBER_LOADED, CLIMBER_UNLOADED,
        DigitalInput(CLIMBER_LEFT_LIMIT_SWITCH)
    )

    private val rightArm = Arm(
        listOf(rightArmMain, rightArmSecondary),
        Encoder(CLIMBER_RIGHT_ENCODER_A, CLIMBER_RIGHT_ENCODER_B),
        CLIMBER_LOADED, CLIMBER_UNLOADED,
        DigitalInput(CLIMBER_RIGHT_LIMIT_SWITCH)
    )

    private val piston1 = DoubleSolenoid(PneumaticsModuleType.REVPH, CLIMB_L_FORWARD, CLIMB_L_REVERSE)
    private val piston2 = DoubleSolenoid(PneumaticsModuleType.REVPH, CLIMB_R_FORWARD, CLIMB_R_REVERSE)

    fun pistons(value: DoubleSolenoid.Value) {
        piston1.set(value)
        piston2.set(value)
        Intake.openIntake()
    }

    fun arms(value: Meters, loaded: Boolean) {
        leftArm.goTo(value, loaded)
        rightArm.goTo(value, loaded)
    }

    val isFinished get() = leftArm.isFinished && rightArm.isFinished

    data class ClimbControllerSpec(
        val kS: Double, val kP: Double, val kI: Double, val kD: Double,
        val feedforward: ElevatorFeedforward,
        val constraints: TrapezoidProfile.Constraints
    )

    private class Arm(
        private val motors: List<WPI_TalonSRX>,
        private val encoder: Encoder,
        private val loaded: ClimbControllerSpec,
        private val unloaded: ClimbControllerSpec,
        private val limitSwitch: DigitalInput
    ) {
        var isLoaded = false
        private var profile =
            TrapezoidProfile(unloaded.constraints, State(0.0, 0.0), State(0.0, 0.0))
        private var startTime = 0.seconds
        private var lastLimitSwitchValue = false
        private val stallDetector = StallDetection(Millis(1000))
        private var stallTimeout = 0.seconds

        enum class Mode {
            CLOSED_LOOP, OPEN_LOOP
        }

        var mode = Mode.OPEN_LOOP

        val isFinished get() = profile.isFinished(Timer.getFPGATimestamp() - startTime.value)

        private val loadedPID: Controller = PID(loaded.kP, loaded.kI, loaded.kD)

        private val unloadedPID: Controller = PID(unloaded.kP, unloaded.kI, unloaded.kD)

        fun openLoop(value: Volts) {
            if (mode == Mode.CLOSED_LOOP) return
            motors.forEach { it.setVoltage(value.value) }
        }

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
            if (stallDetector.isStalled(motors.first().motorOutputPercent, encoder.distance)) {
                stallTimeout = Timer.getFPGATimestamp().seconds + 5.seconds
            }

            if (Timer.getFPGATimestamp() < stallTimeout.value) {
                motors.forEach { it.set(0.0) }
                return
            }

            val limitSwitchValue = limitSwitch.get()
            val leadingEdge = limitSwitchValue && !lastLimitSwitchValue
            lastLimitSwitchValue = limitSwitchValue

            if (leadingEdge) {
                encoder.reset()
            }

            if (mode == Mode.OPEN_LOOP) {
                if (limitSwitchValue && motors.first().motorOutputPercent < 0) {
                    motors.forEach { it.set(0.0) }
                }
                return
            }

            val p = profile
            val time = Timer.getFPGATimestamp().seconds - startTime

            if (p.isFinished(time.value)) {
                motors.forEach { it.set(0.0) }
                return
            }

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

            var out = pid + ff

            if (limitSwitchValue) out = out.coerceAtLeast(0.0)

            motors.forEach { it.set(out) }
        }
    }

    override fun periodic() {
        leftArm.update()
        rightArm.update()
    }
}
