package com.teamXXXX.robot.subsystems

import com.bpsrobotics.engine.controls.RamseteDrivetrain
import com.bpsrobotics.engine.controls.RamseteDrivetrain.WheelVoltages
import com.bpsrobotics.engine.utils.Meters
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import com.revrobotics.CANEncoder
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushed
import com.teamXXXX.robot.Constants.DRIVETRAIN_CHARACTERIZATION
import com.teamXXXX.robot.Constants.DRIVETRAIN_CONSTRAINTS
import com.teamXXXX.robot.Constants.DRIVETRAIN_CONTINUOUS_CURRENT_LIMIT
import com.teamXXXX.robot.Constants.DRIVETRAIN_LEFT_MAIN
import com.teamXXXX.robot.Constants.DRIVETRAIN_LEFT_SECONDARY
import com.teamXXXX.robot.Constants.DRIVETRAIN_PEAK_CURRENT_LIMIT
import com.teamXXXX.robot.Constants.DRIVETRAIN_PEAK_CURRENT_LIMIT_DURATION
import com.teamXXXX.robot.Constants.DRIVETRAIN_RAMSETE
import com.teamXXXX.robot.Constants.DRIVETRAIN_RIGHT_MAIN
import com.teamXXXX.robot.Constants.DRIVETRAIN_RIGHT_SECONDARY
import com.teamXXXX.robot.Constants.DRIVETRAIN_TRACK_WIDTH
import edu.wpi.first.wpilibj.SpeedController
import edu.wpi.first.wpilibj.SpeedControllerGroup
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.geometry.Translation2d
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Drivetrain : SubsystemBase() {

    private val leftMain: SpeedController = CANSparkMax(DRIVETRAIN_LEFT_MAIN, kBrushed)
    private val leftSecondary: SpeedController = CANSparkMax(DRIVETRAIN_LEFT_SECONDARY, kBrushed)
    private val rightMain: SpeedController = CANSparkMax(DRIVETRAIN_RIGHT_MAIN, kBrushed)
    private val rightSecondary: SpeedController = CANSparkMax(DRIVETRAIN_RIGHT_SECONDARY, kBrushed)

    private val left  = SpeedControllerGroup(leftMain,  leftSecondary)
    private val right = SpeedControllerGroup(rightMain, rightSecondary)

    val leftEncoder: CANEncoder = (leftMain as CANSparkMax).encoder
    val rightEncoder = (rightMain as CANSparkMax).encoder

    private val ramsete: RamseteDrivetrain = RamseteDrivetrain(
        Meters(DRIVETRAIN_TRACK_WIDTH.meterValue()),
        DRIVETRAIN_CHARACTERIZATION,
        DRIVETRAIN_CONSTRAINTS,
        DRIVETRAIN_RAMSETE
    )

    /** Calls createTrajectory from the [RamseteDrivetrain] */
    fun createTrajectory(startingPose: Pose2d, vararg points: Translation2d, endingPose: Pose2d) {
        ramsete.createTrajectory(startingPose, *points, endingPose = endingPose)
    }

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

    /** Outputs [left] to the left motor, and [right] to the right motor. */
    fun rawDrive(left: Double, right: Double) {
        differentialDrive.tankDrive(left, right)
    }

    /** Same as [rawDrive], but using the wrapper class. */
    fun rawDrive(voltages: WheelVoltages) {
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
                rawDrive(ramsete.update(
                        Odometry.pose(),
                        DifferentialDriveWheelSpeeds(leftEncoder.velocity, rightEncoder.velocity)
                    )
                )
            }
        }
    }
}
