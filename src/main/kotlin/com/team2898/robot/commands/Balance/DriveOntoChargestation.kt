package com.team2898.robot.commands.Balance

import com.bpsrobotics.engine.utils.`M/s`
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase

/**
 * Drives until the robot thinks it's on charge station (The pitch changes)
 * @property direction The direction in which to drive
 */
class DriveOntoChargestation(private val direction: DriveDirection) : CommandBase() {
    /** The pitch of the robot */
    private var pitch = Odometry.NavxHolder.navx.pitch.toDouble()
    /** Stops the robot if it doesn't tip within a timeframe */
    private val stopTimer = Timer()
    override fun initialize() {
        stopTimer.reset()
        stopTimer.start()
    }
    override fun execute() {
        // If timer hasn't elapsed one second, continue trying to drive onto charge station
        if(!stopTimer.hasElapsed(1.0)){
            Drivetrain.stupidDrive(`M/s`(2.0 * direction.multiplier), `M/s`(2.0 * direction.multiplier))
        } else{
            // If robot pitch hasn't changed yet, it is not infront of charge station, so stop running.
            Drivetrain.stupidDrive(`M/s`(0.0), `M/s`(0.0))
        }
    }
    override fun end(interrupted: Boolean) {
        //Stop robot
        Drivetrain.fullStop()
    }

    override fun isFinished(): Boolean {
        //If the pitch for each respective direction has been reached, stop.
        return when(direction) {
            DriveDirection.FORWARDS -> pitch > 5
            DriveDirection.BACKWARDS -> pitch < -5
        }
    }
}