package com.team2898.robot.commands

import com.bpsrobotics.engine.utils.MovingAverage
import com.bpsrobotics.engine.utils.Sugar.degreesToRadians
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import com.team2898.robot.RobotMap
import com.team2898.robot.RobotMap.ARM_LIMIT_SWITCH
import com.team2898.robot.subsystems.Drivetrain
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.math.trajectory.TrapezoidProfile.State
import edu.wpi.first.wpilibj.AnalogEncoder
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.sin

class ArmTest : CommandBase() {
    companion object {
        val armMotor = CANSparkMax(RobotMap.ARM_MAIN, CANSparkMaxLowLevel.MotorType.kBrushless)
        private val encoder = AnalogEncoder(RobotMap.ARM_ENCODER_PORT)
        private val brakeSolenoid = DoubleSolenoid(RobotMap.PNUEMATICS_MODULE, RobotMap.PNEUMATICS_MODULE_TYPE, RobotMap.DISK_BRAKE_FORWARD, RobotMap.DISK_BRAKE_BACKWARD)
        private val wristSolenoid = DoubleSolenoid(RobotMap.PNUEMATICS_MODULE, RobotMap.PNEUMATICS_MODULE_TYPE, RobotMap.INTAKE_OUT, RobotMap.INTAKE_IN)
        private val limitSwitch = DigitalInput(ARM_LIMIT_SWITCH)
    }
    var last = pos()
    val timer = Timer()
    private val upper = 1.8
    private val lower = 12.0.degreesToRadians()
    private var stopped = false
    val ksin = 0.0192407
    val ks   = 0.00729233
    val kv   = 0.971118
//    val pid = PIDController(0.0, 0.0, 0.0)
    val constraints = TrapezoidProfile.Constraints(
        1.0,
        1.25
    )
    val pid = PIDController(0.0, 0.0, 0.0)
    var profile: TrapezoidProfile? = null
    var prevGoal = 0.0

    init {
        armMotor.restoreFactoryDefaults()
        armMotor.setSmartCurrentLimit(20)
        armMotor.idleMode = CANSparkMax.IdleMode.kBrake
        armMotor.inverted = true

        encoder.distancePerRotation = PI * 2.0
        SmartDashboard.putNumber("arm output", 0.0)
        SmartDashboard.putNumber("arm kp", 0.0)
        SmartDashboard.putNumber("arm kd", 0.0)
        SmartDashboard.putNumber("arm target pos", 0.0)
    }

    fun pos(): Double {
        val p = encoder.absolutePosition
        return (-(if (p < 0.5) {
            p + 1.0
        } else {
            p
        }) * 165.688247975 + 215.199342194).degreesToRadians() - (-0.568584 - 0.16458363)
    }

    override fun initialize() {
        stopped = false
    }

    /*
    * 9.429947216d = -1.241907
    * 90d = -0.755632
    * max =
    * */
    val movingAverage = MovingAverage(15)
    val movingAverage2 = MovingAverage(25)

    val profileTimer = Timer()

    override fun execute() {
        Drivetrain.rawDrive(0.0, 0.0)
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
        SmartDashboard.putBoolean("arm limit switch", limitSwitch.get())

//        armMotor.idleMode = CANSparkMax.IdleMode.kCoast
//        brakeSolenoid.set(DoubleSolenoid.Value.kReverse)

        if (p !in lower..upper || rate.absoluteValue in 1.2..20.0) {
            println("STOPPING p = $p rate = $rate")
            stopped = true
        }

        if (stopped) {
            println("STOPPED")
            armMotor.set(0.0)
            return
        } else {
//            armMotor.set(SmartDashboard.getNumber("arm output", 0.0) + sin(p) * ksin)
        }

//        return

        pid.p = SmartDashboard.getNumber("arm kp", 0.0)
        pid.d = SmartDashboard.getNumber("arm kd", 0.0)
        val setpoint = SmartDashboard.getNumber("arm target pos", p)
        if (setpoint != prevGoal) {
            profile = TrapezoidProfile(constraints,
                State(setpoint, 0.0),
                State(p, rate))
            profileTimer.reset()
            profileTimer.start()
            prevGoal = setpoint
        }
        if (setpoint == 0.0 || setpoint !in lower..upper || ((p - setpoint).absoluteValue < 0.05 && rate.absoluteValue < 0.1) || profile?.isFinished(profileTimer.get()) ?: false) {
            profile = null
        }

        if (profile == null) {
            brakeSolenoid.set(DoubleSolenoid.Value.kForward)
            armMotor.set(0.0)
            return
        } else {
            brakeSolenoid.set(DoubleSolenoid.Value.kReverse)
        }
//        armMotor.set(0.0)

//        var output = SmartDashboard.getNumber("arm output", 0.0)
        var targetSpeed = profile?.calculate(profileTimer.get())?.velocity ?: 0.0
        SmartDashboard.putNumber("target speed", targetSpeed)
        if (setpoint == 0.0) {
            targetSpeed = 0.0
        }
        var output = pid.calculate(rate, targetSpeed)
        output += kv * targetSpeed
        output += ks + sin(p) * ksin
        SmartDashboard.putNumber("arm output", output)
//        if (output.absoluteValue > 0.3) {
//            output = 0.0
//            println("capping output")
//        }
        armMotor.set(output)
    }
}
