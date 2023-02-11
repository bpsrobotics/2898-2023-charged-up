package com.team2898.robot.subsystems

import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless
import com.team2898.robot.Constants.ARM_MAXACCEL
import com.team2898.robot.Constants.ARM_MAXSPEED
import com.team2898.robot.Constants.ARM_RAISED_KD
import com.team2898.robot.Constants.ARM_RAISED_KI
import com.team2898.robot.Constants.ARM_RAISED_KP
import com.team2898.robot.RobotMap.ARM_ENCODER_A
import com.team2898.robot.RobotMap.ARM_ENCODER_B
import com.team2898.robot.RobotMap.ARM_MAIN
import com.team2898.robot.RobotMap.ARM_SECONDARY
import com.team2898.robot.RobotMap.DISK_BREAK_BACKWARD
import com.team2898.robot.RobotMap.DISK_BREAK_FORWARD
import com.team2898.robot.RobotMap.PNEUMATICS_MODULE_TYPE
import com.team2898.robot.RobotMap.PNUEMATICS_MODULE
import edu.wpi.first.math.controller.ArmFeedforward
import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.wpilibj.AnalogEncoder
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.DoubleSolenoid.Value.kForward
import edu.wpi.first.wpilibj.DoubleSolenoid.Value.kReverse
import edu.wpi.first.wpilibj.Encoder
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.SubsystemBase
import kotlin.math.absoluteValue

object Arm : SubsystemBase() {

    private var constraints = TrapezoidProfile.Constraints(ARM_MAXSPEED, ARM_MAXACCEL)
    private val controller = ProfiledPIDController(ARM_RAISED_KP, ARM_RAISED_KI, ARM_RAISED_KD,constraints)
    private var currentGoal: Double? = null
    private val armMotor1 = CANSparkMax(ARM_MAIN, kBrushless)
    private val armMotor2 = CANSparkMax(ARM_SECONDARY, kBrushless)
    private val breakSolenoid = DoubleSolenoid(PNUEMATICS_MODULE, PNEUMATICS_MODULE_TYPE, DISK_BREAK_FORWARD, DISK_BREAK_BACKWARD)
    //TODO: Reminder to fix the arm encoder channel (port)
    private val encoder = AnalogEncoder(0)
    //TODO: Tune the armFeedforward numbers
    private val feedforward = ArmFeedforward(0.0,0.0,0.0)
    //TODO: Add proper limit switch port
    private val limitSwitch = DigitalInput(0)
    private var lastTick = false

    private val timer = Timer()
    private var encoderPos = 0.0
    private var encoderRate = 0.0


    init {
        listOf(armMotor1, armMotor2).forEach {
            it.restoreFactoryDefaults()
            it.setSmartCurrentLimit(20)
        }
        armMotor2.follow(armMotor1)
    }
    override fun periodic() {
        val elapsedTime = timer.get()
        val currentTick = limitSwitch.get()
        timer.reset()
        timer.start()


        encoderRate = (encoder.get() - encoderPos) / elapsedTime

        encoderPos = encoder.get()

        val profile = currentGoal
        if (profile == null) {

            //Engage breaks, stop motors
            armMotor1.stopMotor()
            armMotor2.stopMotor()
            breakSolenoid.set(kReverse)
        } else {

            //Controller moves the arm
            val pidOut = controller.calculate(encoder.distance, profile)
            val feedForwardOut = feedforward.calculate(encoder.distance, encoderRate)
            var output = pidOut + feedForwardOut
            if (currentTick) {
                output = output.coerceAtLeast(0.0)
            }
            //TODO: Adjust soft stop for the arm
            if (encoderPos > 1.0) {
              output.coerceAtMost(0.0)
            }
            armMotor1.set(output)
            breakSolenoid.set(kForward)

        }
        if (profile != null && (armMotor1.encoder.velocity.absoluteValue < 0.05) && ((encoder.distance - profile).absoluteValue < 0.5)) {
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

    fun isMoving() : Boolean {
        return (currentGoal == null)
    }

}