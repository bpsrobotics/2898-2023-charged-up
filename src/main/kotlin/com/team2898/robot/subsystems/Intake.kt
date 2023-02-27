package com.team2898.robot.subsystems

import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushed
import com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless
import com.team2898.robot.RobotMap.INTAKE_BACKWARD
import com.team2898.robot.RobotMap.INTAKE_FORWARD
import com.team2898.robot.RobotMap.INTAKE_MOTOR
import com.team2898.robot.RobotMap.PNEUMATICS_MODULE_TYPE
import com.team2898.robot.RobotMap.PNUEMATICS_MODULE
import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.DoubleSolenoid.Value.kForward
import edu.wpi.first.wpilibj.DoubleSolenoid.Value.kReverse
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Intake: SubsystemBase() {

    private val intakePneumatic = DoubleSolenoid(PNUEMATICS_MODULE, PNEUMATICS_MODULE_TYPE, INTAKE_FORWARD, INTAKE_BACKWARD)
    private val intakeMotor = CANSparkMax(INTAKE_MOTOR, kBrushless)
    private var intakeState = true

    init {
        intakeMotor.restoreFactoryDefaults()
        intakeMotor.setSmartCurrentLimit(30)
    }

    fun intakeOpen() {
        intakePneumatic.set(kForward)
        intakeState = true
    }
    fun intakeClose() {
        intakePneumatic.set(kReverse)
        intakeState = false
    }

    fun runIntake() {
        intakeMotor.set(0.75)
    }
    fun runOuttake() {
        intakeMotor.set(-1.0)
    }

    fun stopIntake() {
        intakeMotor.set(0.0)
    }

    fun intakeIsOpen(): Boolean {
        return intakeState
    }

    override fun periodic() {
//        intakeMotor.set(0.0)
//        println("${intakeMotor.appliedOutput} ${intakeMotor.outputCurrent}")
        SmartDashboard.putNumber("intake duty cycle", intakeMotor.appliedOutput)
        SmartDashboard.putNumber("intake current", intakeMotor.outputCurrent)
    }
}