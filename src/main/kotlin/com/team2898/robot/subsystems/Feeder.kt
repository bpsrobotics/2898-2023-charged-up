package com.team2898.robot.subsystems

import com.ctre.phoenix.motorcontrol.TalonSRXControlMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.team2898.robot.RobotMap.LEFT_BEAM_BREAK
import com.team2898.robot.RobotMap.RIGHT_BEAM_BREAK
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.DoubleSolenoid.Value
import edu.wpi.first.wpilibj.DoubleSolenoid.Value.*
import edu.wpi.first.wpilibj.PneumaticsModuleType
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Feeder : SubsystemBase() {
    private val feederMotor = TalonSRX(5)
    private val gateSolenoid = DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 1)
    private val leftInput = DigitalInput(LEFT_BEAM_BREAK)
    private val rightInput = DigitalInput(RIGHT_BEAM_BREAK)

    private var state = FeederState.STOPPED
    private var countState = CounterState.NOACTIVE
    var tubeCount = 0

    enum class CounterState(val expectedLeft: Boolean, val expectedRight: Boolean) {
        NOACTIVE(false, false),
        LEFTACTIVE(true, false),
        BOTHACTIVE(true, true),
        RIGHTACTIVE(false, true),
        COMPLETE(false, false),
    }

    enum class FeederState(val solenoid: Value, val motor: Double) {
        STOPPED(kForward, 0.0),
        INTAKING(kForward, 1.0),
        OUTTAKING(kReverse, 1.0)
    }

    var lastSwitchTime = 0.0

    override fun periodic() {
        feederMotor.set(TalonSRXControlMode.PercentOutput, state.motor)
        gateSolenoid.set(state.solenoid)

        when (state) {
            FeederState.STOPPED -> {}
            FeederState.INTAKING -> {
                if (Timer.getFPGATimestamp() - lastSwitchTime > 1.0) {
                    state = FeederState.STOPPED
                }
            }

            FeederState.OUTTAKING -> {
                if (Timer.getFPGATimestamp() - lastSwitchTime > 1.0) {
                    state = FeederState.STOPPED
                }
            }
        }
        SmartDashboard.putNumber("tubeCount", tubeCount.toDouble())
        updateBeamBreaks(leftInput.get(), rightInput.get())
    }

    fun startIntaking(manual: Boolean) {
        if (state == FeederState.OUTTAKING) {
            return
        }
        state = FeederState.INTAKING
        lastSwitchTime = Timer.getFPGATimestamp()
        if (manual) {lastSwitchTime -= 0.9}
    }

    fun startOuttaking(manual: Boolean) {
        state = FeederState.OUTTAKING
        lastSwitchTime = Timer.getFPGATimestamp()
        tubeCount = 0
        if (manual) {lastSwitchTime -= 0.9}
    }

    private fun updateBeamBreaks(left: Boolean, right: Boolean) {
        if (left == countState.expectedLeft && right == countState.expectedRight) {
            return
        }
        val num = countState.ordinal + 1
        val nextState = CounterState.values()[num]
        countState = if (left == nextState.expectedLeft && right == nextState.expectedRight) {
            nextState
        } else {
            CounterState.NOACTIVE
        }
        if (countState == CounterState.COMPLETE) {
            countState = CounterState.NOACTIVE
            tubeCount += 1
        }
    }
}
