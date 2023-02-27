package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.MovingAverage
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
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.util.sendable.SendableBuilder
import edu.wpi.first.wpilibj.AnalogEncoder
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.SubsystemBase
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.sin

object Arm : SubsystemBase() {

    private val armMotor = CANSparkMax(ARM_MAIN, kBrushless)
    private val breakSolenoid = DoubleSolenoid(PNUEMATICS_MODULE, PNEUMATICS_MODULE_TYPE, DISK_BRAKE_FORWARD, DISK_BRAKE_BACKWARD)
    private val encoder = AnalogEncoder(ARM_ENCODER_PORT)
    private val limitSwitch = DigitalInput(ARM_LIMIT_SWITCH)
    private var lastTick = false

    var setpoint = pos()

    private val UPPER_SOFT_STOP = 120.561807948.degreesToRadians()
    private val LOWER_SOFT_STOP = 9.429947216.degreesToRadians()
    private var stopped = false
    val ksin = 0.00816702
    val ks   = 0.00907915
    val kv   = 1.87442

    fun pos(): Double {
        val p = encoder.absolutePosition
        return (-(if (p < 0.5) {
            p + 1.0
        } else {
            p
        }) * 165.688247975 + 215.199342194).degreesToRadians()
    }
    val movingAverage = MovingAverage(15)
    val movingAverage2 = MovingAverage(25)

    val profileTimer = Timer()

    val constraints = TrapezoidProfile.Constraints(
        0.5,
        0.75
    )
    val pid = PIDController(0.0, 0.0, 0.0)
    var profile: TrapezoidProfile? = null

    init {
        armMotor.restoreFactoryDefaults()
        armMotor.setSmartCurrentLimit(20)
        armMotor.idleMode = CANSparkMax.IdleMode.kBrake
        armMotor.inverted = true

        encoder.distancePerRotation = PI * 2.0

//        armMotor.restoreFactoryDefaults()
//        armMotor.setSmartCurrentLimit(20)
//        armMotor.idleMode = CANSparkMax.IdleMode.kCoast

//        encoder.distancePerRotation = PI * 2.0
    }

    var last = pos()
    val timer = Timer()

    override fun periodic() {
        if (!releaseTimer.hasElapsed(0.1)) {
            breakSolenoid.set(DoubleSolenoid.Value.kReverse)
            armMotor.set(0.0)
            armMotor.idleMode = CANSparkMax.IdleMode.kCoast
            return
        } else {
            armMotor.idleMode = CANSparkMax.IdleMode.kBrake
        }

//        val elapsedTime = timer.get()
        val currentTick = limitSwitch.get()
//        val currentTick = limitSwitch.get()
//        timer.reset()
//        timer.start()

//        val currentEncoderReading = (-164.077 * encoder.absolutePosition + 34.073).degreesToRadians() - (0.185149 - 0.16458363)

//        encoderRate = (currentEncoderReading - encoderPos) / elapsedTime
//
//        encoderPos = currentEncoderReading

//        val profile = currentGoal
//        if (profile == null) {
//            // Engage brake, stop motors
////            armMotor.stopMotor()
//            breakSolenoid.set(DoubleSolenoid.Value.kReverse)
////            var output = feedforward.calculate(encoder.distance, encoderRate)
////            if (encoderPos > UPPER_SOFT_STOP) {
////                output = output.coerceAtMost(0.0)
////            } else if (encoderPos < LOWER_SOFT_STOP || currentTick) {
////                output = output.coerceAtLeast(0.0)
////            }
////
////            armMotor.set(output)
//        } else {
//            // Controller moves the arm
////            val pidOut = controller.calculate(encoder.distance, profile)
////            val feedForwardOut = feedforward.calculate(encoder.distance, encoderRate)
//            var output = 0.0//pidOut + feedForwardOut
//
//            if (encoderPos > UPPER_SOFT_STOP) {
//                output = output.coerceAtMost(0.0)
//            } else if (encoderPos < LOWER_SOFT_STOP || currentTick) {
//                output = output.coerceAtLeast(0.0)
//            }
//
////            armMotor.set(output)
//            breakSolenoid.set(DoubleSolenoid.Value.kForward)
//        }

//        if (profile != null && (armMotor.encoder.velocity.absoluteValue < 0.05) && ((encoder.distance - profile).absoluteValue < 0.5)) {
//            currentGoal = null
//        }

        val p = pos()
        val dp = p - last
        last = p
        val dt = timer.get()
        timer.start()
        timer.reset()
        movingAverage.add(dp / dt)
        movingAverage2.add(dp / dt)
        val rate = movingAverage.average
        val averagedRate = movingAverage2.average

        SmartDashboard.putNumber("arm pos", p)
        SmartDashboard.putNumber("arm rate", rate)
        SmartDashboard.putNumber("averaged arm rate", averagedRate)

        if (stopped) {
            println("STOPPED")
            armMotor.set(0.0)
            return
        }

        pid.p = SmartDashboard.getNumber("arm kp", 0.0)
        pid.d = SmartDashboard.getNumber("arm kd", 0.0)
//        val setpoint = SmartDashboard.getNumber("arm target pos", p)
//        if (setpoint != prevGoal) {
//        }
        if (setpoint == 0.0 || setpoint !in LOWER_SOFT_STOP..UPPER_SOFT_STOP || ((p - setpoint).absoluteValue < 0.05 && rate.absoluteValue < 0.1) || profileTimer.get() > (profile?.totalTime() ?: 0.0)) {
            profile = null
        }

        if (profile == null) {
            breakSolenoid.set(DoubleSolenoid.Value.kForward)
            armMotor.set(0.0)
            return
        } else {
            breakSolenoid.set(DoubleSolenoid.Value.kReverse)
        }
//        println("pos: $p goal: $setpoint")

//        armMotor.set(0.0)

//        var output = SmartDashboard.getNumber("arm output", 0.0)
        val targetSpeed = profile?.calculate(profileTimer.get())?.velocity ?: 0.0
        SmartDashboard.putNumber("arm target speed", targetSpeed)

        var output = pid.calculate(rate, targetSpeed)
        output += kv * targetSpeed
        output += ks + sin(p) * ksin
//        if (output.absoluteValue > 0.3) {
//            output = 0.0
//            println("capping output")
//        }
//        if (p !in LOWER_SOFT_STOP..UPPER_SOFT_STOP || rate.absoluteValue in 0.8..20.0) {
////            println("STOPPING p = $p rate = $rate")
//            stopped = true
//        }

        if (p > UPPER_SOFT_STOP) {
            output = output.coerceAtMost(0.0)
            println("UPPER SOFT STOP")
        } else if (p < LOWER_SOFT_STOP/* || currentTick*/) {
            output = output.coerceAtLeast(0.0)
            println("LOWER SOFT STOP")
        }
        armMotor.set(output)


        if (!lastTick && currentTick) {
//            encoder.reset()
//            currentGoal = null
        }

        lastTick = limitSwitch.get()
    }

    fun setGoal(newPos: Double) {
        if (newPos !in LOWER_SOFT_STOP..UPPER_SOFT_STOP) return
//        currentGoal = newPos
        setpoint = newPos
        profile = TrapezoidProfile(constraints,
            TrapezoidProfile.State(newPos, 0.0),
            TrapezoidProfile.State(pos(), movingAverage.average)
        )
        profileTimer.reset()
        profileTimer.start()

    }

    fun stop() {
//        currentGoal = null
        profile = null
    }

    fun isMoving(): Boolean {
        return profile != null
    }

    override fun initSendable(builder: SendableBuilder) {
        builder.addDoubleProperty("position", { pos() }) {}
        builder.addDoubleProperty("rate", { movingAverage.average }) {}
        builder.addBooleanProperty("limit switch", { lastTick }) {}
        builder.addDoubleProperty("motor output", { armMotor.appliedOutput }) {}
    }

    private val releaseTimer = Timer()

    init {
        releaseTimer.start()
    }

    fun brakeRelease() {
        stop()
        releaseTimer.reset()
        releaseTimer.start()
    }
}