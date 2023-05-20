package com.team2898.robot.subsystems

import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless
import com.team2898.robot.RobotMap.INTAKE_BACKWARD
import com.team2898.robot.RobotMap.INTAKE_FORWARD
import com.team2898.robot.RobotMap.INTAKE_IN
import com.team2898.robot.RobotMap.INTAKE_MAIN
import com.team2898.robot.RobotMap.INTAKE_OUT
import com.team2898.robot.RobotMap.INTAKE_SECONDARY
import com.team2898.robot.RobotMap.PNEUMATICS_MODULE_TYPE
import com.team2898.robot.RobotMap.PNUEMATICS_MODULE
import edu.wpi.first.util.sendable.SendableBuilder
import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.DoubleSolenoid.Value.kForward
import edu.wpi.first.wpilibj.DoubleSolenoid.Value.kReverse
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Intake: SubsystemBase() {

//    private val intakePneumatic = DoubleSolenoid(PNUEMATICS_MODULE, PNEUMATICS_MODULE_TYPE, INTAKE_FORWARD, INTAKE_BACKWARD)
    private val hinge = DoubleSolenoid(PNUEMATICS_MODULE, PNEUMATICS_MODULE_TYPE, INTAKE_IN, INTAKE_OUT)
    private val intakeMotor = CANSparkMax(INTAKE_MAIN, kBrushless)
    private val secondary = CANSparkMax(INTAKE_SECONDARY, kBrushless)
    private var intakeState = true

    init {
        intakeMotor.restoreFactoryDefaults()
        intakeMotor.setSmartCurrentLimit(15)
        intakeMotor.inverted = false
        intakeMotor.idleMode = CANSparkMax.IdleMode.kBrake

        secondary.restoreFactoryDefaults()
        secondary.setSmartCurrentLimit(15)
        secondary.inverted = true
        secondary.idleMode = CANSparkMax.IdleMode.kBrake
//        secondary.follow(intakeMotor)
    }

    fun intakeOpen() {
//        intakePneumatic.set(kReverse)
        intakeState = true
    }
    fun intakeClose() {
//        intakePneumatic.set(kForward)
        intakeState = false
    }

    fun runIntake(speed: Double) {
        intakeMotor.set(speed)
        secondary.set(speed)
    }
    fun runOuttake(speed: Double) {
        intakeMotor.set(-speed)
        secondary.set(-speed)
    }

    fun stopIntake() {
        intakeMotor.set(0.0)
        secondary.set(0.0)
    }

    fun intakeIsOpen(): Boolean {
        return intakeState
    }

    override fun periodic() {
        if (Arm.pos() < 0.47 || Arm.forceWrist || Arm.setpoint < 0.4) {
            hinge.set(kReverse)
        } else {
            hinge.set(kForward)
        }
//        intakeMotor.set(0.0)
//        println("${intakeMotor.appliedOutput} ${intakeMotor.outputCurrent}")
        // TODO: move to sendable
    }

    override fun initSendable(builder: SendableBuilder) {
        builder.addDoubleProperty("1 duty cycle", { intakeMotor.appliedOutput }, {})
        builder.addDoubleProperty("1 current", { intakeMotor.outputCurrent }, {})
        builder.addDoubleProperty("1 temperature", { intakeMotor.motorTemperature }, {})

        builder.addDoubleProperty("2 duty cycle", { secondary.appliedOutput }, {})
        builder.addDoubleProperty("2 current", { secondary.outputCurrent }, {})
        builder.addDoubleProperty("2 temperature", { secondary.motorTemperature }, {})
    }
}