package com.team2898.robot.subsystems

import com.bpsrobotics.engine.controls.StallDetection
import com.bpsrobotics.engine.utils.Millis
import com.bpsrobotics.engine.utils.Volts
import com.bpsrobotics.engine.utils.plus
import com.bpsrobotics.engine.utils.seconds
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import com.team2898.robot.Constants.CLIMBER_ENDSTOP
import com.team2898.robot.RobotMap.CLIMBER_LEFT_ENCODER_A
import com.team2898.robot.RobotMap.CLIMBER_LEFT_ENCODER_B
import com.team2898.robot.RobotMap.CLIMBER_LEFT_LIMIT_SWITCH
import com.team2898.robot.RobotMap.CLIMBER_LEFT_MAIN
import com.team2898.robot.RobotMap.CLIMBER_LEFT_SECONDARY
import com.team2898.robot.RobotMap.CLIMBER_RIGHT_ENCODER_A
import com.team2898.robot.RobotMap.CLIMBER_RIGHT_ENCODER_B
import com.team2898.robot.RobotMap.CLIMBER_RIGHT_LIMIT_SWITCH
import com.team2898.robot.RobotMap.CLIMBER_RIGHT_MAIN
import com.team2898.robot.RobotMap.CLIMBER_RIGHT_SECONDARY
import com.team2898.robot.RobotMap.CLIMB_L_FORWARD
import com.team2898.robot.RobotMap.CLIMB_L_REVERSE
import com.team2898.robot.RobotMap.CLIMB_R_FORWARD
import com.team2898.robot.RobotMap.CLIMB_R_REVERSE
import edu.wpi.first.util.sendable.SendableBuilder
import edu.wpi.first.wpilibj.*
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Climb : SubsystemBase() {

    private val leftArmMain = WPI_TalonSRX(CLIMBER_LEFT_MAIN)
    private val leftArmSecondary = WPI_TalonSRX(CLIMBER_LEFT_SECONDARY)
    private val rightArmMain = WPI_TalonSRX(CLIMBER_RIGHT_MAIN)
    private val rightArmSecondary = WPI_TalonSRX(CLIMBER_RIGHT_SECONDARY)

    init {
        listOf(leftArmMain, leftArmSecondary, rightArmMain, rightArmSecondary).forEach {
            it.configFactoryDefault()
            it.configContinuousCurrentLimit(10)
            it.configPeakCurrentLimit(30, 50)
//            it.enableVoltageCompensation(true)
        }
        rightArmMain.inverted = true
        rightArmSecondary.inverted = true
    }

    fun openLoop(value: Volts) {
        leftArm.openLoop(value)
        rightArm.openLoop(value)
    }

    private val leftArm = Arm(
        listOf(leftArmMain, leftArmSecondary),
        Encoder(CLIMBER_LEFT_ENCODER_A, CLIMBER_LEFT_ENCODER_B).apply { this.setReverseDirection(true) },
        DigitalInput(CLIMBER_LEFT_LIMIT_SWITCH),
        CLIMBER_ENDSTOP,
        true
    )

    private val rightArm = Arm(
        listOf(rightArmMain, rightArmSecondary),
        Encoder(CLIMBER_RIGHT_ENCODER_A, CLIMBER_RIGHT_ENCODER_B).apply { this.setReverseDirection(true) },
        DigitalInput(CLIMBER_RIGHT_LIMIT_SWITCH),
        CLIMBER_ENDSTOP,
        true
    )

    private val piston1 = DoubleSolenoid(PneumaticsModuleType.REVPH, CLIMB_L_FORWARD, CLIMB_L_REVERSE)
    private val piston2 = DoubleSolenoid(PneumaticsModuleType.REVPH, CLIMB_R_FORWARD, CLIMB_R_REVERSE)

    fun pistons(value: DoubleSolenoid.Value) {
        piston1.set(value)
        piston2.set(value)
        Intake.openIntake()
    }

    private class Arm(
        private val motors: List<WPI_TalonSRX>,
        internal val encoder: Encoder,
        private val limitSwitch: DigitalInput,
        private val endStop: Int,
        private val invertedLimitSwitch: Boolean = false
    ) {
        private var lastLimitSwitchValue = false
        private val stallDetector = StallDetection(Millis(1000))
        private var stallTimeout = 0.seconds
        internal val limitSwitchv get() = if (invertedLimitSwitch) !limitSwitch.get() else limitSwitch.get()
        private var isZeroed = false

        fun openLoop(value: Volts) {
            motors.forEach { it.setVoltage(value.value.run {
                when {
                    limitSwitchv || (encoder.get() <= 0.0 && isZeroed) -> coerceAtLeast(0.0)
                    encoder.get() >= endStop -> coerceAtMost(0.0)
                    else -> this
                }
            }) }
        }

        fun update() {
            if (stallDetector.isStalled(motors.first().motorOutputPercent, encoder.distance)) {
                stallTimeout = Timer.getFPGATimestamp().seconds + 5.seconds
            }

            if (Timer.getFPGATimestamp() < stallTimeout.value) {
                motors.forEach { it.set(0.0) }
                return
            }

            val limitSwitchValue = limitSwitchv
            val leadingEdge = limitSwitchValue && !lastLimitSwitchValue
            lastLimitSwitchValue = limitSwitchValue

            if (leadingEdge) {
                isZeroed = true
                encoder.reset()
            }

            if ((limitSwitchValue || (encoder.get() <= 0.0 && isZeroed)) && motors.first().motorOutputPercent < 0) {
                motors.forEach { it.set(0.0) }
            }
            if (encoder.get() >= endStop && motors.first().motorOutputPercent > 0) {
                motors.forEach { it.set(0.0) }
            }
        }
    }

    override fun periodic() {
        leftArm.update()
        rightArm.update()
    }

    override fun initSendable(builder: SendableBuilder) {
        builder.setSmartDashboardType("Subsystem")
        builder.addDoubleProperty("left encoder", leftArm.encoder::getDistance) {}
        builder.addDoubleProperty("right encoder", rightArm.encoder::getDistance) {}
        builder.addBooleanProperty("left limit switch", leftArm::limitSwitchv) {}
        builder.addBooleanProperty("right limit switch", rightArm::limitSwitchv) {}
    }
}
