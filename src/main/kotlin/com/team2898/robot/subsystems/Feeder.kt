package com.team2898.robot.subsystems

import com.ctre.phoenix.motorcontrol.TalonSRXControlMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.PneumaticsModuleType
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Feeder : SubsystemBase(){
    private val feederMotor = TalonSRX(5)
    private val fred = DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 1 )

    private var mode = FeederState.STOPPED
    enum class FeederState {
        STOPPED, INTAKING, OUTTAKING
    }
    var lastTime = 0.0
    override fun periodic() {
        when (mode) {
            FeederState.STOPPED -> {
                feederMotor.set(TalonSRXControlMode.PercentOutput, 0.0)
                fred.set(DoubleSolenoid.Value.kForward)
            }
            FeederState.INTAKING -> {
                feederMotor.set(TalonSRXControlMode.PercentOutput, 1.0)
                fred.set(DoubleSolenoid.Value.kForward)
                if(Timer.getFPGATimestamp() - lastTime > 1.0){
                    mode = FeederState.STOPPED
                }
            }

            FeederState.OUTTAKING -> {
                feederMotor.set(TalonSRXControlMode.PercentOutput, 1.0)
                fred.set(DoubleSolenoid.Value.kReverse)
                if(Timer.getFPGATimestamp() - lastTime > 1.0){
                    mode = FeederState.STOPPED
                }
            }

        }
    }
    fun startIntaking(){
        mode = FeederState.INTAKING
        lastTime = Timer.getFPGATimestamp()
    }
    fun startOuttaking(){
        mode = FeederState.OUTTAKING
        lastTime = Timer.getFPGATimestamp()
    }
}