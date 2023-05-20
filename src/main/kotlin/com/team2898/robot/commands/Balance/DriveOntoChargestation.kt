package com.team2898.robot.commands.Balance

import com.bpsrobotics.engine.utils.`M/s`
import com.bpsrobotics.engine.utils.MovingAverage
import com.team2898.robot.subsystems.Drivetrain
import com.team2898.robot.subsystems.Odometry
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.absoluteValue

/**
 * Drives until the robot thinks it's on charge station (The pitch changes)
 * @property direction The direction in which to drive
 */
class DriveOntoChargestation(private val direction: DriveDirection) : CommandBase() {
    /** The pitch of the robot */
    private val pitchAvg = MovingAverage(16)
    /** Stops the robot if it doesn't tip within a timeframe */
    private val stopTimer = Timer()
    override fun initialize() {
        stopTimer.reset()
        stopTimer.start()
    }
    override fun execute() {
        pitchAvg.add(Odometry.NavxHolder.navx.pitch.toDouble())
        // If timer hasn't elapsed one second, continue trying to drive onto charge station
        if(!stopTimer.hasElapsed(1.0)){
            Drivetrain.stupidDrive(`M/s`(1.0 * direction.multiplier), `M/s`(1.0 * direction.multiplier))
        } else {
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
        return pitchAvg.average.absoluteValue > 5.0
    }
}