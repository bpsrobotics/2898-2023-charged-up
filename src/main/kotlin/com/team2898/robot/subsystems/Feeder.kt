package com.team2898.robot.subsystems

import com.ctre.phoenix.motorcontrol.TalonSRXControlMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.PneumaticsModuleType
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Feeder : SubsystemBase() {
    private val feederMotor = TalonSRX(5)
    private val gateSolenoid = DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 1 )

    private var state = FeederState.STOPPED
    enum class FeederState {
        STOPPED, INTAKING, OUTTAKING
    }

    var lastSwitchTime = 0.0

    override fun periodic() {
        when (state) {
            FeederState.STOPPED -> {
                feederMotor.set(TalonSRXControlMode.PercentOutput, 0.0)
                gateSolenoid.set(DoubleSolenoid.Value.kForward)
            }
            FeederState.INTAKING -> {
                feederMotor.set(TalonSRXControlMode.PercentOutput, 1.0)
                gateSolenoid.set(DoubleSolenoid.Value.kForward)

                if (Timer.getFPGATimestamp() - lastSwitchTime > 1.0) {
                    state = FeederState.STOPPED
                }
            }
            FeederState.OUTTAKING -> {
                feederMotor.set(TalonSRXControlMode.PercentOutput, 1.0)
                gateSolenoid.set(DoubleSolenoid.Value.kReverse)

                if (Timer.getFPGATimestamp() - lastSwitchTime > 1.0) {
                    state = FeederState.STOPPED
                }
            }
        }
    }
    fun startIntaking(){
        if(state == FeederState.OUTTAKING) {
            return
        }
        state = FeederState.INTAKING
        lastSwitchTime = Timer.getFPGATimestamp()
    }

    fun startOuttaking(){
        state = FeederState.OUTTAKING
        lastSwitchTime = Timer.getFPGATimestamp()
    }
}
