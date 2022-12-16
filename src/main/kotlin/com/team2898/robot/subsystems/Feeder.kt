@file:Suppress("SpellCheckingInspection")  // nobody likes a prescriptivist

package com.team2898.robot.subsystems

import com.ctre.phoenix.motorcontrol.TalonSRXControlMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.team2898.robot.RobotMap.FEEDER_MOTOR
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
    private val feederMotor = TalonSRX(FEEDER_MOTOR)
    private val gateSolenoid = DoubleSolenoid(7, PneumaticsModuleType.REVPH, 0, 1)
    private val leftInput = DigitalInput(LEFT_BEAM_BREAK)
    private val rightInput = DigitalInput(RIGHT_BEAM_BREAK)

    private var state = FeederState.STOPPED
    private var countState = CounterState.NOACTIVE

    private var lastSwitchTime = 0.0
    var tubeCount = 0


    enum class CounterState(private val expectedLeft: Boolean, private val expectedRight: Boolean) {
        NOACTIVE(false, false),
        LEFTACTIVE(true, false),
        BOTHACTIVE(true, true),
        RIGHTACTIVE(false, true),
        COMPLETE(false, false);
        fun matches(left: Boolean, right: Boolean) =
                left == expectedLeft && right == expectedRight
    }

    enum class FeederState(val solenoid: Value, val motor: Double) {
        STOPPED(kReverse, 0.0),
        INTAKING(kReverse, 1.0),
        OUTTAKING(kForward, 1.0)
    }
    override fun periodic() {
        feederMotor.set(TalonSRXControlMode.PercentOutput, -state.motor)
        gateSolenoid.set(state.solenoid)

        SmartDashboard.putBoolean("Left Beam Break", leftInput.get())
        SmartDashboard.putBoolean("Right Beam Break", rightInput.get())

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
        SmartDashboard.putString("feeder state", state.name)
        SmartDashboard.putNumber("feeder last switched", lastSwitchTime)
        updateBeamBreaks(!leftInput.get(), !rightInput.get())
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
        val nextState = CounterState.values()[countState.ordinal + 1]
        countState = when {
            countState.matches(left,right) -> return
            nextState.matches(left,right) -> nextState
            else -> CounterState.NOACTIVE
        }


        if (countState == CounterState.COMPLETE) {
            countState = CounterState.NOACTIVE
            tubeCount += 1
        }
    }
}
