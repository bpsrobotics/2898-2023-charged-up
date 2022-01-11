package com.team2898.robot.subsystems

import com.bpsrobotics.engine.controls.Controller
import com.bpsrobotics.engine.controls.Ramsete
import com.bpsrobotics.engine.controls.TrajectoryMaker
import com.bpsrobotics.engine.utils.minus
import com.bpsrobotics.engine.utils.seconds
import com.bpsrobotics.engine.utils.toMeters
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushed
import com.team2898.robot.Constants.DRIVETRAIN_CONTINUOUS_CURRENT_LIMIT
import com.team2898.robot.Constants.DRIVETRAIN_KA
import com.team2898.robot.Constants.DRIVETRAIN_KD
import com.team2898.robot.Constants.DRIVETRAIN_KP
import com.team2898.robot.Constants.DRIVETRAIN_KS
import com.team2898.robot.Constants.DRIVETRAIN_KV
import com.team2898.robot.Constants.DRIVETRAIN_LEFT_ENCODER_A
import com.team2898.robot.Constants.DRIVETRAIN_LEFT_ENCODER_B
import com.team2898.robot.Constants.DRIVETRAIN_LEFT_MAIN
import com.team2898.robot.Constants.DRIVETRAIN_LEFT_SECONDARY
import com.team2898.robot.Constants.DRIVETRAIN_MAX_ACCELERATION
import com.team2898.robot.Constants.DRIVETRAIN_MAX_VELOCITY
import com.team2898.robot.Constants.DRIVETRAIN_PEAK_CURRENT_LIMIT
import com.team2898.robot.Constants.DRIVETRAIN_PEAK_CURRENT_LIMIT_DURATION
import com.team2898.robot.Constants.DRIVETRAIN_RIGHT_ENCODER_A
import com.team2898.robot.Constants.DRIVETRAIN_RIGHT_ENCODER_B
import com.team2898.robot.Constants.DRIVETRAIN_RIGHT_MAIN
import com.team2898.robot.Constants.DRIVETRAIN_RIGHT_SECONDARY
import com.team2898.robot.Constants.DRIVETRAIN_TRACK_WIDTH
import edu.wpi.first.wpilibj.Encoder
import edu.wpi.first.wpilibj.SpeedController
import edu.wpi.first.wpilibj.SpeedControllerGroup
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import edu.wpi.first.wpilibj.trajectory.Trajectory
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Drivetrain : SubsystemBase() {

    private val leftMain: SpeedController = CANSparkMax(DRIVETRAIN_LEFT_MAIN, kBrushed)
    private val leftSecondary: SpeedController = CANSparkMax(DRIVETRAIN_LEFT_SECONDARY, kBrushed)
    private val rightMain: SpeedController = CANSparkMax(DRIVETRAIN_RIGHT_MAIN, kBrushed)
    private val rightSecondary: SpeedController = CANSparkMax(DRIVETRAIN_RIGHT_SECONDARY, kBrushed)

    private val left  = SpeedControllerGroup(leftMain,  leftSecondary)
    private val right = SpeedControllerGroup(rightMain, rightSecondary)

    val leftEncoder  = Encoder(DRIVETRAIN_LEFT_ENCODER_A,  DRIVETRAIN_LEFT_ENCODER_B)
    val rightEncoder = Encoder(DRIVETRAIN_RIGHT_ENCODER_A, DRIVETRAIN_RIGHT_ENCODER_B)

    val trajectoryMaker = TrajectoryMaker(DRIVETRAIN_MAX_VELOCITY, DRIVETRAIN_MAX_ACCELERATION)

    private val ramsete: Ramsete = Ramsete(
        DRIVETRAIN_TRACK_WIDTH.toMeters(),
        Odometry,
        Controller.PID(DRIVETRAIN_KP, DRIVETRAIN_KD),
        Controller.PID(DRIVETRAIN_KP, DRIVETRAIN_KD),
        SimpleMotorFeedforward(DRIVETRAIN_KS.value, DRIVETRAIN_KV, DRIVETRAIN_KA),
        SimpleMotorFeedforward(DRIVETRAIN_KS.value, DRIVETRAIN_KV, DRIVETRAIN_KA)
    )

    private var trajectory: Trajectory? = null
    private var startTime = 0.seconds

    private var mode = Mode.DISABLED

    enum class Mode {
        OPEN_LOOP, CLOSED_LOOP, DISABLED
    }

    /** Computes left and right throttle from driver controller turn and throttle inputs. */
    private val differentialDrive = DifferentialDrive(left, right)

    /** Initializes motor configurations. */
    init {
        applyToMotors {
            if (this is WPI_TalonSRX) {
                configFactoryDefault()
                // Configure current limits to prevent motors stalling and overheating/breaking something or browning out the robot
                configContinuousCurrentLimit(DRIVETRAIN_CONTINUOUS_CURRENT_LIMIT)
                // Have a higher peak current limit for accelerating and starting, but it's only allowed for a short amount of time
                configPeakCurrentLimit(DRIVETRAIN_PEAK_CURRENT_LIMIT, DRIVETRAIN_PEAK_CURRENT_LIMIT_DURATION)
            } else if (this is CANSparkMax) {
                restoreFactoryDefaults()
                setSmartCurrentLimit(DRIVETRAIN_CONTINUOUS_CURRENT_LIMIT)
                idleMode = CANSparkMax.IdleMode.kBrake  // TODO: figure out if this is right
            }
        }
    }

    fun follow(path: Trajectory) {
        trajectory = path
        startTime = Timer.getFPGATimestamp().seconds
        mode = Mode.CLOSED_LOOP
    }

    /** Outputs [left] to the left motor, and [right] to the right motor. */
    fun rawDrive(left: Double, right: Double) {
        differentialDrive.tankDrive(left, right)
    }

    /** Same as [rawDrive], but using the wrapper class. */
    fun rawDrive(voltages: Ramsete.WheelVoltages) {
        rawDrive(voltages.left.value, voltages.right.value)
    }

    /**
     * Computes driver turn and throttle inputs and sets the motors.
     * @param turn The amount to turn, between -1 and 1.
     * @param throttle The amount to move, between -1 and 1.
     * @param quickTurn If true, the drivetrain will turn on a dime instead of also driving forwards.
     */
    fun cheesyDrive(turn: Double, throttle: Double, quickTurn: Boolean) {
        differentialDrive.curvatureDrive(throttle, turn, quickTurn)
    }

    /** Runs the provided [block] of code on each motor. */
    private fun applyToMotors(block: SpeedController.() -> Unit) {
        for (motor in listOf(leftMain, leftSecondary, rightMain, rightSecondary)) {
            motor.apply(block)
        }
    }

    override fun periodic() {
        when (mode) {
            Mode.DISABLED -> differentialDrive.tankDrive(0.0, 0.0)
            Mode.OPEN_LOOP -> {}  // Nothing to do in the loop because it's handled by [Robot]
            Mode.CLOSED_LOOP -> {
                rawDrive(
                    ramsete.voltages(
                    trajectory ?: run { mode = Mode.DISABLED; /* TODO: is this the right thing to do? */ return },
                    Timer.getFPGATimestamp().seconds - startTime,
                    Odometry.vels
                ))
            }
        }
    }
}
