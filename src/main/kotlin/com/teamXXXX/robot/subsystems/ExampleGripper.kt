package com.teamXXXX.robot.subsystems

import com.teamXXXX.robot.Constants
import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.SubsystemBase

/**
 * This is an example pneumatic gripper, designed to show you how to make a subsystem.
 * Has 2 pistons, actuated by 2 valves. The pistons are double action (can push & pull).
 */
object ExampleGripper : SubsystemBase() {
    private val solenoidOne = DoubleSolenoid(
            Constants.GRABBER_PISTON_ONE_FORWARD,
            Constants.GRABBER_PISTON_ONE_REVERSE
    )

    private val solenoidTwo = DoubleSolenoid(
            Constants.GRABBER_PISTON_TWO_FORWARD,
            Constants.GRABBER_PISTON_TWO_REVERSE
    )

    /**
     * This enum defines all of the possible gripper states
     */
    enum class GripperState {
        INIT, GRABBING, RELEASING, STOP
    }

    /** Initializes the gripper state to send to the SmartDashboard */
    override fun initSendable(builder: SendableBuilder?) {
        super.initSendable(builder)
        builder?.addStringProperty("gripper-state", { gripperState.name }, null)
    }

    /**
     * Gripper state reflects the current gripper state.
     * Set to INIT at start up. See periodic below.
     */
    private var gripperState = GripperState.INIT

    /**
     * Grab will close the gripper by opening the two solenoid valves.
     * This also sets the gripper state variable to GRABBING.
     */
    fun grab() {
        solenoidOne.set(DoubleSolenoid.Value.kForward)
        solenoidTwo.set(DoubleSolenoid.Value.kReverse)
        gripperState = GripperState.GRABBING

    }

    /**
     * Release will open the gripper by opening the two solenoid valves.
     * This also sets the gripper state variable to RELEASING.
     */
    fun release() {
        solenoidOne.set(DoubleSolenoid.Value.kReverse)
        solenoidTwo.set(DoubleSolenoid.Value.kForward)
        gripperState = GripperState.RELEASING
    }

    /**
     * Stop will stop the gripper by closing the two solenoid valves.
     * This also sets the gripper state variable to STOP.
     */
    fun stop() {
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