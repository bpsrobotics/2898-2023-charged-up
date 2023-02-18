package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.Sugar.degreesToRadians
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless
import com.team2898.robot.Constants.ARM_MAXACCEL
import com.team2898.robot.Constants.ARM_MAXSPEED
import com.team2898.robot.Constants.ARM_RAISED_KD
import com.team2898.robot.Constants.ARM_RAISED_KI
import com.team2898.robot.Constants.ARM_RAISED_KP
import com.team2898.robot.RobotMap.ARM_ENCODER_PORT
import com.team2898.robot.RobotMap.ARM_LIMIT_SWITCH
import com.team2898.robot.RobotMap.ARM_MAIN
import com.team2898.robot.RobotMap.DISK_BRAKE_BACKWARD
import com.team2898.robot.RobotMap.DISK_BRAKE_FORWARD
import com.team2898.robot.RobotMap.PNEUMATICS_MODULE_TYPE
import com.team2898.robot.RobotMap.PNUEMATICS_MODULE
import edu.wpi.first.math.controller.ArmFeedforward
import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.util.sendable.SendableBuilder
import edu.wpi.first.wpilibj.AnalogEncoder
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.SubsystemBase
import kotlin.math.PI
import kotlin.math.absoluteValue

object Arm : SubsystemBase() {

    private var constraints = TrapezoidProfile.Constraints(ARM_MAXSPEED, ARM_MAXACCEL)
    private val controller = ProfiledPIDController(ARM_RAISED_KP, ARM_RAISED_KI, ARM_RAISED_KD, constraints)
    private var currentGoal: Double? = null
    private val armMotor = CANSparkMax(ARM_MAIN, kBrushless)
    private val breakSolenoid = DoubleSolenoid(PNUEMATICS_MODULE, PNEUMATICS_MODULE_TYPE, DISK_BRAKE_FORWARD, DISK_BRAKE_BACKWARD)
    private val encoder = AnalogEncoder(ARM_ENCODER_PORT)
    //TODO: Tune the armFeedforward numbers
    private val feedforward = ArmFeedforward(0.0, 0.0, 0.0)
    private val limitSwitch = DigitalInput(ARM_LIMIT_SWITCH)
    private var lastTick = false

    private val timer = Timer()
    private var encoderPos = 0.0
    private var encoderRate = 0.0

    private val UPPER_SOFT_STOP = 101.561807948.degreesToRadians()
    private val LOWER_SOFT_STOP = 9.429947216.degreesToRadians()

    init {
        armMotor.restoreFactoryDefaults()
        armMotor.setSmartCurrentLimit(20)
        armMotor.idleMode = CANSparkMax.IdleMode.kCoast

        encoder.distancePerRotation = PI * 2.0
    }
    override fun periodic() {
        val elapsedTime = timer.get()
        val currentTick = limitSwitch.get()
//        val currentTick = limitSwitch.get()
        timer.reset()
        timer.start()

        val currentEncoderReading = (-164.077 * encoder.absolutePosition + 34.073).degreesToRadians() - (0.185149 - 0.16458363)

        encoderRate = (currentEncoderReading - encoderPos) / elapsedTime

        encoderPos = currentEncoderReading

        val profile = currentGoal
        if (profile == null) {
            // Engage brake, stop motors
            armMotor.stopMotor()
            breakSolenoid.set(DoubleSolenoid.Value.kReverse)
//            var output = feedforward.calculate(encoder.distance, encoderRate)
//            if (encoderPos > UPPER_SOFT_STOP) {
//                output = output.coerceAtMost(0.0)
//            } else if (encoderPos < LOWER_SOFT_STOP || currentTick) {
//                output = output.coerceAtLeast(0.0)
//            }
//
//            armMotor.set(output)
        } else {
            // Controller moves the arm
            val pidOut = controller.calculate(encoder.distance, profile)
            val feedForwardOut = feedforward.calculate(encoder.distance, encoderRate)
            var output = pidOut + feedForwardOut

            if (encoderPos > UPPER_SOFT_STOP) {
                output = output.coerceAtMost(0.0)
            } else if (encoderPos < LOWER_SOFT_STOP || currentTick) {
                output = output.coerceAtLeast(0.0)
            }

//            armMotor.set(output)
            breakSolenoid.set(DoubleSolenoid.Value.kForward)
        }

        if (profile != null && (armMotor.encoder.velocity.absoluteValue < 0.05) && ((encoder.distance - profile).absoluteValue < 0.5)) {
            currentGoal = null
        }

        if (!lastTick && currentTick) {
            encoder.reset()
            currentGoal = null
        }

        lastTick = limitSwitch.get()
    }

    fun setGoal(newPos: Double) {
        currentGoal = newPos
    }

    fun stop() {
        currentGoal = null
    }

    fun isMoving(): Boolean {
        return currentGoal != null
    }

    /*
    * lower hardstop: 0.239077
    * looped around
    * 90d: 0.750455
    * */

    override fun initSendable(builder: SendableBuilder) {
        builder.addDoubleProperty("raw position", {
            val p = encoder.absolutePosition
            -0.00623400361108 * (if (p < 0.5) {
                p + 1.0
            } else {
                p
            }) + 1.291174325
        }) {}
        builder.addDoubleProperty("position", { encoderPos }) {}
        builder.addDoubleProperty("rate", { encoderRate }) {}
        builder.addBooleanProperty("limit switch", { lastTick }) {}
    }
}