package com.team2898.robot.subsystems

import com.bpsrobotics.engine.utils.MovingAverage
import com.bpsrobotics.engine.utils.Sugar.degreesToRadians
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless
import com.team2898.robot.Constants.ARM_MAXACCEL
import com.team2898.robot.Constants.ARM_MAXSPEED
import com.team2898.robot.RobotMap.ARM_ENCODER_PORT
import com.team2898.robot.RobotMap.ARM_LIMIT_SWITCH
import com.team2898.robot.RobotMap.ARM_MAIN
import com.team2898.robot.RobotMap.DISK_BRAKE_BACKWARD
import com.team2898.robot.RobotMap.DISK_BRAKE_FORWARD
import com.team2898.robot.RobotMap.PNEUMATICS_MODULE_TYPE
import com.team2898.robot.RobotMap.PNUEMATICS_MODULE
import edu.wpi.first.math.controller.PIDController
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
    private val brakeSolenoid = DoubleSolenoid(PNUEMATICS_MODULE, PNEUMATICS_MODULE_TYPE, DISK_BRAKE_FORWARD, DISK_BRAKE_BACKWARD)
    private val encoder = AnalogEncoder(ARM_ENCODER_PORT)
    private val limitSwitch = DigitalInput(ARM_LIMIT_SWITCH)
    private var lastTick = false

    var setpoint = pos()

    private const val UPPER_SOFT_STOP = 1.95
    val LOWER_SOFT_STOP = 9.429947216.degreesToRadians()
    private var stopped = false
    var ksin = 0.0192407
    var ks   = 0.00729233
    var kv   = 0.5

    fun pos(): Double {
        val p = encoder.absolutePosition
        return (-(if (p < 0.505) {
            p + 1.0
        } else {
            p
        }) * 165.688247975 + 215.199342194).degreesToRadians() - (-0.568584 - 0.16458363) - (0.65 - 0.17453293) + 0.05 - 0.061268369999999 - 0.30541637 - -0.19769663 - (0.290913 - LOWER_SOFT_STOP)
    }

    val movingAverage = MovingAverage(15)
    val movingAverage2 = MovingAverage(25)

    val profileTimer = Timer()

    val constraints = TrapezoidProfile.Constraints(
        ARM_MAXSPEED,
        ARM_MAXACCEL
    )
    val pid = PIDController(0.0, 0.0, 0.0)
    var profile: TrapezoidProfile? = null
    private val integral = MovingAverage(50)

    init {
        armMotor.restoreFactoryDefaults()
        armMotor.setSmartCurrentLimit(40)
        armMotor.idleMode = CANSparkMax.IdleMode.kBrake
        armMotor.inverted = true

//        armMotor.encoder.velocityConversionFactor = PI * 2.0 / 525.0 / 42.0 / 3.33333333

        encoder.distancePerRotation = PI * 2.0

        SmartDashboard.putNumber("arm kp", 0.0)
        SmartDashboard.putNumber("arm kd", 0.0)
        SmartDashboard.putNumber("arm ks", ks)
        SmartDashboard.putNumber("arm ksin", ksin)
        SmartDashboard.putNumber("arm kv", kv)
    }

    var last = pos()
    val timer = Timer()

    override fun periodic() {
        SmartDashboard.putNumber("quotient", armMotor.encoder.velocity / movingAverage.average)
        SmartDashboard.putNumber("arm current", armMotor.outputCurrent)
        SmartDashboard.putNumber("arm duty cycle", armMotor.appliedOutput)
        ks = SmartDashboard.getNumber("arm ks", ks)
        ksin = SmartDashboard.getNumber("arm ksin", ksin)
        kv = SmartDashboard.getNumber("arm kv", kv)

//        val currentTick = limitSwitch.get()
//        lastTick = limitSwitch.get()
        val currentTick = false

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

        integral.add((rate - armMotor.encoder.velocity).absoluteValue)

//        SmartDashboard.putNumber("arm encoder difference", integral.average * integral.size)

        if (stopped) {
            println("STOPPED")
            armMotor.set(0.0)
            return
        }

        pid.p = SmartDashboard.getNumber("arm kp", 0.0)
        pid.d = SmartDashboard.getNumber("arm kd", 0.0)
        if (setpoint == 0.0 || setpoint !in LOWER_SOFT_STOP..UPPER_SOFT_STOP || ((p - setpoint).absoluteValue < 0.05 && rate.absoluteValue < 0.1) || profileTimer.get() > (profile?.totalTime() ?: 0.0)) {
            profile = null
        }

//        // TODO
//        brakeSolenoid.set(DoubleSolenoid.Value.kReverse)
//        armMotor.set(0.0)
//        armMotor.idleMode = CANSparkMax.IdleMode.kCoast
//        return

        if (!releaseTimer.hasElapsed(0.1)) {
            brakeSolenoid.set(DoubleSolenoid.Value.kReverse)
            armMotor.set(0.0)
            armMotor.idleMode = CANSparkMax.IdleMode.kCoast
            return
        } else {
            armMotor.idleMode = CANSparkMax.IdleMode.kBrake
        }

        if (profile == null) {
            brakeSolenoid.set(DoubleSolenoid.Value.kForward)
            armMotor.set(0.0)
            return
        } else {
            brakeSolenoid.set(DoubleSolenoid.Value.kReverse)
        }

        val targetSpeed = profile?.calculate(profileTimer.get())?.velocity ?: 0.0
        SmartDashboard.putNumber("arm target speed", targetSpeed)

        var output = pid.calculate(rate, targetSpeed)
        output += kv * targetSpeed
        output += ks + sin(p) * ksin

        if (p > UPPER_SOFT_STOP) {
            output = output.coerceAtMost(0.0)
            println("UPPER SOFT STOP")
        } else if (p < LOWER_SOFT_STOP || currentTick) {
            output = output.coerceAtLeast(0.0)
            println("LOWER SOFT STOP")
        }
        armMotor.set(output)


        // fixme move to top
        if (!lastTick && currentTick) {
//            encoder.reset()
//            currentGoal = null
        }
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
        profile = null
    }

    fun isMoving(): Boolean {
        return profile != null
    }

    override fun initSendable(builder: SendableBuilder) {
        builder.addDoubleProperty("position", { pos() }) {}
        builder.addDoubleProperty("raw position", { encoder.absolutePosition }) {}
        builder.addDoubleProperty("arm motor rate", { armMotor.encoder.velocity }) {}
        builder.addDoubleProperty("rate", { movingAverage.average }) {}
        builder.addDoubleProperty("rate2", { movingAverage2.average }) {}
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

    var forceWrist = false

    fun forceWristUp() {
        forceWrist = true
    }

    fun unForceWrist() {
        forceWrist = false
    }
}