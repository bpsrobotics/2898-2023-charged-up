package com.team2898.robot.subsystems

import com.bpsrobotics.engine.async.CSVLogger
import com.bpsrobotics.engine.controls.Controller
import com.bpsrobotics.engine.controls.Ramsete
import com.bpsrobotics.engine.controls.TrajectoryMaker
import com.bpsrobotics.engine.utils.*
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless
import com.team2898.robot.Constants.DRIVETRAIN_CONTINUOUS_CURRENT_LIMIT
import com.team2898.robot.Constants.DRIVETRAIN_KA
import com.team2898.robot.Constants.DRIVETRAIN_KD
import com.team2898.robot.Constants.DRIVETRAIN_KP
import com.team2898.robot.Constants.DRIVETRAIN_KS
import com.team2898.robot.Constants.DRIVETRAIN_KV
import com.team2898.robot.RobotMap.DRIVETRAIN_LEFT_ENCODER_A
import com.team2898.robot.RobotMap.DRIVETRAIN_LEFT_ENCODER_B
import com.team2898.robot.RobotMap.DRIVETRAIN_LEFT_MAIN
import com.team2898.robot.RobotMap.DRIVETRAIN_LEFT_SECONDARY
import com.team2898.robot.Constants.DRIVETRAIN_MAX_ACCELERATION
import com.team2898.robot.Constants.DRIVETRAIN_MAX_VELOCITY
import com.team2898.robot.Constants.DRIVETRAIN_PEAK_CURRENT_LIMIT
import com.team2898.robot.Constants.DRIVETRAIN_PEAK_CURRENT_LIMIT_DURATION
import com.team2898.robot.Constants.DRIVETRAIN_RAMSETE_B
import com.team2898.robot.Constants.DRIVETRAIN_RAMSETE_Z
import com.team2898.robot.RobotMap.DRIVETRAIN_RIGHT_ENCODER_A
import com.team2898.robot.RobotMap.DRIVETRAIN_RIGHT_ENCODER_B
import com.team2898.robot.RobotMap.DRIVETRAIN_RIGHT_MAIN
import com.team2898.robot.RobotMap.DRIVETRAIN_RIGHT_SECONDARY
import com.team2898.robot.Constants.DRIVETRAIN_TRACK_WIDTH
import com.team2898.robot.DriverDashboard
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.math.trajectory.Trajectory
import edu.wpi.first.wpilibj.Encoder
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import edu.wpi.first.wpilibj.motorcontrol.MotorController
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.SubsystemBase
import java.io.File
import kotlin.math.PI

object Drivetrain : SubsystemBase() {

    private val leftMain = CANSparkMax(DRIVETRAIN_LEFT_MAIN, kBrushless)
    private val leftSecondary = CANSparkMax(DRIVETRAIN_LEFT_SECONDARY, kBrushless)
    private val rightMain = CANSparkMax(DRIVETRAIN_RIGHT_MAIN, kBrushless)
    private val rightSecondary = CANSparkMax(DRIVETRAIN_RIGHT_SECONDARY, kBrushless)

    private val left  = MotorControllerGroup(leftMain,  leftSecondary)
    private val right = MotorControllerGroup(rightMain, rightSecondary)

    val leftEncoder  = Encoder(DRIVETRAIN_LEFT_ENCODER_A,  DRIVETRAIN_LEFT_ENCODER_B)
    val rightEncoder = Encoder(DRIVETRAIN_RIGHT_ENCODER_A, DRIVETRAIN_RIGHT_ENCODER_B)

    init {
        listOf(leftEncoder, rightEncoder).map {
            it.distancePerPulse = (In(6.0).meterValue() * PI) / 2048
        }
        if (RobotBase.isReal()) {
             val file = File("/home/lvuser/dt-data.csv").outputStream().bufferedWriter()
             file.write("time,leftvel,rightvel,leftgoal,rightgoal,leftpid,rightpid,leftff,rightff\n")
        }

    }

    val trajectoryMaker = TrajectoryMaker(DRIVETRAIN_MAX_VELOCITY, DRIVETRAIN_MAX_ACCELERATION)

    private val leftPid = Controller.PID(DRIVETRAIN_KP, DRIVETRAIN_KD)
    private val rightPid = Controller.PID(DRIVETRAIN_KP, DRIVETRAIN_KD)
    private val leftFF = SimpleMotorFeedforward(DRIVETRAIN_KS, DRIVETRAIN_KV, DRIVETRAIN_KA)
    private val rightFF = SimpleMotorFeedforward(DRIVETRAIN_KS, DRIVETRAIN_KV, DRIVETRAIN_KA)

    init {
        if (RobotBase.isReal()) {
            CSVLogger("drivetrain", 50.0,
                "leftVelocity" to { leftEncoder.rate },
                "rightVelocity" to { rightEncoder.rate },
                "leftSet" to { leftPid.setpoint},
                "rightSet" to { rightPid.setpoint}
                )
        }
    }

    private val ramsete: Ramsete = Ramsete(
        DRIVETRAIN_TRACK_WIDTH.toMeters(),
        Odometry,
        leftPid,
        rightPid,
        leftFF,
        rightFF,
        DRIVETRAIN_RAMSETE_B,
        DRIVETRAIN_RAMSETE_Z
    )

    private var trajectory: Trajectory? = null
    private var startTime = 0.seconds

    var mode = Mode.OPEN_LOOP

    enum class Mode {
        OPEN_LOOP, CLOSED_LOOP, STUPID
    }

    /** Computes left and right throttle from driver controller turn and throttle inputs. */
    private val differentialDrive = DifferentialDrive(left, right)

    /** Initializes motor configurations. */
    init {
        differentialDrive.setDeadband(0.0)

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
                idleMode = CANSparkMax.IdleMode.kCoast
           }
        }

        rightMain.inverted = true
        rightSecondary.inverted = true
    }

    fun follow(path: Trajectory) {
        trajectory = path
        startTime = Timer.getFPGATimestamp().seconds
        mode = Mode.CLOSED_LOOP
    }

    fun stopAuto() {
        trajectory = null
    }

    fun stupidDrive(left: `M/s`, right: `M/s`) {
        mode = Mode.STUPID
        leftPid.setpoint = left.value
        rightPid.setpoint = right.value
    }

    /** Outputs [left] to the left motor, and [right] to the right motor. */
    fun rawDrive(left: Double, right: Double) {
//        Drivetrain.mode = Mode.OPEN_LOOP
        differentialDrive.tankDrive(left, right, false)
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
    private fun applyToMotors(block: MotorController.() -> Unit) {
        for (motor in listOf(leftMain, leftSecondary, rightMain, rightSecondary)) {
            motor.apply(block)
        }
    }

    override fun periodic() {
        when (mode) {
            Mode.OPEN_LOOP -> {
                if (trajectory != null) {
                    SmartDashboard.putNumber("path heading", trajectory!!.initialPose.rotation.degrees)
                }
            }  // Nothing to do in the loop because it's handled by [Robot]
            Mode.CLOSED_LOOP -> {
                rawDrive(
                    ramsete.voltages(
                    trajectory ?: run { mode = Mode.OPEN_LOOP; return },
                    Timer.getFPGATimestamp().seconds - startTime,
                    Odometry.vels
                ))
            }
            Mode.STUPID -> {
                val l = leftPid.calculate(leftEncoder.rate)
                val r = rightPid.calculate(rightEncoder.rate)

                val lf = leftFF.calculate(leftPid.setpoint)
                val rf = rightFF.calculate(rightPid.setpoint)

                rawDrive(/*l + */lf, /*r + */rf)
            }
        }
        DriverDashboard.number("left encoder", Odometry.leftVel.value)
        DriverDashboard.number("right encoder", Odometry.rightVel.value)
    }
}
