package com.team2898.robot.subsystems

import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import com.team2898.robot.RobotMap.INTAKE_MOTOR
import com.team2898.robot.DriverDashboard
import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.PneumaticsModuleType
import edu.wpi.first.wpilibj2.command.SubsystemBase
import java.lang.Double.max
import kotlin.math.abs

object Intake : SubsystemBase() {
    private val piston1 = DoubleSolenoid(PneumaticsModuleType.REVPH, 10, 11) // TODO Piston Constants
    private val piston2 = DoubleSolenoid(PneumaticsModuleType.REVPH, 12, 13) // TODO Piston Constants
    private val controller = CANSparkMax(INTAKE_MOTOR, CANSparkMaxLowLevel.MotorType.kBrushless)

//    enum class IntakeStates {
//        IDLE,
//        RUNNING
//    }
//
//    var state = IntakeStates.IDLE
    fun setOpenState(state: Boolean) {
        piston1.set(if (state) DoubleSolenoid.Value.kForward else DoubleSolenoid.Value.kReverse)
        piston2.set(if (state) DoubleSolenoid.Value.kForward else DoubleSolenoid.Value.kReverse)
    }

    fun setIntake(state: Boolean) {
        if (state) {
            Feed.intake()
            val drivetrainSpeed = max(Odometry.vels.leftMetersPerSecond, Odometry.vels.rightMetersPerSecond)
            controller.set(abs(drivetrainSpeed / 10).coerceIn(0.0, 0.5))
        } else {
            Feed.stopIntaking()
            controller.set(0.0)
        }
    }

    fun startIntake() {
        setIntake(true)
    }

    fun stopIntake() {
        setIntake(false)
    }

    fun openIntake() {
        setOpenState(true)
    }

    fun closeIntake() {
        setOpenState(false)
    }

    override fun periodic() {
//        when (state) {
//            IntakeStates.IDLE -> {
//                setIntake(false)
//                setOpenState(false)
//            }
//            IntakeStates.RUNNING -> {
//                setIntake(true)
//                setOpenState(true)
//            }
//        }
        DriverDashboard.boolean("Intake Open", piston1.get() == DoubleSolenoid.Value.kForward)
        DriverDashboard.boolean("Intake Running", abs(controller.get() - 0.0) <= 0.2)
    }
}
