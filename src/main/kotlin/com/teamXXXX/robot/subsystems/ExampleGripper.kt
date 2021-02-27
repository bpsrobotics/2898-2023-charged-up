package com.teamXXXX.robot.subsystems

import com.teamXXXX.robot.Constants
import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj2.command.SubsystemBase

/**
 * This is an example pneumatic gripper, designed to show you how to make a subsystem
 * Has 2 pistons, that are actuated by 2 valves. The pistons are double action (can push & pull)
 */

object ExampleGripper:SubsystemBase (){
    val solenoidOne = DoubleSolenoid(
            Constants.GRABBER_PISTON_ONE_FORWARD,
            Constants.GRABBER_PISTON_ONE_REVERSE)

    val solenoidTwo = DoubleSolenoid(
            Constants.GRABBER_PISTON_TWO_FORWARD,
            Constants.GRABBER_PISTON_TWO_REVERSE)

    enum class GripperState{
        INIT, GRABBING, RELEASING, STOP
    }

    var gripperState = GripperState.INIT


    fun grab(){
        solenoidOne.set(DoubleSolenoid.Value.kForward)
        solenoidTwo.set(DoubleSolenoid.Value.kReverse)
        gripperState = GripperState.GRABBING
    }

    fun release(){
        solenoidOne.set(DoubleSolenoid.Value.kReverse)
        solenoidTwo.set(DoubleSolenoid.Value.kForward)
        gripperState = GripperState.RELEASING
    }

    fun stop(){
        solenoidOne.set(DoubleSolenoid.Value.kOff)
        solenoidTwo.set(DoubleSolenoid.Value.kOff)
        gripperState = GripperState.STOP
    }

    /**
     * The periodic function will run once everything is set up, and stop the gripper on setup
     */
    override fun periodic() {
        if(gripperState == GripperState.INIT){
            stop()
        }
    }

}